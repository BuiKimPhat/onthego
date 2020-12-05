package com.leobkdn.onthego.data;

import android.net.http.HttpResponseCache;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import androidx.annotation.Nullable;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.data.model.Weather;

import org.json.JSONException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Class that handles trip CRUD.
 * (Server class)
 */
public class DestinationDataSource extends ServerData {

    // server credentials
//    private static final String secret = "@M1@j0K37oU?";
//    private static final String hostName = "10.10.50.83";
//    private static final String instance = "LEOTHESECOND";
//    private static final String port = "1433";
//    private static final String dbName = "OnTheGo";
//    private static final String dbUser = "user";
//    private static final String dbPassword = "userpass";
//    private static final String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";instance=" + instance + ";user=" + dbUser + ";password=" + dbPassword + ";databasename=" + dbName;
    private String stringResult = "Error";
    private ArrayList<TripDestination> result = new ArrayList<TripDestination>();
    private ArrayList<Destination> result1 = new ArrayList<>();

    //    public Result<String> addUserTrip(String token, int tripId){
//
//    }
//    public Result<String> addUserTrip(String token, String name, ArrayList<Destination> destinations){
//    }
    public Result<ArrayList<TripDestination>> fetchTripDestination(String token, int tripId) {
        try {
//            //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//                // verify token
//
//                String sqlQuery = "select id, [name], startTime, finishTime from Destination join Trip_Destination on Destination.id = Trip_Destination.destinationId where Trip_Destination.tripId = ?";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
//                statement.setInt(1, tripId);
//                ResultSet res = statement.executeQuery();
//                if (!res.next()) throw new SQLException("Không tìm thấy điểm đến");
//                else {
//                    do {
//                        result.add(new TripDestination(res.getInt(1), res.getString(2), res.getTimestamp(3), res.getTimestamp(4)));
//                    } while (res.next());
//                }
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");
//        } catch (JWTVerificationException e) {
//            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
            // Create URL
            URL endPoint = new URL(server + "/destination/trip/" + tripId);
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                result = new ArrayList<TripDestination>();
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    int id = -1, rateNum = 0;
                    String name = null, address = null, description = null;
                    float lat = 0, lon = 0, rating = 0;
                    Timestamp startTime = null, finishTime = null;
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        if (jsonReader.nextName().equals("id") && jsonReader.peek() != JsonToken.NULL) {
                            id = jsonReader.nextInt();
                        } else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("name") && jsonReader.peek() != JsonToken.NULL) {
                            name = jsonReader.nextString();
                        } else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("address") && jsonReader.peek() != JsonToken.NULL)
                            address = jsonReader.nextString();
                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("description") && jsonReader.peek() != JsonToken.NULL)
                            description = jsonReader.nextString();
                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("rating") && jsonReader.peek() != JsonToken.NULL)
                            rating = (float) jsonReader.nextDouble();
                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("rateNum") && jsonReader.peek() != JsonToken.NULL)
                            rateNum = jsonReader.nextInt();
                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("latitude") && jsonReader.peek() != JsonToken.NULL) {
                            lat = (float) jsonReader.nextDouble();
                        } else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("longitude") && jsonReader.peek() != JsonToken.NULL) {
                            lon = (float) jsonReader.nextDouble();
                        } else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("startTime") && jsonReader.peek() != JsonToken.NULL)
                            startTime = new Timestamp(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'").parse(jsonReader.nextString()).getTime());
                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("finishTime") && jsonReader.peek() != JsonToken.NULL)
                            finishTime = new Timestamp(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'").parse(jsonReader.nextString()).getTime());
                        else jsonReader.skipValue();
                    }
                    jsonReader.endObject();
                    if (id > 0 && name != null)
                        result.add(new TripDestination(id, name, address, description, rating, rateNum, lat, lon, startTime, finishTime));
                }
                jsonReader.endArray();
                connection.disconnect();
            } else {
//                Log.w("httpRes","error");
                InputStream responseBody = connection.getErrorStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                String error = "Lỗi không xác định";
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("error") && jsonReader.peek() != JsonToken.NULL) {
                        error = jsonReader.nextString();
                    } else jsonReader.skipValue();
                }
                jsonReader.endObject();
                throw new Exception(error);
            }
        } catch (Exception e) {
            return new Result.Error(e);
        }
        return new Result.Success<>(result);
    }

    // An use .size() instead

    public Result<ArrayList<Destination>> fetchDestinations(String token, @Nullable String category) {
        try {
            //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//                // verify token
//            tokenVerifier(token);
//                String sqlQuery;
//                PreparedStatement statement;
//                if (category == null) {
//                    sqlQuery = "select id, [name], address, phone, description, city from Destination where category is null";
//                    statement = connection.prepareStatement(sqlQuery);
//                } else {
//                    sqlQuery = "select id, [name], address, phone, description, city from Destination where category = ?";
//                    statement = connection.prepareStatement(sqlQuery);
//                    statement.setString(1, category);
//                }
//                ResultSet res = statement.executeQuery();
//                if (!res.next()) throw new SQLException("Không tìm thấy điểm đến");
//                else {
//                    do {
//                        result1.add(new Destination(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), res.getString(5), category, res.getString(6), null));
//                    } while (res.next());
//                }
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");

            // Create URL
            URL endPoint;
            if (category == null)
                endPoint = new URL(server + "/destination");
            else endPoint = new URL(server + "/destination?category=" + category);
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    int id = -1, rateNum = 0;
                    float rating = 0, lat = 0, lon = 0;
                    String name = null, address = null, description = null;
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        if (jsonReader.nextName().equals("id") && jsonReader.peek() != JsonToken.NULL) {
                            id = jsonReader.nextInt();
                        } else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("name") && jsonReader.peek() != JsonToken.NULL) {
                            name = jsonReader.nextString();
                        } else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("address") && jsonReader.peek() != JsonToken.NULL)
                            address = jsonReader.nextString();
                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("description") && jsonReader.peek() != JsonToken.NULL)
                            description = jsonReader.nextString();
                        else jsonReader.skipValue();
//                        if (jsonReader.nextName().equals("inCost") && jsonReader.peek() != JsonToken.NULL)
//                            inCost = jsonReader.nextInt();
//                        else jsonReader.skipValue();
//                        if (jsonReader.nextName().equals("avgCost") && jsonReader.peek() != JsonToken.NULL)
//                            avgCost = jsonReader.nextInt();
//                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("rating") && jsonReader.peek() != JsonToken.NULL)
                            rating = (float) jsonReader.nextDouble();
                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("rateNum") && jsonReader.peek() != JsonToken.NULL)
                            rateNum = jsonReader.nextInt();
                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("latitude") && jsonReader.peek() != JsonToken.NULL)
                            lat = (float) jsonReader.nextDouble();
                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("longitude") && jsonReader.peek() != JsonToken.NULL)
                            lon = (float) jsonReader.nextDouble();
                        else jsonReader.skipValue();
                    }
                    jsonReader.endObject();
                    if (id > 0 && name != null)
                        result1.add(new Destination(id, name, address, description, category, rating, rateNum, lat, lon));
                }
                jsonReader.endArray();
                connection.disconnect();
            } else {
                InputStream responseBody = connection.getErrorStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                String error = "Lỗi không xác định";
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("error") && jsonReader.peek() != JsonToken.NULL) {
                        error = jsonReader.nextString();
                    } else jsonReader.skipValue();
                }
                jsonReader.endObject();
                throw new Exception(error);
            }
        } catch (Exception e) {
            return new Result.Error(e);
        }
        return new Result.Success<>(result1);
    }

    public Result<String> addTripDestination(String token, int tripID, int destinationID) {
        try {
            //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//                // verify token
//
//                String sqlQuery = "insert into Trip_Destination(tripId, destinationId) values (?,?)";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
//                statement.setInt(1, tripID);
//                statement.setInt(2, destinationID);
//                int res = statement.executeUpdate();
//                if (res > 0) stringResult = "Thêm địa điểm vào chuyến đi thành công";
//                else stringResult = "Không thể thêm địa điểm vào chuyến đi";
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");
//        } catch (JWTVerificationException e) {
//            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
            // Create URL
            URL endPoint = new URL(server + "/destination/trip/add");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String postData = "{\"tripId\":" + tripID + ", \"destinationId\":" + destinationID + "}";
            connection.getOutputStream().write(postData.getBytes());
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("message") && jsonReader.peek() != JsonToken.NULL) {
                        stringResult = jsonReader.nextString();
                    } else jsonReader.skipValue();
                }
                jsonReader.endObject();
                connection.disconnect();
                return new Result.Success<>(stringResult);
            } else {
//                Log.w("httpRes","error");
                InputStream responseBody = connection.getErrorStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                String error = "Lỗi không xác định";
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("error") && jsonReader.peek() != JsonToken.NULL) {
                        error = jsonReader.nextString();
                    } else jsonReader.skipValue();
                }
                jsonReader.endObject();
                throw new Exception(error);
            }
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }
    public Result<Weather> getCurrentWeather(String token, float lat, float lon){
        try {
            // Create URL
            URL endPoint = new URL(server + "/destination/weather?lat="+lat+"&lon="+lon);
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                jsonReader.beginObject();
                float temp = 0;
                String description = null, icon = null;
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("temp") && jsonReader.peek() != JsonToken.NULL) {
                        temp = (float) jsonReader.nextDouble();
                    } else jsonReader.skipValue();
                    if (jsonReader.nextName().equals("description") && jsonReader.peek() != JsonToken.NULL) {
                        description = jsonReader.nextString();
                    } else jsonReader.skipValue();
                    if (jsonReader.nextName().equals("icon") && jsonReader.peek() != JsonToken.NULL) {
                        icon = jsonReader.nextString();
                    } else jsonReader.skipValue();
                }
                jsonReader.endObject();
                connection.disconnect();
                return new Result.Success<>(new Weather(temp, description, icon));
            } else {
                InputStream responseBody = connection.getErrorStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                String error = "Lỗi không xác định";
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("error") && jsonReader.peek() != JsonToken.NULL) {
                        error = jsonReader.nextString();
                    } else jsonReader.skipValue();
                }
                jsonReader.endObject();
                throw new Exception(error);
            }
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }
    // TODO: write error handler function to reuse code
}