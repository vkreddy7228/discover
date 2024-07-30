package com.example.discover.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discover.R;
import com.example.discover.databinding.FollowingRvDesignBinding;
import com.example.discover.model.FollowingModel;
import com.example.discover.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter class for displaying users that a user is following in a RecyclerView.
 */
public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {

    private final ArrayList<FollowingModel> list;
    private final Context context;

    /**
     * Constructor for the FollowingAdapter.
     *
     * @param list    The list of users that the user is following.
     * @param context The context in which the adapter is used.
     */
    public FollowingAdapter(ArrayList<FollowingModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.following_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowingModel followingModel = list.get(position);
        bindFollowingUser(holder, followingModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Binds the user data to the ViewHolder.
     *
     * @param holder         The ViewHolder to bind data to.
     * @param followingModel The user data to be displayed.
     */
    private void bindFollowingUser(@NonNull ViewHolder holder, FollowingModel followingModel) {
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(followingModel.getmFollowing())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            Picasso.get()
                                    .load(userModel.getMProfilePhoto())
                                    .placeholder(R.drawable.add_profile_place_holder)
                                    .into(holder.mBinding.followingUserProfileSearchPageImageView);
                            holder.mBinding.followingUserNameSearchPageTextView.setText(userModel.getmName());
                            holder.mBinding.followingUserProfessionSearchPageTextView.setText(userModel.getmProfession());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database errors if necessary
                    }
                });
    }

    /**
     * ViewHolder class for holding following user views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final FollowingRvDesignBinding mBinding;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view to be held by the ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = FollowingRvDesignBinding.bind(itemView);
        }
    }
}
