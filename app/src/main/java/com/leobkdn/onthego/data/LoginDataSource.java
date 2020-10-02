package com.leobkdn.onthego.data;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.ui.login.LoginActivity;
import com.leobkdn.onthego.ui.signup.SignUpActivity;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    // server credentials
    private static final String secret = "@M1@j0K37oU?";
    private static final String hostName = "192.168.1.15";
    private static final String instance = "LEOTHESECOND";
    private static final String port = "1433";
    private static final String dbName = "OnTheGo";
    private static final String dbUser = "user";
    private static final String dbPassword = "userpass";
    private static final String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";instance=" + instance + ";user=" + dbUser + ";password=" + dbPassword + ";databasename=" + dbName;
    private String token;
    private LoggedInUser newUser;

    private String tokenGenerator(Integer UID){
        // create token by HS256 algorithm
        // Payload: userID
        // Exp: 1 year
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            Date expDate = calendar.getTime();
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withClaim("uid", UID)
                    .withKeyId(java.util.UUID.randomUUID().toString())
                    .withExpiresAt(expDate)
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
            throw exception;
        }
    }
    public Result<LoggedInUser> login(String username, String password) {
        // TODO: handle loggedInUser authentication
        try {
            LoggedInUser fakeUser =
                    new LoggedInUser("Bạn của tôi", token);
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Lỗi đăng nhập", e));
        }
    }

    public Result<LoggedInUser> signUp(String email, String password, String fullName) {
        //  request to create new user
        try {
            //Set connection
            Connection connection = DriverManager.getConnection(dbURI);
            if (connection != null) {

                // check unique info
                String sqlQuery = "select id from [User] where email = ?";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, email);
                ResultSet checkEmail = statement.executeQuery();
                if (checkEmail.next()) throw new SQLException("Email đã tồn tại!");

                //  insert data to db
                sqlQuery = "insert into [User] (email,[password],[name], createdAt, isAdmin) values (?, ?, ?, CURRENT_TIMESTAMP, 0)";
                statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, email);
                statement.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
                statement.setString(3, fullName);
                int rowInserted = statement.executeUpdate();

                if (rowInserted > 0) {
                    //if insert success
                    //get last user id, name from database
                    sqlQuery = "select id, [name] from [User] where id = IDENT_CURRENT('User')";
                    statement = connection.prepareStatement(sqlQuery);
                    ResultSet result = statement.executeQuery();
                    result.next();
                    Integer userID = result.getInt(1);
                    String username = result.getString(2);

                    // create token
                    token = tokenGenerator(userID);

                    //insert new token to database
                    sqlQuery = "insert into [User_Token] (userId,token,createdAt) values (?, ?, CURRENT_TIMESTAMP)";
                    statement = connection.prepareStatement(sqlQuery);
                    statement.setInt(1, userID);
                    statement.setString(2,token);
                    int tokenNewRow = statement.executeUpdate();
                    if (tokenNewRow > 0) {
                        // if token inserted
                        // set LoggedInUser
                        newUser = new LoggedInUser(username, email, token); // LOGIN SUCCESS
                    }
                }
                connection.close();
            } else throw new SQLException("Lỗi kết nối");
        } catch (Exception e) {
            return new Result.Error(e);
        }
        return new Result.Success<>(newUser);
    }

    public void logout() {
        // TODO: revoke authentication
    }
}