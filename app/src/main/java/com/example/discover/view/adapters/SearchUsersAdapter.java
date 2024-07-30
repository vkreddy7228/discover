package com.example.discover.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.discover.R;
import com.example.discover.databinding.SearchFragmentRvDesignBinding;
import com.example.discover.model.FollowersModel;
import com.example.discover.model.FollowingModel;
import com.example.discover.model.NotificationsModel;
import com.example.discover.model.UserModel;
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
 * Adapter for displaying a list of users in a RecyclerView.
 */
public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<UserModel> list;

    /**
     * Constructor for SearchUsersAdapter.
     *
     * @param context The context in which the adapter is running.
     * @param list    The list of UserModel objects to be displayed.
     */
    public SearchUsersAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    /**
     * Updates the list of users and notifies the adapter of the change.
     *
     * @param filteredList The updated list of users.
     */
    public void setFilteredList(ArrayList<UserModel> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_fragment_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel userModel = list.get(position);
        bindUserData(holder, userModel);
        checkIfFollowing(userModel, holder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Binds user data to the ViewHolder.
     *
     * @param holder     The ViewHolder to bind data to.
     * @param userModel  The user data to be displayed.
     */
    private void bindUserData(@NonNull ViewHolder holder, UserModel userModel) {
        Picasso.get()
                .load(userModel.getMProfilePhoto())
                .placeholder(R.drawable.add_profile_place_holder)
                .into(holder.mBinding.userProfileSearchPageImageView);

        holder.mBinding.userNameSearchPageTextView.setText(userModel.getmName());
        holder.mBinding.userProfessionSearchPageTextView.setText(userModel.getmProfession());
    }

    /**
     * Checks if the current user is following the specified user and updates the UI accordingly.
     *
     * @param userModel The user to check if the current user is following.
     * @param holder    The ViewHolder for the current user item.
     */
    private void checkIfFollowing(UserModel userModel, ViewHolder holder) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userModel.getmUserId())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isFollowing = snapshot.exists();
                        updateFollowButton(holder, isFollowing);
                        setupFollowButtonClickListener(userModel, holder, isFollowing);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors
                    }
                });
    }

    /**
     * Updates the follow button UI based on whether the user is being followed.
     *
     * @param holder     The ViewHolder for the current user item.
     * @param isFollowing Whether the current user is following the specified user.
     */
    private void updateFollowButton(ViewHolder holder, boolean isFollowing) {
        if (isFollowing) {
            holder.mBinding.followButtonSearchPage.setText("Following");
            holder.mBinding.followButtonSearchPage.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.mBinding.followButtonSearchPage.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_view_divider));
        } else {
            holder.mBinding.followButtonSearchPage.setText("Follow");
            holder.mBinding.followButtonSearchPage.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.mBinding.followButtonSearchPage.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200));
        }
    }

    /**
     * Sets up the click listener for the follow button.
     *
     * @param userModel   The user model representing the user being followed/unfollowed.
     * @param holder      The ViewHolder for the current user item.
     * @param isFollowing Whether the current user is following the specified user.
     */
    private void setupFollowButtonClickListener(UserModel userModel, ViewHolder holder, boolean isFollowing) {
        holder.mBinding.followButtonSearchPage.setOnClickListener(v -> {
            if (isFollowing) {
                unfollowUser(userModel);
            } else {
                followUser(userModel);
            }
        });
    }

    /**
     * Handles the logic for following a user.
     *
     * @param userModel The user model representing the user to follow.
     */
    private void followUser(UserModel userModel) {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        FollowersModel followersModel = new FollowersModel();
        followersModel.setMFollowedBy(currentUserId);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userModel.getmUserId())
                .child("followers")
                .child(currentUserId)
                .setValue(followersModel)
                .addOnSuccessListener(unused -> {
                    incrementFollowerCount(userModel);
                    incrementFollowingCount(currentUserId, userModel.getmUserId());
                    sendFollowNotification(userModel);
                });
    }

    /**
     * Handles the logic for unfollowing a user.
     *
     * @param userModel The user model representing the user to unfollow.
     */
    private void unfollowUser(UserModel userModel) {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        decrementFollowingCount(currentUserId, userModel.getmUserId());

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("following")
                .child(userModel.getmUserId())
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userModel.getmUserId())
                .child("followers")
                .child(currentUserId)
                .removeValue()
                .addOnSuccessListener(unused -> decrementFollowerCount(userModel));
    }

    /**
     * Increments the follower count of the specified user.
     *
     * @param userModel The user model representing the user whose follower count is to be incremented.
     */
    private void incrementFollowerCount(UserModel userModel) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userModel.getmUserId())
                .child("mFollowersCount")
                .setValue(userModel.getmFollowersCount() + 1);
    }

    /**
     * Decrements the follower count of the specified user.
     *
     * @param userModel The user model representing the user whose follower count is to be decremented.
     */
    private void decrementFollowerCount(UserModel userModel) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userModel.getmUserId())
                .child("mFollowersCount")
                .setValue(userModel.getmFollowersCount() - 1);
    }

    /**
     * Increments the following count of the logged-in user.
     *
     * @param currentUserId The ID of the logged-in user.
     * @param followedUserId The ID of the user being followed.
     */
    private void incrementFollowingCount(String currentUserId, String followedUserId) {
        FollowingModel followingModel = new FollowingModel();
        followingModel.setmFollowing(followedUserId);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("following")
                .child(followedUserId)
                .setValue(followingModel);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel != null) {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(currentUserId)
                                        .child("mFollowingCount")
                                        .setValue(userModel.getmFollowingCount() + 1);
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
     * Decrements the following count of the logged-in user.
     *
     * @param currentUserId The ID of the logged-in user.
     * @param followedUserId The ID of the user being unfollowed.
     */
    private void decrementFollowingCount(String currentUserId, String followedUserId) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel != null) {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(currentUserId)
                                        .child("mFollowingCount")
                                        .setValue(userModel.getmFollowingCount() - 1);
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
     * Sends a follow notification to the specified user.
     *
     * @param userModel The user model representing the user to send the notification to.
     */
    private void sendFollowNotification(UserModel userModel) {
        NotificationsModel notificationsModel = new NotificationsModel();
        notificationsModel.setmNotificationAt(new Date().getTime());
        notificationsModel.setmNotificationBy(FirebaseAuth.getInstance().getUid());
        notificationsModel.setmType("follow");

        FirebaseDatabase.getInstance().getReference()
                .child("notifications")
                .child(userModel.getmUserId())
                .push()
                .setValue(notificationsModel);
    }

    /**
     * ViewHolder class for the SearchUsersAdapter.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        final SearchFragmentRvDesignBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = SearchFragmentRvDesignBinding.bind(itemView);
        }
    }
}
