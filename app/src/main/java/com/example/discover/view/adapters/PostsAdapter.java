package com.example.discover.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discover.R;
import com.example.discover.databinding.PostsDashboardRvDesignBinding;
import com.example.discover.model.NotificationsModel;
import com.example.discover.model.PostModel;
import com.example.discover.model.UserModel;
import com.example.discover.view.activity.CommentActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

/**
 * Adapter class for displaying posts in a RecyclerView.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final ArrayList<PostModel> list;
    private final Context context;

    /**
     * Constructor for the PostsAdapter.
     *
     * @param list    The list of posts to be displayed.
     * @param context The context in which the adapter is used.
     */
    public PostsAdapter(ArrayList<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.posts_dashboard_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostModel postModel = list.get(position);
        bindPostData(holder, postModel);
        setupUserDetails(holder, postModel);
        setupLikeFeature(holder, postModel);
        setupCommentClickListeners(holder, postModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Binds post data to the ViewHolder.
     *
     * @param holder     The ViewHolder to bind data to.
     * @param postModel  The post data to be displayed.
     */
    private void bindPostData(@NonNull ViewHolder holder, PostModel postModel) {
        Picasso.get().load(postModel.getmPostImage()).into(holder.mBinding.userPostHomePageImageView);
        holder.mBinding.postDescriptionHomePageTextView.setText(postModel.getmPostDesc());
        holder.mBinding.likesCountTextView.setText(String.valueOf(postModel.getmPostLikes()));
        holder.mBinding.commentsCount.setText(String.valueOf(postModel.getmCommentsCount()));
        holder.mBinding.postDescriptionHomePageTextView.setVisibility(postModel.getmPostDesc().isEmpty() ? View.GONE : View.VISIBLE);
    }

    /**
     * Sets up user details in the ViewHolder.
     *
     * @param holder     The ViewHolder to set up.
     * @param postModel  The post data which includes the user ID.
     */
    private void setupUserDetails(@NonNull ViewHolder holder, PostModel postModel) {
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(postModel.getmPostedBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel != null) {
                                Picasso.get().load(userModel.getMProfilePhoto()).into(holder.mBinding.userProfilePicPostProfilePageImageView);
                                holder.mBinding.userNameHomeDashBoardTextview.setText(userModel.getmName());
                                holder.mBinding.userDesignationHomeDashBoardTextView.setText(userModel.getmProfession());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors
                    }
                });
    }

    /**
     * Sets up the like feature of the post.
     *
     * @param holder     The ViewHolder to set up.
     * @param postModel  The post data which includes the like information.
     */
    private void setupLikeFeature(@NonNull ViewHolder holder, PostModel postModel) {
        String userId = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("posts").child(postModel.getmPostId())
                .child("likes").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.mBinding.likePostImageView.setImageResource(R.drawable.liked);
                        } else {
                            holder.mBinding.likePostImageView.setImageResource(R.drawable.not_liked);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors
                    }
                });

        holder.mBinding.likePostImageView.setOnClickListener(v -> handleLikeClick(holder, postModel));
    }

    /**
     * Handles the like button click event.
     *
     * @param holder     The ViewHolder containing the like button.
     * @param postModel  The post data to update.
     */
    private void handleLikeClick(@NonNull ViewHolder holder, PostModel postModel) {
        String userId = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String postId = postModel.getmPostId();

        database.getReference().child("posts").child(postId).child("likes").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            // Add the like to database
                            database.getReference().child("posts").child(postId).child("likes").child(userId)
                                    .setValue(true)
                                    .addOnSuccessListener(unused -> {
                                        database.getReference().child("posts").child(postId).child("mPostLikes")
                                                .setValue(postModel.getmPostLikes() + 1)
                                                .addOnSuccessListener(unused1 -> {
                                                    holder.mBinding.likePostImageView.setImageResource(R.drawable.liked);
                                                    sendLikeNotification(postModel);
                                                });
                                    });
                        } else {
                            // Remove the like from database
                            database.getReference().child("posts").child(postId).child("likes").child(userId)
                                    .removeValue()
                                    .addOnSuccessListener(unused -> {
                                        database.getReference().child("posts").child(postId).child("mPostLikes")
                                                .setValue(postModel.getmPostLikes() - 1)
                                                .addOnSuccessListener(unused1 -> {
                                                    holder.mBinding.likePostImageView.setImageResource(R.drawable.not_liked);
                                                });
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors
                    }
                });
    }

    /**
     * Sends a like notification to the post author.
     *
     * @param postModel The post data for which the notification is to be sent.
     */
    private void sendLikeNotification(PostModel postModel) {
        NotificationsModel notificationsModel = new NotificationsModel();
        notificationsModel.setmNotificationAt(new Date().getTime());
        notificationsModel.setmNotificationBy(FirebaseAuth.getInstance().getUid());
        notificationsModel.setmPostedBy(postModel.getmPostedBy());
        notificationsModel.setmPostId(postModel.getmPostId());
        notificationsModel.setmType("like");

        FirebaseDatabase.getInstance().getReference().child("notifications")
                .child(postModel.getmPostedBy())
                .push()
                .setValue(notificationsModel);
    }

    /**
     * Sets up click listeners for comment actions.
     *
     * @param holder     The ViewHolder containing comment views.
     * @param postModel  The post data associated with the comments.
     */
    private void setupCommentClickListeners(@NonNull ViewHolder holder, PostModel postModel) {
        View.OnClickListener commentClickListener = v -> handleOnClick(postModel);

        holder.mBinding.commentPostImageView.setOnClickListener(commentClickListener);
        holder.mBinding.viewCommentsTextView.setOnClickListener(commentClickListener);
        holder.mBinding.viewCommentsTextView1.setOnClickListener(commentClickListener);
    }

    /**
     * Starts the CommentActivity for the given post.
     *
     * @param postModel The post data to be passed to the CommentActivity.
     */
    private void handleOnClick(PostModel postModel) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("mPostId", postModel.getmPostId());
        intent.putExtra("mPostedBy", postModel.getmPostedBy());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * ViewHolder class for holding post views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final PostsDashboardRvDesignBinding mBinding;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view to be held by the ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = PostsDashboardRvDesignBinding.bind(itemView);
        }
    }
}
