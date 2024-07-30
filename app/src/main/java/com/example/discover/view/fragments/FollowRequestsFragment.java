package com.example.discover.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discover.R;
import com.example.discover.model.FollowRequestsModel;
import com.example.discover.view.adapters.FollowRequestsAdapter;

import java.util.ArrayList;

/**
 * Fragment to display a list of follow requests received by the user.
 */
public class FollowRequestsFragment extends Fragment {

    private RecyclerView followRequestsRecyclerView;
    private ArrayList<FollowRequestsModel> followRequestsModelList;

    /**
     * Default constructor required for fragment instantiation.
     */
    public FollowRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization logic, if needed
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow_requests, container, false);

        // Initialize RecyclerView and data list
        initializeViews(view);
        populateFollowRequestsList();
        setupRecyclerView();

        return view;
    }

    /**
     * Initializes views by binding them to their respective IDs.
     *
     * @param view The root view of the fragment.
     */
    private void initializeViews(View view) {
        followRequestsRecyclerView = view.findViewById(R.id.followRequestsRecyclerView);
        followRequestsModelList = new ArrayList<>();
    }

    /**
     * Populates the follow requests list with dummy data.
     */
    private void populateFollowRequestsList() {
        for (int i = 0; i < 15; i++) {
            followRequestsModelList.add(new FollowRequestsModel(R.drawable.megha_vamsi, "Meghana Jakkuva has requested to follow you"));
        }
    }

    /**
     * Sets up the RecyclerView with an adapter and layout manager.
     */
    private void setupRecyclerView() {
        FollowRequestsAdapter followRequestsAdapter = new FollowRequestsAdapter(followRequestsModelList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        followRequestsRecyclerView.setLayoutManager(linearLayoutManager);
        followRequestsRecyclerView.setNestedScrollingEnabled(false);
        followRequestsRecyclerView.setAdapter(followRequestsAdapter);
    }
}
