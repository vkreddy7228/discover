package com.example.discover.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.discover.databinding.FragmentFollowersBinding;
import com.example.discover.model.FollowersModel;
import com.example.discover.view.adapters.FollowersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * Fragment to display a list of followers for the current user.
 */
public class FollowersFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;
    private FirebaseStorage mFirebaseStorage;
    private FragmentFollowersBinding mBinding;
    private ArrayList<FollowersModel> mFollowersList;

    /**
     * Default constructor required for fragment instantiation.
     */
    public FollowersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase instances and followers list
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFollowersList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentFollowersBinding.inflate(inflater, container, false);

        // Set up RecyclerView with adapter and layout manager
        setupRecyclerView();

        // Load followers data from Firebase
        loadFollowersData();

        return mBinding.getRoot();
    }

    /**
     * Sets up the RecyclerView with its adapter and layout manager.
     */
    private void setupRecyclerView() {
        FollowersAdapter followersAdapter = new FollowersAdapter(mFollowersList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.followersRecyclerView.setLayoutManager(linearLayoutManager);
        mBinding.followersRecyclerView.setNestedScrollingEnabled(false);
        mBinding.followersRecyclerView.setAdapter(followersAdapter);
    }

    /**
     * Loads followers data from Firebase and updates the RecyclerView.
     */
    private void loadFollowersData() {
        mDataBase.getReference().child("users").child(mAuth.getUid())
                .child("followers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            mFollowersList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                FollowersModel followersModel = dataSnapshot.getValue(FollowersModel.class);
                                if (followersModel != null) {
                                    mFollowersList.add(followersModel);
                                }
                            }
                            // Notify the adapter that the data has changed
                            ((FollowersAdapter) mBinding.followersRecyclerView.getAdapter()).notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors here
                    }
                });
    }
}
