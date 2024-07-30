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
import com.example.discover.R;
import com.example.discover.databinding.FragmentNotificationsBinding;
import com.example.discover.model.NotificationsModel;
import com.example.discover.view.adapters.NotificationsAdapter;
import com.example.discover.view.fragments.HomePostsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A fragment that displays notifications for the user.
 * It shows a list of notifications and handles navigation when the back button is pressed.
 */
public class NotificationsFragment extends Fragment {

    private ArrayList<NotificationsModel> notificationsModelArrayList;
    private FragmentNotificationsBinding mBinding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;

    /**
     * Required empty public constructor.
     */
    public NotificationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentNotificationsBinding.inflate(inflater, container, false);

        // Handle back press to navigate to HomePostsFragment
        handleOnBackPressed();

        // Initialize notifications list and adapter
        initializeNotificationsList();

        // Fetch notifications from Firebase
        fetchNotifications();

        return mBinding.getRoot();
    }

    /**
     * Initializes the notifications list and its adapter.
     */
    private void initializeNotificationsList() {
        notificationsModelArrayList = new ArrayList<>();
        NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notificationsModelArrayList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.notificationsRecyclerView.setLayoutManager(linearLayoutManager);
        mBinding.notificationsRecyclerView.setNestedScrollingEnabled(false);
        mBinding.notificationsRecyclerView.setAdapter(notificationsAdapter);
    }

    /**
     * Fetches notifications from Firebase and updates the notifications adapter.
     */
    private void fetchNotifications() {
        mDataBase.getReference().child("notifications")
                .child(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            notificationsModelArrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                NotificationsModel notificationsModel = dataSnapshot.getValue(NotificationsModel.class);
                                notificationsModel.setmNotificationId(dataSnapshot.getKey());
                                notificationsModelArrayList.add(notificationsModel);
                            }
                            // Sort notifications by time in descending order
                            Collections.sort(notificationsModelArrayList, new Comparator<NotificationsModel>() {
                                @Override
                                public int compare(NotificationsModel o1, NotificationsModel o2) {
                                    return Long.compare(o2.getmNotificationAt(), o1.getmNotificationAt());
                                }
                            });
                            ((NotificationsAdapter) mBinding.notificationsRecyclerView.getAdapter()).notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    /**
     * Handles the back press event to navigate to the HomePostsFragment.
     */
    private void handleOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Replace the current fragment with HomePostsFragment
                Fragment targetFragment = new HomePostsFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, targetFragment);
                transaction.commit();

                // Update BottomNavigationView to reflect the current fragment
                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationBar);
                bottomNavigationView.setSelectedItemId(R.id.home);
            }
        });
    }
}
