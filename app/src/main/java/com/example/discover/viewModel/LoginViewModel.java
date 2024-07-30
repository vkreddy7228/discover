package com.example.discover.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.discover.repo.LoginRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * ViewModel for handling login-related data and operations.
 */
public class LoginViewModel extends AndroidViewModel {

    private final LoginRepository loginRepository;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /**
     * Initializes the ViewModel with the LoginRepository.
     *
     * @param application The application context.
     */
    public LoginViewModel(@NonNull Application application) {
        super(application);
        loginRepository = new LoginRepository();
    }

    /**
     * Gets the LiveData representing login success state.
     *
     * @return A LiveData<Boolean> indicating login success.
     */
    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    /**
     * Gets the LiveData representing error messages.
     *
     * @return A LiveData<String> with error messages.
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Attempts to sign in a user with the given email and password.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     */
    public void signIn(String email, String password) {
        loginRepository.signInWithEmailPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Invalid credentials");
                }
            }
        });
    }
}
