package com.leobkdn.onthego.data;

import android.Manifest;
import android.accounts.AuthenticatorException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.ui.destination.Destination;
import com.leobkdn.onthego.ui.go.Trip;
import com.leobkdn.onthego.ui.login.LoginActivity;
import com.leobkdn.onthego.ui.signup.SignUpActivity;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

/**
 * Class that handles trip CRUD.
 * (Server class)
 */
public class TripDataSource {

    // server credentials
    private static final String secret = "@M1@j0K37oU?";
    private static final String hostName = "192.168.1.15";
    private static final String instance = "LEOTHESECOND";
    private static final String port = "1433";
    private static final String dbName = "OnTheGo";
    private static final String dbUser = "user";
    private static final String dbPassword = "userpass";
    private static final String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";instance=" + instance + ";user=" + dbUser + ";password=" + dbPassword + ";databasename=" + dbName;
    private String stringResult = "Error";
    private ArrayList<Trip> result = new ArrayList<Trip>();

    private String tokenVerifier(String token){
        // veirfy if token meets the claims
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getPayload();
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            throw exception;
        }
    }
//    public Result<String> addUserTrip(String token, int tripId){
//
//    }
//    public Result<String> addUserTrip(String token, String name, ArrayList<Destination> destinations){
//    }
    public Result<ArrayList<Trip>> fetchUserTrip(String token){
        try {
            //Set connection
            Connection connection = DriverManager.getConnection(dbURI);
            if (connection != null) {
                // verify token
                tokenVerifier(token);

                String sqlQuery = "select Trip.id, Trip.[name], cUser.[name] as [owner], Trip.createdAt from Trip join (select id, [name] from [User] where id in (select id from [User_Token] where token = ?)) as cUser on ownerId = cUser.id";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, token);
                ResultSet res = statement.executeQuery();
                if (!res.next()) throw new SQLException("Không tìm thấy trip");
                else {
                    do {
                        result.add(new Trip(res.getInt(1), res.getString(2), res.getString(3), res.getDate(4)));
                    } while (res.next());
                }
                connection.close();
            } else throw new SQLException("Lỗi kết nối");
        } catch (JWTVerificationException e){
            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
        } catch (Exception e) {
            return new Result.Error(e);
        }
        return new Result.Success<>(result);
    }
}