package com.example.discover.view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.discover.R;
import com.example.discover.databinding.FragmentProfileBinding;
import com.example.discover.model.UserModel;
import com.example.discover.view.activity.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * A fragment that displays and allows the user to edit their profile information.
 * This includes uploading a profile picture, logging out, and viewing followers and following lists.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mBinding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;
    private FirebaseStorage mStorage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);

        // Handle back press to navigate to HomePostsFragment
        handleOnBackPressed();

        // Set up menu icon click listener
        mBinding.menuIconProfilePageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLogoutVisibility();
            }
        });

        // Set up logout button click listener
        mBinding.logOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        // Load and display user profile information
        loadUserProfile();

        // Set up profile picture click listener
        mBinding.userProfilePicPostProfilePageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Set up followers and following click listeners
        mBinding.followersCountProfilePageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(new FollowersFragment());
            }
        });

        mBinding.followingCountProfilePageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(new FollowingFragment());
            }
        });

        return mBinding.getRoot();
    }

    /**
     * Toggles the visibility of the logout text view.
     */
    private void toggleLogoutVisibility() {
        if (mBinding.logOutTextView.getVisibility() == View.VISIBLE) {
            mBinding.logOutTextView.setVisibility(View.GONE);
        } else {
            mBinding.logOutTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Logs out the current user and navigates to the login activity.
     */
    private void logOut() {
        mAuth.signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        getContext().startActivity(intent);
    }

    /**
     * Loads and displays the current user's profile information.
     */
    private void loadUserProfile() {
        mDataBase.getReference().child("users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            if (user != null) {
                                Picasso.get().load(user.getMProfilePhoto())
                                        .placeholder(R.drawable.add_profile_place_holder)
                                        .into(mBinding.userProfilePicPostProfilePageImageView);
                                mBinding.userNameProfilePageTextView.setText(user.getmName());
                                mBinding.userProfession.setText(user.getmProfession());
                                mBinding.postsCountProfilePageTextView.setText(String.valueOf(user.getmPostsCount()));
                                mBinding.followersCountProfilePageTextView.setText(String.valueOf(user.getmFollowersCount()));
                                mBinding.followingCountProfilePageTextView.setText(String.valueOf(user.getmFollowingCount()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    /**
     * Opens an intent to pick an image from the gallery.
     */
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 11);
    }

    /**
     * Handles the result of the image picker intent.
     * Uploads the selected image to Firebase Storage and updates the user's profile photo URL in Firebase Database.
     *
     * @param requestCode the request code passed in startActivityForResult
     * @param resultCode  the result code returned by the activity
     * @param data        the intent containing the result data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 11 && data != null && data.getData() != null) {
            Uri uri = data.getData();
            mBinding.userProfilePicPostProfilePageImageView.setImageURI(uri);

            final StorageReference reference = mStorage.getReference().child("profilePhotos")
                    .child(mAuth.getUid());
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Profile photo updated", Toast.LENGTH_SHORT).show();
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mDataBase.getReference().child("users").child(mAuth.getUid()).child("mProfilePhoto")
                                    .setValue(uri.toString());
                        }
                    });
                }
            });
        }
    }

    /**
     * Navigates to the specified fragment.
     *
     * @param fragment the fragment to navigate to
     */
    private void navigateToFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Configures the back press behavior to navigate to HomePostsFragment.
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
