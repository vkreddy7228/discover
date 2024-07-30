package com.example.discover.repo;

import com.example.discover.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRepository {

    private final FirebaseAuth auth;
    private final FirebaseDatabase database;

    public UserRepository() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    public interface SignUpCallback {
        void onSuccess(String userId);
        void onFailure(String error);
    }

    public void signUp(String email, String password, SignUpCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(task.getResult().getUser().getUid());
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void saveUser(String userId, UserModel user) {
        DatabaseReference userRef = database.getReference().child("users").child(userId);
        userRef.setValue(user);
    }
}
