package com.leobkdn.onthego.data;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.data.model.Trip;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Class that handles trip CRUD.
 * (Server class)
 */
public class TripDataSource extends ServerData {

    // server credentials
//    private static final String secret = "@M1@j0K37oU?";
//    private static final String hostName = "10.20.2.181";
//    private static final String instance = "LEOTHESECOND";
//    private static final String port = "1433";
//    private static final String dbName = "OnTheGo";
//    private static final String dbUser = "user";
//    private static final String dbPassword = "userpass";
//    private static final String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";instance=" + instance + ";user=" + dbUser + ";password=" + dbPassword + ";databasename=" + dbName;
    private String stringResult = "Error";
    private int sum = 0 ; //Lưu số trip quey đươc
    private ArrayList<Trip> result = new ArrayList<Trip>();

//    private String tokenVerifier(String token) {
//        // verify if token meets the claims
//        try {
//            Algorithm algorithm = Algorithm.HMAC256(secret);
//            JWTVerifier verifier = JWT.require(algorithm)
//                    .build(); //Reusable verifier instance
//            DecodedJWT jwt = verifier.verify(token);
//            return jwt.getPayload();
//        } catch (JWTVerificationException exception) {
//            //Invalid signature/claims
//            throw exception;
//        }
//    }

    //    public Result<String> addUserTrip(String token, int tripId){
//
//    }
//    public Result<String> addUserTrip(String token, String name, ArrayList<Destination> destinations){
//    }
    public Result<ArrayList<Trip>> fetchUserTrip(String token) {
        try {
            //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//                // verify token
//                tokenVerifier(token);
//                result = new ArrayList<Trip>();
//                String sqlQuery = "select Trip.id, Trip.[name], [User].[name] as [owner], Trip.createdAt from Trip join (select tripId from User_Trip where userId in (select userId from [User_Token] where token = ?)) as cUser on Trip.id = cUser.tripId join [User] on Trip.ownerId = [User].id";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
//                statement.setString(1, token);
//                ResultSet res = statement.executeQuery();
//                if (!res.next()) throw new SQLException("Không tìm thấy trip");
//                else {
//                    do {
//                        result.add(new Trip(res.getInt(1), res.getString(2), res.getString(3), res.getDate(4)));
//                    } while (res.next());
//                }
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");
//        } catch (JWTVerificationException e) {
//            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
            // Create URL
            URL endPoint = new URL(server + "/trip");
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
                    int id = -1;
                    String name = null, owner = null;
                    Timestamp createdAt = null;
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        if (jsonReader.nextName().equals("id") && jsonReader.peek() != JsonToken.NULL) {
                            id = jsonReader.nextInt();
                            sum++;
                        } else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("name") && jsonReader.peek() != JsonToken.NULL) {
                            name = jsonReader.nextString();
                        } else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("owner") && jsonReader.peek() != JsonToken.NULL)
                            owner = jsonReader.nextString();
                        else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("createdAt") && jsonReader.peek() != JsonToken.NULL)
                            createdAt = new Timestamp(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'").parse(jsonReader.nextString()).getTime());
                        else jsonReader.skipValue();
                    }
                    jsonReader.endObject();
                    if (id > 0 && name != null)
                        result.add(new Trip(id, name, owner, createdAt));
                }
                jsonReader.endArray();
                connection.disconnect();
                return new Result.Success<>(result);
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
    // An
    public int getSum() {
        return sum;
    }

    public Result<String> addTrip(String token, int tripId) {
        try {
            //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//                // verify token
//                tokenVerifier(token);
//
//                // check unique info
//                String sqlQuery = "select userId from [User_Token] where token = ?";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
//                statement.setString(1, token);
//                ResultSet creds = statement.executeQuery();
//                if (!creds.next()) throw new SQLException("Không tồn tại token");
//                sqlQuery = "insert into User_Trip values (?,?)";
//                statement = connection.prepareStatement(sqlQuery);
//                statement.setInt(1, creds.getInt(1));
//                statement.setInt(2, tripId);
//                int count = statement.executeUpdate();
//                if (count <= 0) throw new SQLException("Không thể thêm trip");
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");
//        } catch (JWTVerificationException e) {
//            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
            // Create URL
            URL endPoint = new URL(server + "/trip/add");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String postData = "{\"tripId\":" + tripId + "}";
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

    public Result<String> addTrip(String token, String newTripName, ArrayList<TripDestination> destinations) {
        try {
            //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//                // verify token
//                tokenVerifier(token);
//
//                // check unique info
//                String sqlQuery = "select userId from [User_Token] where token = ?";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
//                statement.setString(1, token);
//                ResultSet creds = statement.executeQuery();
//                if (!creds.next()) throw new SQLException("Không tồn tại token");
//
//                sqlQuery = "insert into Trip (ownerId, [name], createdAt) values (?,?, CURRENT_TIMESTAMP)";
//                statement = connection.prepareStatement(sqlQuery);
//                statement.setInt(1, creds.getInt(1));
//                statement.setString(2, newTripName);
//                int count = statement.executeUpdate();
//                if (count <= 0) throw new SQLException("Không thể thêm trip");
//
//                sqlQuery = "insert into Trip_Destination(tripId, destinationId, startTime, finishTime) values (IDENT_CURRENT('Trip'),?,?,?)";
//                statement = connection.prepareStatement(sqlQuery);
//                for (int i=0;i<destinations.size();i++){
//                    statement.setInt(1, destinations.get(i).getId());
//                    statement.setDate(2, destinations.get(i).getStartTime() != null ? new Date(destinations.get(i).getStartTime().getTime()) : null);
//                    statement.setDate(3, destinations.get(i).getFinishTime() != null ? new Date(destinations.get(i).getFinishTime().getTime()): null);
//                    int countDes = statement.executeUpdate();
//                    if (countDes <= 0) throw new SQLException("Không thể thêm trip");
//                }
//
//                sqlQuery = "insert into User_Trip values (?,IDENT_CURRENT('Trip'))";
//                statement = connection.prepareStatement(sqlQuery);
//                statement.setInt(1, creds.getInt(1));
//                int countUT = statement.executeUpdate();
//                if (countUT <= 0) throw new SQLException("Không thể thêm trip");
//
//                stringResult = "Tạo chuyến đi thành công!";
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");
//        } catch (JWTVerificationException e) {
//            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
            // Create URL
            URL endPoint = new URL(server + "/trip/add");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String postData = "{\"name\":\"" + newTripName + "\"}";
            if (!destinations.isEmpty()) {
                postData = "{\"name\":\"" + newTripName + "\",\"destinations\": [";
                for (int i = 0; i < destinations.size() - 1; i++) {
                    postData += "{\"id\":" + destinations.get(i).getId()
                            + ",\"startTime\":"
                            + (destinations.get(i).getStartTime() != null ? destinations.get(i).getStartTime().getTime() : "null")
                            + ",\"finishTime\":"
                            + (destinations.get(i).getFinishTime() != null ? destinations.get(i).getFinishTime().getTime() : "null")
                            + "},";
                }
                postData += "{\"id\":" + destinations.get(destinations.size() - 1).getId()
                        + ",\"startTime\":"
                        + (destinations.get(destinations.size() - 1).getStartTime() != null ? destinations.get(destinations.size() - 1).getStartTime().getTime() : "null")
                        + ",\"finishTime\":"
                        + (destinations.get(destinations.size() - 1).getFinishTime() != null ? destinations.get(destinations.size() - 1).getFinishTime().getTime() : "null")
                        + "}]}";
            }
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

    public Result<String> editTrip(String token, int tripId, String newTripName, ArrayList<TripDestination> destinations) {
        try {
            // Create URL
            URL endPoint = new URL(server + "/trip/edit");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String postData = "{\"id\":" + tripId + ",\"name\":\"" + newTripName + "\"}";
            if (!destinations.isEmpty()) {
                postData = "{\"id\":" + tripId + ",\"name\":\"" + newTripName + "\",\"destinations\": [";
                for (int i = 0; i < destinations.size() - 1; i++) {
                    postData += "{\"id\":" + destinations.get(i).getId()
                            + ",\"startTime\":"
                            + (destinations.get(i).getStartTime() != null ? destinations.get(i).getStartTime().getTime() : "null")
                            + ",\"finishTime\":"
                            + (destinations.get(i).getFinishTime() != null ? destinations.get(i).getFinishTime().getTime() : "null")
                            + "},";
                }
                postData += "{\"id\":" + destinations.get(destinations.size() - 1).getId()
                        + ",\"startTime\":"
                        + (destinations.get(destinations.size() - 1).getStartTime() != null ? destinations.get(destinations.size() - 1).getStartTime().getTime() : "null")
                        + ",\"finishTime\":"
                        + (destinations.get(destinations.size() - 1).getFinishTime() != null ? destinations.get(destinations.size() - 1).getFinishTime().getTime() : "null")
                        + "}]}";
            }
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

    public Result<String> deleteTrip(String token, int tripId) {
        try {
            // Create URL
            URL endPoint = new URL(server + "/trip/delete");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String postData = "{\"tripId\":" + tripId + "}";
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
                return new Result.Success(stringResult);
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
}