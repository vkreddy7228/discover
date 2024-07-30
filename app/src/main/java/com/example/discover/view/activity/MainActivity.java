package com.example.discover.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.discover.R;
import com.example.discover.databinding.ActivityMainBinding;
import com.example.discover.view.fragments.HomePostsFragment;
import com.example.discover.view.fragments.NotificationsFragment;
import com.example.discover.view.fragments.PostFragment;
import com.example.discover.view.fragments.ProfileFragment;
import com.example.discover.view.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load default fragment
        loadFragment(new HomePostsFragment());

        binding.bottomNavigationBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.home:
                        selectedFragment = new HomePostsFragment();
                        break;
                    case R.id.search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.post:
                        selectedFragment = new PostFragment();
                        break;
                    case R.id.notifications:
                        selectedFragment = new NotificationsFragment();
                        break;
                    case R.id.profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
