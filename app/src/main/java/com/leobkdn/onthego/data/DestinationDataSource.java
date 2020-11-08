package com.leobkdn.onthego.data;

import androidx.annotation.Nullable;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.TripDestination;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class that handles trip CRUD.
 * (Server class)
 */
public class DestinationDataSource {

    // server credentials
    private static final String secret = "@M1@j0K37oU?";
    private static final String hostName = "192.168.1.62";
    private static final String instance = "LEOTHESECOND";
    private static final String port = "1433";
    private static final String dbName = "OnTheGo";
    private static final String dbUser = "user";
    private static final String dbPassword = "userpass";
    private static final String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";instance=" + instance + ";user=" + dbUser + ";password=" + dbPassword + ";databasename=" + dbName;
    private String stringResult = "Error";
    private ArrayList<TripDestination> result = new ArrayList<TripDestination>();
    private ArrayList<Destination> result1 = new ArrayList<>();

    private String tokenVerifier(String token){
        // verify if token meets the claims
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
    public Result<ArrayList<TripDestination>> fetchTripDestination(String token, int tripId){
        try {
            //Set connection
            Connection connection = DriverManager.getConnection(dbURI);
            if (connection != null) {
                // verify token
                tokenVerifier(token);

                String sqlQuery = "select id, [name], startTime, finishTime from Destination join Trip_Destination on Destination.id = Trip_Destination.destinationId where Trip_Destination.tripId = ?";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setInt(1, tripId);
                ResultSet res = statement.executeQuery();
                if (!res.next()) throw new SQLException("Không tìm thấy điểm đến");
                else {
                    do {
                        result.add(new TripDestination(res.getInt(1), res.getString(2), res.getTimestamp(3), res.getTimestamp(4)));
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

    public Result<ArrayList<Destination>> fetchDestinations(String token, @Nullable String category){
        try {
            //Set connection
            Connection connection = DriverManager.getConnection(dbURI);
            if (connection != null) {
                // verify token
                tokenVerifier(token);
                String sqlQuery;
                PreparedStatement statement;
                if (category == null){
                    sqlQuery = "select id, [name], address, phone, description, city from Destination where category is null";
                    statement = connection.prepareStatement(sqlQuery);
                } else {
                    sqlQuery = "select id, [name], address, phone, description, city from Destination where category = ?";
                    statement = connection.prepareStatement(sqlQuery);
                    statement.setString(1, category);
                }
                ResultSet res = statement.executeQuery();
                if (!res.next()) throw new SQLException("Không tìm thấy điểm đến");
                else {
                    do {
                        result1.add(new Destination(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), res.getString(5), category, res.getString(6), null));
                    } while (res.next());
                }
                connection.close();
            } else throw new SQLException("Lỗi kết nối");
        } catch (JWTVerificationException e){
            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
        } catch (Exception e) {
            return new Result.Error(e);
        }
        return new Result.Success<>(result1);
    }
}