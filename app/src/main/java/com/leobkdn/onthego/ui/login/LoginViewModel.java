package com.leobkdn.onthego.ui.login;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.leobkdn.onthego.data.LoginRepository;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.R;

import java.time.LocalDate;
import java.util.Date;

// handle data on changed, call login, signup, logout method (from repository), input validators, store result of login/sign up
public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>(); //error messages
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.postValue(new LoginResult(new LoggedInUserView(data.getDisplayName(), data.getEmail(), data.getToken(), data.getIsAdmin(), data.getBirthday(), data.getAddress())));
        } else {
            loginResult.postValue(new LoginResult(result.toString()));
        }
    }

    public void signUp(String email, String password, String name, @Nullable Date birthday, @Nullable String address){
        Result<LoggedInUser> result = loginRepository.signUp(email, password, name, birthday, address);
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.postValue(new LoginResult(new LoggedInUserView(data.getDisplayName(), data.getEmail(), data.getToken(), data.getIsAdmin(), data.getBirthday(), data.getAddress())));
        } else {
            loginResult.postValue(new LoginResult(result.toString()));
        }
    }

    public void logOut(String token){
        Result<String> result = loginRepository.logout(token);
        if (result instanceof Result.Success) {
            loginResult.postValue(new LoginResult((Result.Success<String>) result));
        } else {
            loginResult.postValue(new LoginResult(result.toString()));
        }
    }

    public void editInfo(LoggedInUserView user){
        Result<String> result = loginRepository.editInfo(user);
        if (result instanceof Result.Success) {
            loginResult.postValue(new LoginResult((Result.Success<String>) result));
        } else {
            loginResult.postValue(new LoginResult(result.toString()));
        }
    }

    public void changePassword(String token, String oldPassword, String newPassword){
        Result<String> result = loginRepository.changePassword(token, oldPassword, newPassword);
        if (result instanceof Result.Success) {
            loginResult.postValue(new LoginResult((Result.Success<String>) result));
        } else {
            loginResult.postValue(new LoginResult(result.toString()));
        }
    }

    public void loginDataChanged(String email, String password) {
        if (!isUserNameValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password, null));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }
    public void signUpDataChanged(String email, String password, String name) {
        if (!isUserNameValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password, null));
        } else if (!isTextValid(name)){
            loginFormState.setValue(new LoginFormState(null, null, R.string.no_empty));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }
    public void pwdDataChanged(String oldPwd, String newPwd, String confirmPwd){
        if (!isPasswordValid(oldPwd)){
            loginFormState.setValue(new LoginFormState(R.string.invalid_password, null, null));
        } else if (!isPasswordValid(newPwd)){
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password, null));
        } else if (!isConfirmValid(newPwd, confirmPwd)) {
            loginFormState.setValue(new LoginFormState(null, null, R.string.password_confirm_invalid));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
//            return !username.trim().isEmpty();
            return false;
        }
    }
    private boolean isTextValid(String text){
        if (text == null) return false;
        return !text.trim().isEmpty();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
    private boolean isConfirmValid(String newPassword, String confirmPassword){
        return newPassword.equals(confirmPassword);
    }
    
}