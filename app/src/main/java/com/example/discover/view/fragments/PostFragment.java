package com.example.discover.view.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.discover.R;
import com.example.discover.databinding.FragmentPostBinding;
import com.example.discover.model.PostModel;
import com.example.discover.model.UserModel;
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
import java.util.ArrayList;
import java.util.Date;

/**
 * A fragment that allows users to create and upload a new post.
 * This includes selecting an image, providing a description, and choosing a category.
 */
public class PostFragment extends Fragment {

    private FragmentPostBinding mBinding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;
    private FirebaseStorage mStorage;
    private Uri uri;
    private String category = "";
    private ProgressDialog progressDialog;

    /**
     * Required empty public constructor.
     */
    public PostFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPostBinding.inflate(inflater, container, false);

        // Handle back press to navigate to HomePostsFragment
        handleOnBackPressed();

        // Set up the progress dialog
        setUpProgressDialog();

        // Set up user profile data
        setUpUserProfile();

        // Image upload button click listener
        mBinding.uploadPostImagePostPageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Category selection listener
        mBinding.autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category = mBinding.autoCompleteTextView.getText().toString();
            }
        });

        // Post button click listener
        mBinding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category.isEmpty()) {
                    Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.show();
                uploadPost();
            }
        });

        return mBinding.getRoot();
    }

    /**
     * Opens an intent to pick an image from the gallery.
     */
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 10);
    }

    /**
     * Configures the progress dialog shown during post upload.
     */
    private void setUpProgressDialog() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Post uploading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * Handles post upload to Firebase Storage and Database.
     */
    private void uploadPost() {
        if (uri == null) {
            Toast.makeText(getContext(), "Please select an image to upload", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        final StorageReference storageReference = mStorage.getReference().child("posts")
                .child(mAuth.getUid())
                .child(new Date().getTime() + "");

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        PostModel postModel = createPostModel(uri);
                        savePostToDatabase(postModel);
                    }
                });
            }
        });
    }

    /**
     * Creates a PostModel instance with the provided image URI and user input.
     *
     * @param uri the URI of the uploaded image
     * @return a PostModel instance
     */
    private PostModel createPostModel(Uri uri) {
        PostModel postModel = new PostModel();
        postModel.setmPostImage(uri.toString());
        postModel.setmPostedBy(mAuth.getUid());
        postModel.setmCategory(category);
        postModel.setmPostDesc(mBinding.postDescriptionPostPageEditText.getText().toString());
        postModel.setPostedAt(new Date().getTime());
        return postModel;
    }

    /**
     * Saves the post model to the database and performs additional actions upon success.
     *
     * @param postModel the post model to be saved
     */
    private void savePostToDatabase(PostModel postModel) {
        mDataBase.getReference().child("posts")
                .push().setValue(postModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        uploadToCategorySpecificPosts(postModel);
                        increasePostsCount();
                    }
                });
    }

    /**
     * Uploads the post model to the category-specific posts section in the database.
     *
     * @param postModel the post model to be uploaded
     */
    private void uploadToCategorySpecificPosts(PostModel postModel) {
        mDataBase.getReference().child("categories")
                .child(postModel.getmCategory())
                .push().setValue(postModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        navigateToHomePostsFragment();
                    }
                });
    }

    /**
     * Increases the post count for the current user.
     */
    private void increasePostsCount() {
        mDataBase.getReference().child("users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel != null) {
                                mDataBase.getReference().child("users")
                                        .child(mAuth.getUid()).child("mPostsCount")
                                        .setValue(userModel.getmPostsCount() + 1);
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
     * Navigates back to the HomePostsFragment and updates the BottomNavigationView.
     */
    private void navigateToHomePostsFragment() {
        Fragment fragment = new HomePostsFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationBar);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    /**
     * Sets up the user profile details in the fragment view.
     */
    private void setUpUserProfile() {
        mDataBase.getReference().child("users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel != null) {
                                Picasso.get().load(userModel.getMProfilePhoto())
                                        .placeholder(R.drawable.add_profile_place_holder)
                                        .into(mBinding.userProfilePostPageImageView);
                                mBinding.userNamePostPageTextView.setText(userModel.getmName());
                                mBinding.userProfessionPostPageTextView.setText(userModel.getmProfession());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && data != null && data.getData() != null) {
            uri = data.getData();
            mBinding.postImagePostPageImageView.setImageURI(uri);
            mBinding.postButton.setEnabled(true);
            mBinding.postImagePostPageImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Handles the back press event to navigate to the HomePostsFragment.
     */
    private void handleOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHomePostsFragment();
            }
        });
    }
}
