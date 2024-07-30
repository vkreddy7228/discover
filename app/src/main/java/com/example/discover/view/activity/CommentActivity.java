package com.example.discover.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.discover.R;
import com.example.discover.databinding.ActivityCommentBinding;
import com.example.discover.model.CommentsModel;
import com.example.discover.model.NotificationsModel;
import com.example.discover.model.PostModel;
import com.example.discover.model.UserModel;
import com.example.discover.view.adapters.CommentsAdapter;
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
 * Activity to display and manage comments for a specific post.
 */
public class CommentActivity extends AppCompatActivity {

    private ActivityCommentBinding mBinding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;
    private Intent intent;
    private String mPostId;
    private String mPostedBy;
    private ArrayList<CommentsModel> commentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();

        // Retrieve post ID and the user who posted
        intent = getIntent();
        mPostId = intent.getStringExtra("mPostId");
        mPostedBy = intent.getStringExtra("mPostedBy");

        // Load post details from Firebase
        mDataBase.getReference().child("posts").child(mPostId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    PostModel postModel = snapshot.getValue(PostModel.class);
                    // Load post image
                    Picasso.get().load(postModel.getmPostImage()).into(mBinding.commentsPagePostImageView);
                    mBinding.likesCountTextView.setText(String.valueOf(postModel.getmPostLikes()));

                    // Set post description if available
                    if (!postModel.getmPostDesc().isEmpty()) {
                        mBinding.postDescriptionHomePageTextView.setText(postModel.getmPostDesc());
                    } else {
                        mBinding.postDescriptionHomePageTextView.setVisibility(View.GONE);
                    }

                    // Check if the user has liked the post
                    mDataBase.getReference().child("posts").child(mPostId)
                            .child("likes").child(mAuth.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        mBinding.likePostImageView.setImageResource(R.drawable.liked);
                                    } else {
                                        mBinding.likePostImageView.setImageResource(R.drawable.not_liked);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle possible errors
                                }
                            });

                    // Load user details
                    mDataBase.getReference().child("users").child(postModel.getmPostedBy())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        UserModel userModel = snapshot.getValue(UserModel.class);
                                        Picasso.get().load(userModel.getMProfilePhoto()).into(mBinding.userProfileCommentsPageImageView);
                                        mBinding.userNameCommentsPage.setText(userModel.getmName());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle possible errors
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });

        // Initialize comments list and adapter
        commentsList = new ArrayList<>();
        CommentsAdapter commentsAdapter = new CommentsAdapter(this, commentsList);
        LinearLayoutManager linearLayoutManagerPosts = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.commentsRecyclerView.setLayoutManager(linearLayoutManagerPosts);
        mBinding.commentsRecyclerView.setNestedScrollingEnabled(true);
        mBinding.commentsRecyclerView.setAdapter(commentsAdapter);

        // Fetch comments from Firebase
        mDataBase.getReference().child("posts").child(mPostId)
                .child("comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentsList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                CommentsModel commentsModel = dataSnapshot.getValue(CommentsModel.class);
                                commentsList.add(commentsModel);
                            }
                            commentsAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle possible errors
                    }
                });
    }

    /**
     * Handles posting a comment to the database.
     *
     * @param view The view that was clicked.
     */
    public void postComment(View view) {
        CommentsModel commentsModel = new CommentsModel();
        commentsModel.setmComment(mBinding.writeACommentEditText.getText().toString());
        commentsModel.setmCommentedBy(mAuth.getUid());
        commentsModel.setmCommentedAt(new Date().getTime());

        mDataBase.getReference().child("posts")
                .child(mPostId)
                .child("comments")
                .push()
                .setValue(commentsModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Update comment count
                        mDataBase.getReference().child("posts")
                                .child(mPostId)
                                .child("mCommentsCount")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int commentCount = 0;
                                        if (snapshot.exists()) {
                                            commentCount = snapshot.getValue(Integer.class);
                                        }
                                        mDataBase.getReference().child("posts")
                                                .child(mPostId)
                                                .child("mCommentsCount")
                                                .setValue(commentCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();
                                                        mBinding.writeACommentEditText.getText().clear();

                                                        // Send notification for the comment
                                                        NotificationsModel notificationsModel = new NotificationsModel();
                                                        notificationsModel.setmNotificationAt(new Date().getTime());
                                                        notificationsModel.setmNotificationBy(mAuth.getUid());
                                                        notificationsModel.setmPostedBy(mPostedBy);
                                                        notificationsModel.setmPostId(mPostId);
                                                        notificationsModel.setmType("comment");

                                                        FirebaseDatabase.getInstance().getReference().child("notifications")
                                                                .child(mPostedBy)
                                                                .push()
                                                                .setValue(notificationsModel);
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle possible errors
                                    }
                                });
                    }
                });
    }
}
