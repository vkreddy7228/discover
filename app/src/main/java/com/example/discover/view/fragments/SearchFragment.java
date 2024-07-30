package com.example.discover.view.fragments;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import com.example.discover.R;
import com.example.discover.databinding.FragmentSearchBinding;
import com.example.discover.model.UserModel;
import com.example.discover.view.adapters.SearchUsersAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * A fragment that displays a searchable list of users.
 * It allows users to search and filter other users by their names.
 */
public class SearchFragment extends Fragment {

    private ArrayList<UserModel> mUserModelList;
    private FragmentSearchBinding mBinding;
    private SearchUsersAdapter searchUsersAdapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserModelList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSearchBinding.inflate(inflater, container, false);

        // Initialize and configure RecyclerView and Adapter
        setupRecyclerView();

        // Load and display users from Firebase Database
        loadUsersFromDatabase();

        // Configure search functionality
        configureSearchView();

        // Handle back press to navigate to HomePostsFragment
        handleOnBackPressed();

        return mBinding.getRoot();
    }

    /**
     * Initializes and configures the RecyclerView and its adapter.
     */
    private void setupRecyclerView() {
        searchUsersAdapter = new SearchUsersAdapter(getContext(), mUserModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.usersListRecyclerView.setLayoutManager(linearLayoutManager);
        mBinding.usersListRecyclerView.setNestedScrollingEnabled(false);
        mBinding.usersListRecyclerView.setAdapter(searchUsersAdapter);
    }

    /**
     * Loads the list of users from Firebase Database and updates the RecyclerView adapter.
     */
    private void loadUsersFromDatabase() {
        mDataBase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mUserModelList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (!dataSnapshot.getKey().equals(mAuth.getUid())) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            if (userModel != null) {
                                userModel.setmUserId(dataSnapshot.getKey());
                                mUserModelList.add(userModel);
                            }
                        }
                    }
                    searchUsersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    /**
     * Configures the SearchView to filter the list of users based on the query text.
     */
    private void configureSearchView() {
        mBinding.searchViewSearchPage.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    /**
     * Filters the list of users based on the query text and updates the adapter.
     *
     * @param text the text to filter the list by
     */
    private void filterList(String text) {
        ArrayList<UserModel> filteredList = new ArrayList<>();
        for (UserModel userModel : mUserModelList) {
            if (userModel.getmName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(userModel);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
        } else {
            searchUsersAdapter.setFilteredList(filteredList);
        }
    }

    /**
     * Configures the back press behavior to navigate to the HomePostsFragment.
     */
    private void handleOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHomePostsFragment();
            }
        });
    }

    /**
     * Navigates to the HomePostsFragment and updates the BottomNavigationView.
     */
    private void navigateToHomePostsFragment() {
        Fragment fragment = new HomePostsFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationBar);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }
}
