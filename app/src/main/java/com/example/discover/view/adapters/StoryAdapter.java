package com.example.discover.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discover.R;
import com.example.discover.databinding.StorylistRvDesignBinding;
import com.example.discover.model.StoryModel;
import com.example.discover.model.UserModel;
import com.example.discover.model.UserStories;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

/**
 * Adapter for displaying a list of stories in a RecyclerView.
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private final ArrayList<StoryModel> list;
    private final Context context;

    /**
     * Constructor for StoryAdapter.
     *
     * @param list    The list of StoryModel objects to be displayed.
     * @param context The context in which the adapter is running.
     */
    public StoryAdapter(ArrayList<StoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.storylist_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoryModel storyModel = list.get(position);
        if (storyModel != null) {
            holder.mBinding.statusCircle.setPortionsCount(storyModel.getmUserStoriesList().size());

            FirebaseDatabase.getInstance().getReference().child("users")
                    .child(storyModel.getmStoryBy())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel != null) {
                                Picasso.get()
                                        .load(userModel.getMProfilePhoto())
                                        .into(holder.mBinding.userProfilePicPostProfilePageImageView);

                                String name = userModel.getmName();
                                if (name.length() > 8) {
                                    name = name.substring(0, 7) + "..";
                                }
                                holder.mBinding.userNameStoryHomePageTextView.setText(name);

                                holder.mBinding.userProfilePicPostProfilePageImageView.setOnClickListener(v -> {
                                    ArrayList<MyStory> myStories = new ArrayList<>();
                                    for (UserStories stories : storyModel.getmUserStoriesList()) {
                                        myStories.add(new MyStory(
                                                stories.getmStoryImage(),
                                                new Date(stories.getmStoryAt())
                                        ));
                                    }

                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories)
                                            .setStoryDuration(5000) // Duration in Millis
                                            .setTitleText(userModel.getmName())
                                            .setSubtitleText(userModel.getmProfession())
                                            .setTitleLogoUrl(userModel.getMProfilePhoto())
                                            .setStoryClickListeners(new StoryClickListeners() {
                                                @Override
                                                public void onDescriptionClickListener(int position) {
                                                    // Your action
                                                }

                                                @Override
                                                public void onTitleIconClickListener(int position) {
                                                    // Your action
                                                }
                                            })
                                            .build()
                                            .show();
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * ViewHolder class for the StoryAdapter.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final StorylistRvDesignBinding mBinding;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view for this ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = StorylistRvDesignBinding.bind(itemView);
        }
    }
}
