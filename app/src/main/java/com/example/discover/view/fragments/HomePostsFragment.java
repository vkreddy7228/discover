package com.example.discover.view.fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.discover.R;
import com.example.discover.databinding.FragmentHomePostsBinding;
import com.example.discover.model.PostModel;
import com.example.discover.model.StoryModel;
import com.example.discover.model.UserModel;
import com.example.discover.model.UserStories;
import com.example.discover.view.adapters.PostsAdapter;
import com.example.discover.view.adapters.StoryAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A fragment representing the home posts screen.
 * It displays a list of stories and posts, and allows users to upload new stories.
 */
public class HomePostsFragment extends Fragment {

    private ArrayList<StoryModel> storyModelArrayList;
    private ArrayList<PostModel> homePostsList;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;
    private FirebaseStorage mStorage;
    private FragmentHomePostsBinding mBinding;
    private ActivityResultLauncher<String> galleryLauncher;
    private ProgressDialog mDialog;
    private ShimmerRecyclerView postsShimmerRecycler, storiesShimmerRecycler;
    private String mCategory;

    /**
     * Required empty public constructor.
     */
    public HomePostsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentHomePostsBinding.inflate(inflater, container, false);

        // Show shimmer effect until data loads
        showShimmerUntilDataLoads();

        // Set up progress dialog for story upload
        setUpProgressDialog();

        // Initialize story list and adapter
        initializeStoryList();

        // Initialize home posts list and adapter
        initializeHomePostsList();

        // Fetch stories and posts from Firebase
        fetchStories();
        fetchPosts();

        // Set up toolbar icon
        setUpToolBarIcon();

        // Set up upload story button click listener
        mBinding.uploadStoryImageView.setOnClickListener(v -> uploadStory());

        // Register gallery launcher for selecting images
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> handleImageSelection(uri));

        return mBinding.getRoot();
    }

    /**
     * Displays shimmer effect until data loads.
     */
    private void showShimmerUntilDataLoads() {
        postsShimmerRecycler = mBinding.getRoot().findViewById(R.id.homeDashboardRecyclerView);
        postsShimmerRecycler.showShimmerAdapter();

        storiesShimmerRecycler = mBinding.getRoot().findViewById(R.id.storyListRecyclerView);
        storiesShimmerRecycler.showShimmerAdapter();
    }

    /**
     * Sets up the progress dialog for story uploading.
     */
    private void setUpProgressDialog() {
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setTitle("Story uploading..");
        mDialog.setMessage("Please Wait !!");
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * Initializes the story list and its adapter.
     */
    private void initializeStoryList() {
        storyModelArrayList = new ArrayList<>();
        StoryAdapter storyAdapter = new StoryAdapter(storyModelArrayList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.storyListRecyclerView.setLayoutManager(linearLayoutManager);
        mBinding.storyListRecyclerView.setNestedScrollingEnabled(true);
        mBinding.storyListRecyclerView.setAdapter(storyAdapter);
    }

    /**
     * Initializes the home posts list and its adapter.
     */
    private void initializeHomePostsList() {
        homePostsList = new ArrayList<>();
        PostsAdapter postsAdapter = new PostsAdapter(homePostsList, getContext());
        LinearLayoutManager linearLayoutManagerPosts = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.homeDashboardRecyclerView.setLayoutManager(linearLayoutManagerPosts);
        mBinding.homeDashboardRecyclerView.setNestedScrollingEnabled(true);
        mBinding.homeDashboardRecyclerView.setAdapter(postsAdapter);
    }

    /**
     * Fetches the stories from Firebase and updates the story adapter.
     */
    private void fetchStories() {
        mDataBase.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    storyModelArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        StoryModel storyModel = new StoryModel();
                        storyModel.setmStoryBy(dataSnapshot.getKey());
                        storyModel.setmStoryAt(dataSnapshot.child("mStoryBy").getValue(Long.class));
                        ArrayList<UserStories> stories = new ArrayList<>();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.child("userStories").getChildren()) {
                            UserStories userStories = dataSnapshot1.getValue(UserStories.class);
                            stories.add(userStories);
                        }
                        storyModel.setmUserStoriesList(stories);
                        storyModelArrayList.add(storyModel);
                    }
                    // Sort the list based on the time field
                    Collections.sort(storyModelArrayList, Comparator.comparingLong(StoryModel::getmStoryAt).reversed());
                    storiesShimmerRecycler.hideShimmerAdapter();
                    ((StoryAdapter) mBinding.storyListRecyclerView.getAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    /**
     * Fetches the posts from Firebase and updates the posts adapter.
     */
    private void fetchPosts() {
        mDataBase.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                homePostsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostModel postModel = dataSnapshot.getValue(PostModel.class);
                    postModel.setmPostId(dataSnapshot.getKey());
                    homePostsList.add(postModel);
                }
                // Sort the list based on the time field
                Collections.sort(homePostsList, Comparator.comparingLong(PostModel::getPostedAt).reversed());
                postsShimmerRecycler.hideShimmerAdapter();
                ((PostsAdapter) mBinding.homeDashboardRecyclerView.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    /**
     * Handles the story upload action.
     */
    private void uploadStory() {
        galleryLauncher.launch("image/*");
    }

    /**
     * Handles the image selection from the gallery.
     *
     * @param uri The URI of the selected image.
     */
    private void handleImageSelection(Uri uri) {
        mBinding.uploadStoryImageView.setImageURI(uri);
        mDialog.show();
        final StorageReference reference = mStorage.getReference().child("stories")
                .child(mAuth.getUid())
                .child(new Date().getTime() + "");

        reference.putFile(uri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri1 -> {
            StoryModel storyModel = new StoryModel();
            storyModel.setmStoryAt(new Date().getTime());
            mDataBase.getReference().child("stories")
                    .child(mAuth.getUid())
                    .child("mStoryBy")
                    .setValue(storyModel.getmStoryAt()).addOnSuccessListener(unused -> {
                        UserStories userStories = new UserStories(uri1.toString(), storyModel.getmStoryAt());
                        mDataBase.getReference().child("stories")
                                .child(mAuth.getUid())
                                .child("userStories")
                                .push()
                                .setValue(userStories);
                        mDialog.dismiss();
                        Toast.makeText(getContext(), "Story uploaded", Toast.LENGTH_SHORT).show();
                    });
        }));
    }

    /**
     * Sets up the toolbar icon with the user's profile photo.
     */
    private void setUpToolBarIcon() {
        mDataBase.getReference().child("users")
                .child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            Picasso.get().load(userModel.getMProfilePhoto()).into(mBinding.userProfileIconStatusBarImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish(); // Exit the app immediately
            }
        });
    }
}
