package com.example.discover.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discover.R;
import com.example.discover.databinding.CommentsRvDesignBinding;
import com.example.discover.model.CommentsModel;
import com.example.discover.model.UserModel;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter class for displaying comments in a RecyclerView.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<CommentsModel> list;

    /**
     * Constructor for the CommentsAdapter.
     *
     * @param context The context in which the adapter is used.
     * @param list    The list of comments to be displayed.
     */
    public CommentsAdapter(Context context, ArrayList<CommentsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comments_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentsModel commentsModel = list.get(position);
        bindComment(holder, commentsModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Binds the comment data to the ViewHolder.
     *
     * @param holder        The ViewHolder to bind data to.
     * @param commentsModel The comment data to be displayed.
     */
    private void bindComment(@NonNull ViewHolder holder, CommentsModel commentsModel) {
        holder.mBinding.commentBodyTextView.setText(commentsModel.getmComment());
        String timeText = TimeAgo.using(commentsModel.getmCommentedAt());
        holder.mBinding.timeOfComment.setText(timeText);

        // Fetch user details and update UI
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(commentsModel.getmCommentedBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel != null) {
                                holder.mBinding.commentedUserNameTextView.setText(userModel.getmName());
                                Picasso.get().load(userModel.getMProfilePhoto()).into(holder.mBinding.commentedUserProfileImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database errors if necessary
                    }
                });
    }

    /**
     * ViewHolder class for holding comment views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final CommentsRvDesignBinding mBinding;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view to be held by the ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = CommentsRvDesignBinding.bind(itemView);
        }
    }
}
