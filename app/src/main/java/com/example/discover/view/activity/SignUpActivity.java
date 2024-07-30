package com.example.discover.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.discover.databinding.ActivitySignUpBinding;
import com.example.discover.viewModel.SignUpViewModel;

/**
 * Activity for user registration screen.
 */
public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding mSignUpBinding;
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(mSignUpBinding.getRoot());

        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        handleOnBackPressed();

        signUpViewModel.getSignUpSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(SignUpActivity.this, "Registration Success.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUpViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        mSignUpBinding.registerButtonLoginScreen.setOnClickListener(v -> {
            String email = mSignUpBinding.emailSignUpScreenEditTextInput.getText().toString();
            String password = mSignUpBinding.passwordSignUpScreenEditTextInput.getText().toString();
            String name = mSignUpBinding.nameSignUpScreenEditTextInput.getText().toString();
            String profession = mSignUpBinding.professionSignUpScreenEditTextInput.getText().toString();
            signUpViewModel.signUp(email, password, name, profession);
        });

        mSignUpBinding.signInTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Handles the back button press event to navigate to LoginActivity.
     */
    private void handleOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
