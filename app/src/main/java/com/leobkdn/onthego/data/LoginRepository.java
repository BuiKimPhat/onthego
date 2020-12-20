package com.leobkdn.onthego.data;

import androidx.annotation.Nullable;

import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.result.Result;
import com.leobkdn.onthego.data.source.LoginDataSource;

import java.util.Date;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public Result<String> logout(@Nullable String token) {
        //handle logout
        user = null;
        if (token != null) {
            return dataSource.logout(token);
        } else return new Result.Error(new Exception("Không có token! Vui lòng đăng nhập lại!"));
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<LoggedInUser> login(String username, String password) {
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public Result<LoggedInUser> signUp(String email, String password, String name, @Nullable Date birthday, @Nullable String address) {
        Result<LoggedInUser> result = dataSource.signUp(email, password, name, birthday, address);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public Result<String> editInfo(LoggedInUser user) {
        if (user == null) return new Result.Error(new Exception("Dữ liệu người dùng lỗi! Vui lòng đăng nhập lại!"));
        Result<String> result = dataSource.editInfo(new LoggedInUser(user.getDisplayName(), user.getEmail(), user.getToken(), user.getIsAdmin(), user.getBirthday(), user.getAddress()));
        return result;
    }

    public Result<String> changePassword(String token, String oldPassword, String newPassword){
        if (token == null) return new Result.Error(new Exception("Không có token! Vui lòng đăng nhập lại!"));
        Result<String> result = dataSource.changePassword(token, oldPassword, newPassword);
        return result;
    }
}