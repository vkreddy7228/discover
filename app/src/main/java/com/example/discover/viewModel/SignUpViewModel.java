package com.example.discover.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.discover.model.UserModel;
import com.example.discover.repo.UserRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> signUpSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public SignUpViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<Boolean> getSignUpSuccess() {
        return signUpSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void signUp(String email, String password, String name, String profession) {
        if (isValid(email, password, name, profession)) {
            userRepository.signUp(email, password, new UserRepository.SignUpCallback() {
                @Override
                public void onSuccess(String userId) {
                    UserModel user = new UserModel(name, profession, email, password);
                    userRepository.saveUser(userId, user);
                    signUpSuccess.setValue(true);
                }

                @Override
                public void onFailure(String error) {
                    errorMessage.setValue(error);
                }
            });
        }
    }

    private boolean isValid(String email, String password, String name, String profession) {
        // Validation logic (can be moved to a validator class)
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || profession.isEmpty()) {
            errorMessage.setValue("All the fields are mandatory.");
            return false;
        }
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$");
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            errorMessage.setValue("Invalid password format");
            return false;
        }
        return true;
    }
}
