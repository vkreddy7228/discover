package com.example.discover.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discover.R;
import com.example.discover.databinding.FollowersRvDesignBinding;
import com.example.discover.model.FollowersModel;
import com.example.discover.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter class for displaying followers in a RecyclerView.
 */
public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {

    private final ArrayList<FollowersModel> list;
    private final Context context;

    /**
     * Constructor for the FollowersAdapter.
     *
     * @param list    The list of followers to be displayed.
     * @param context The context in which the adapter is used.
     */
    public FollowersAdapter(ArrayList<FollowersModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.followers_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowersModel followersModel = list.get(position);
        bindFollower(holder, followersModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Binds the follower data to the ViewHolder.
     *
     * @param holder           The ViewHolder to bind data to.
     * @param followersModel   The follower data to be displayed.
     */
    private void bindFollower(@NonNull ViewHolder holder, FollowersModel followersModel) {
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(followersModel.getMFollowedBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            Picasso.get()
                                    .load(userModel.getMProfilePhoto())
                                    .placeholder(R.drawable.add_profile_place_holder)
                                    .into(holder.mBinding.followersUserProfileFollowersPageImageView);
                            holder.mBinding.followersUserNameFollowersPageTextView.setText(userModel.getmName());
                            holder.mBinding.followersUserProfessionFollowersPageTextView.setText(userModel.getmProfession());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database errors if necessary
                    }
                });
    }

    /**
     * ViewHolder class for holding follower views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final FollowersRvDesignBinding mBinding;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view to be held by the ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = FollowersRvDesignBinding.bind(itemView);
        }
    }
}
