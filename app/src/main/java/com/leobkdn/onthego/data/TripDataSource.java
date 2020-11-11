package com.leobkdn.onthego.data;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.ui.go.Trip;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class that handles trip CRUD.
 * (Server class)
 */
public class TripDataSource {

    // server credentials
    private static final String secret = "@M1@j0K37oU?";
    private static final String hostName = "10.20.2.181";
    private static final String instance = "LEOTHESECOND";
    private static final String port = "1433";
    private static final String dbName = "OnTheGo";
    private static final String dbUser = "user";
    private static final String dbPassword = "userpass";
    private static final String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";instance=" + instance + ";user=" + dbUser + ";password=" + dbPassword + ";databasename=" + dbName;
    private String stringResult = "Error";
    private ArrayList<Trip> result = new ArrayList<Trip>();

    private String tokenVerifier(String token) {
        // verify if token meets the claims
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getPayload();
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            throw exception;
        }
    }

    //    public Result<String> addUserTrip(String token, int tripId){
//
//    }
//    public Result<String> addUserTrip(String token, String name, ArrayList<Destination> destinations){
//    }
    public Result<ArrayList<Trip>> fetchUserTrip(String token) {
        try {
            //Set connection
            Connection connection = DriverManager.getConnection(dbURI);
            if (connection != null) {
                // verify token
                tokenVerifier(token);
                result = new ArrayList<Trip>();
                String sqlQuery = "select Trip.id, Trip.[name], [User].[name] as [owner], Trip.createdAt from Trip join (select tripId from User_Trip where userId in (select userId from [User_Token] where token = ?)) as cUser on Trip.id = cUser.tripId join [User] on Trip.ownerId = [User].id";
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
        } catch (JWTVerificationException e) {
            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
        } catch (Exception e) {
            return new Result.Error(e);
        }
        return new Result.Success<>(result);
    }

    public Result<ArrayList<Trip>> addUserTrip(String token, int tripId) {
        try {
            //Set connection
            Connection connection = DriverManager.getConnection(dbURI);
            if (connection != null) {
                // verify token
                tokenVerifier(token);

                // check unique info
                String sqlQuery = "select userId from [User_Token] where token = ?";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, token);
                ResultSet creds = statement.executeQuery();
                if (!creds.next()) throw new SQLException("Không tồn tại token");
                sqlQuery = "insert into User_Trip values (?,?)";
                statement = connection.prepareStatement(sqlQuery);
                statement.setInt(1, creds.getInt(1));
                statement.setInt(2, tripId);
                int count = statement.executeUpdate();
                if (count <= 0) throw new SQLException("Không thể thêm trip");
                connection.close();
            } else throw new SQLException("Lỗi kết nối");
        } catch (JWTVerificationException e) {
            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
        } catch (Exception e) {
            return new Result.Error(e);
        }
        return fetchUserTrip(token);
    }

    public Result<String> newTrip(String token, String newTripName, ArrayList<TripDestination> destinations) {
        try {
            //Set connection
            Connection connection = DriverManager.getConnection(dbURI);
            if (connection != null) {
                // verify token
                tokenVerifier(token);

                // check unique info
                String sqlQuery = "select userId from [User_Token] where token = ?";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, token);
                ResultSet creds = statement.executeQuery();
                if (!creds.next()) throw new SQLException("Không tồn tại token");

                sqlQuery = "insert into Trip (ownerId, [name], createdAt) values (?,?, CURRENT_TIMESTAMP)";
                statement = connection.prepareStatement(sqlQuery);
                statement.setInt(1, creds.getInt(1));
                statement.setString(2, newTripName);
                int count = statement.executeUpdate();
                if (count <= 0) throw new SQLException("Không thể thêm trip");

                sqlQuery = "insert into Trip_Destination(tripId, destinationId, startTime, finishTime) values (IDENT_CURRENT('Trip'),?,?,?)";
                statement = connection.prepareStatement(sqlQuery);
                for (int i=0;i<destinations.size();i++){
                    statement.setInt(1, destinations.get(i).getId());
                    statement.setDate(2, destinations.get(i).getStartTime() != null ? new Date(destinations.get(i).getStartTime().getTime()) : null);
                    statement.setDate(3, destinations.get(i).getFinishTime() != null ? new Date(destinations.get(i).getFinishTime().getTime()): null);
                    int countDes = statement.executeUpdate();
                    if (countDes <= 0) throw new SQLException("Không thể thêm trip");
                }

                sqlQuery = "insert into User_Trip values (?,IDENT_CURRENT('Trip'))";
                statement = connection.prepareStatement(sqlQuery);
                statement.setInt(1, creds.getInt(1));
                int countUT = statement.executeUpdate();
                if (countUT <= 0) throw new SQLException("Không thể thêm trip");

                stringResult = "Tạo chuyến đi thành công!";
                connection.close();
            } else throw new SQLException("Lỗi kết nối");
        } catch (JWTVerificationException e) {
            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
        } catch (Exception e) {
            return new Result.Error(e);
        }
        return new Result.Success<>(stringResult);
    }
}