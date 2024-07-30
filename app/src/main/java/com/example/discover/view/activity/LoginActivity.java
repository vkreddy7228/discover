package com.example.discover.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.discover.databinding.ActivityLoginBinding;
import com.example.discover.viewModel.LoginViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity for user login screen.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mLoginBinding;
    private LoginViewModel loginViewModel;
    private boolean doubleBackToExitPressedOnce = false;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mLoginBinding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        checkIfUserLoggedInAlready();
        handleOnBackPressed();

        mLoginBinding.signInButtonLoginScreen.setOnClickListener(v -> {
            String email = mLoginBinding.emailSignInScreenEditTextInput.getText().toString();
            String password = mLoginBinding.passwordSignInScreenEditTextInput.getText().toString();
            if (isValid(email, password)) {
                loginViewModel.signIn(email, password);
            }
        });

        mLoginBinding.registerLoginScreenTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        loginViewModel.getLoginSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfUserLoggedInAlready() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Handles the back button press event to confirm exit.
     */
    private void handleOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(LoginActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                    handler.postDelayed(() -> doubleBackToExitPressedOnce = false, 2000); // 2 seconds to press back again to exit
                }
            }
        });
    }

    /**
     * Validates the email and password inputs.
     *
     * @param email    The email address.
     * @param password The password.
     * @return True if inputs are valid, otherwise false.
     */
    private boolean isValid(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (loginViewModel.getLoginSuccess().getValue() != null && loginViewModel.getLoginSuccess().getValue()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
