package com.example.discover.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.discover.databinding.FragmentFollowingBinding;
import com.example.discover.model.FollowingModel;
import com.example.discover.view.adapters.FollowingAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * Fragment to display a list of users that the current user is following.
 */
public class FollowingFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;
    private FirebaseStorage mFirebaseStorage;

    private FragmentFollowingBinding mBinding;
    private ArrayList<FollowingModel> mFollowingList;

    /**
     * Default constructor required for fragment instantiation.
     */
    public FollowingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase instances and following list
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFollowingList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentFollowingBinding.inflate(inflater, container, false);

        // Set up RecyclerView with adapter and layout manager
        setupRecyclerView();

        // Load following data from Firebase
        loadFollowingData();

        return mBinding.getRoot();
    }

    /**
     * Sets up the RecyclerView with its adapter and layout manager.
     */
    private void setupRecyclerView() {
        FollowingAdapter followingAdapter = new FollowingAdapter(mFollowingList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.followingRecyclerView.setLayoutManager(linearLayoutManager);
        mBinding.followingRecyclerView.setNestedScrollingEnabled(false);
        mBinding.followingRecyclerView.setAdapter(followingAdapter);
    }

    /**
     * Loads following data from Firebase and updates the RecyclerView.
     */
    private void loadFollowingData() {
        mDataBase.getReference().child("users").child(mAuth.getUid())
                .child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            mFollowingList.clear(); // Clear the list before adding new data
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                FollowingModel followingModel = dataSnapshot.getValue(FollowingModel.class);
                                if (followingModel != null) {
                                    mFollowingList.add(followingModel);
                                }
                            }
                            // Notify the adapter that the data has changed
                            ((FollowingAdapter) mBinding.followingRecyclerView.getAdapter()).notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors here
                    }
                });
    }
}
