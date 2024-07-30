package com.example.discover.repo;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Repository for handling authentication-related tasks using Firebase.
 */
public class LoginRepository {

    private final FirebaseAuth mAuth;

    /**
     * Initializes the repository with Firebase authentication instance.
     */
    public LoginRepository() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Signs in a user with email and password.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @return A Task representing the sign-in operation.
     */
    public Task<AuthResult> signInWithEmailPassword(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }
}
