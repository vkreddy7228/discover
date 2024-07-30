package com.example.discover.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discover.R;
import com.example.discover.databinding.NotificationsRvDesignBinding;
import com.example.discover.model.NotificationsModel;
import com.example.discover.model.PostModel;
import com.example.discover.model.UserModel;
import com.example.discover.view.activity.CommentActivity;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter class for displaying notifications in a RecyclerView.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private final ArrayList<NotificationsModel> list;
    private final Context context;

    /**
     * Constructor for the NotificationsAdapter.
     *
     * @param list    The list of notifications to be displayed.
     * @param context The context in which the adapter is used.
     */
    public NotificationsAdapter(ArrayList<NotificationsModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notifications_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationsModel model = list.get(position);
        bindNotification(holder, model);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Binds notification data to the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param model  The notification data to be displayed.
     */
    private void bindNotification(@NonNull ViewHolder holder, NotificationsModel model) {
        String type = model.getmType();

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(model.getmNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            Picasso.get().load(userModel.getMProfilePhoto()).into(holder.mBinding.userProfileNotificationsPageImageView);
                            String text = TimeAgo.using(model.getmNotificationAt());
                            holder.mBinding.notificationMessageNotificationsPageTextView.setText(
                                    Html.fromHtml(getNotificationText(type, userModel.getmName(), text))
                            );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors
                    }
                });

        if (!type.equals("follow")) {
            FirebaseDatabase.getInstance().getReference().child("posts")
                    .child(model.getmPostId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                PostModel postModel = snapshot.getValue(PostModel.class);
                                if (postModel != null) {
                                    Picasso.get().load(postModel.getmPostImage()).into(holder.mBinding.userLikedPostImageView);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                        }
                    });
        }

        setupNotificationClick(holder, model, type);
        updateNotificationBackground(holder, model);
    }

    /**
     * Generates the notification text based on the type.
     *
     * @param type    The type of notification.
     * @param name    The name of the user who triggered the notification.
     * @param timeAgo The time ago string.
     * @return The formatted notification text.
     */
    private String getNotificationText(String type, String name, String timeAgo) {
        switch (type) {
            case "follow":
                return "<b>" + name + "</b> started following you.<br>" + timeAgo;
            case "like":
                return "<b>" + name + "</b> liked your post.<br>" + timeAgo;
            case "comment":
                return "<b>" + name + "</b> commented on your post.<br>" + timeAgo;
            default:
                return "";
        }
    }

    /**
     * Sets up the click listener for the notification.
     *
     * @param holder The ViewHolder holding the notification view.
     * @param model  The notification data.
     * @param type   The type of notification.
     */
    private void setupNotificationClick(@NonNull ViewHolder holder, NotificationsModel model, String type) {
        holder.mBinding.openNotification.setOnClickListener(v -> {
            if (!type.equals("follow")) {
                holder.mBinding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("mPostId", model.getmPostId());
                intent.putExtra("mPostedBy", model.getmPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                FirebaseDatabase.getInstance().getReference().child("notifications")
                        .child(model.getmPostedBy())
                        .child(model.getmNotificationId())
                        .child("mOpened")
                        .setValue(true);
            }
        });
    }

    /**
     * Updates the background color of the notification based on whether it has been opened.
     *
     * @param holder The ViewHolder holding the notification view.
     * @param model  The notification data.
     */
    private void updateNotificationBackground(@NonNull ViewHolder holder, NotificationsModel model) {
        if (model.ismOpened()) {
            holder.mBinding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    /**
     * ViewHolder class for holding notification views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final NotificationsRvDesignBinding mBinding;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view to be held by the ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = NotificationsRvDesignBinding.bind(itemView);
        }
    }
}
