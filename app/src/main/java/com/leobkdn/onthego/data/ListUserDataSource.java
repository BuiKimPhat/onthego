package com.leobkdn.onthego.data;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.ui.modify_user.list.Users_class;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class ListUserDataSource extends ServerData {
    //    private static final String secret = "@M1@j0K37oU?";
//    private static final String hostName = "110.20.2.181";
//    private static final String instance = "LEOTHESECOND";
//    private static final String port = "1433";
//    private static final String dbName = "OnTheGo";
//    private static final String dbUser = "user";
//    private static final String dbPassword = "userpass";
//    private static final String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";instance=" + instance + ";user=" + dbUser + ";password=" + dbPassword + ";databasename=" + dbName;
//    private ArrayList<Users_class> Users;
    private int sum; // lưu số lượng users query được.
    private int tripCount = 0;
    private int destinationCount = 0;
    private String stringResult = " Hello world";

    public ArrayList<Users_class> getListUsers(String token) {
        ArrayList<Users_class> Users = new ArrayList<Users_class>();
        try {
            URL endPoint = new URL(server + "/user/admin_User");
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
                    String name="1",email="1";
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String Name = jsonReader.nextName();
                            if (Name.equals("id") && jsonReader.peek() != JsonToken.NULL) {
                                id = jsonReader.nextInt();
                            } else jsonReader.skipValue();
                        Name = jsonReader.nextName();
                            if (Name.equals("name") && jsonReader.peek() != JsonToken.NULL) {
                                name = jsonReader.nextString();
                            } else jsonReader.skipValue();
                        Name = jsonReader.nextName();
                            if (Name.equals("email") && jsonReader.peek() != JsonToken.NULL) {
                                email = jsonReader.nextString();
                            } else jsonReader.skipValue();
                        Users.add(new Users_class(name, email, id));
                    }
                    jsonReader.endObject();
                }
                jsonReader.endArray();
                connection.disconnect();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return Users;
    }

    public String getSum(String token) {
        try {
            URL endPoint = new URL(server + "/user/getUserCount");
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
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()){
                    if (jsonReader.nextName().equals("numOfUsers"))
                        sum = jsonReader.nextInt();
                    }
                }
            }
        } catch (Exception e) {
            return ""+e;
        }
        return " " +sum;
    }

    public int getDestinationCount(String token) {
        try {
            URL endPoint = new URL(server + "/destination/getDestinationCount");
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
                    jsonReader.beginObject();
                    while (jsonReader.hasNext())
                    if (jsonReader.nextName().equals("numOfDestinations") && jsonReader.peek() != JsonToken.NULL)
                        destinationCount = jsonReader.nextInt();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return destinationCount;
    }

    public int getTripCount(String token) {
        try {
            URL endPoint = new URL(server + "/trip/getTripCount");
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
                    jsonReader.beginObject();
                    while (jsonReader.hasNext())
                    if (jsonReader.nextName().equals("numOfTrip") && jsonReader.peek() != JsonToken.NULL)
                        tripCount = jsonReader.nextInt();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return tripCount;
    }

    //lấy thông tin user
    public LoggedInUser getInfoUser(int id,String token1) {
        LoggedInUser newUser = new LoggedInUser();
        try {
            URL url = new URL(server + "/user/getUserInfor/"+id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer "+token1);
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                String name = null, address = null, email = null;
                String token = null;
                boolean isAdmin = false;
                Date birthday = null;
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()){
                        Log.i(TAG, "getInfoUser: request success");
                    if (jsonReader.nextName().equals("name") && jsonReader.peek() != JsonToken.NULL) {
                        name = jsonReader.nextString();
                        newUser.setDisplayName(name);
                    } else jsonReader.skipValue();
                    if (jsonReader.nextName().equals("email") && jsonReader.peek() != JsonToken.NULL) {
                        email = jsonReader.nextString();
                        newUser.setEmail(email);
                    } else jsonReader.skipValue();
                    if (jsonReader.nextName().equals("isAdmin") && jsonReader.peek() != JsonToken.NULL) {
                        isAdmin = jsonReader.nextBoolean();
                        newUser.setAdmin(isAdmin);
                    } else jsonReader.skipValue();
                    if (jsonReader.nextName().equals("birthday") && jsonReader.peek() != JsonToken.NULL){
                        birthday = new SimpleDateFormat("yyyy-MM-dd").parse(jsonReader.nextString());
                        newUser.setBirthday(birthday);}
                    else jsonReader.skipValue();
                    if (jsonReader.nextName().equals("address") && jsonReader.peek() != JsonToken.NULL){
                        address = jsonReader.nextString();
                        newUser.setAddress(address);}
                    else jsonReader.skipValue();
                    if (jsonReader.nextName().equals("token") && jsonReader.peek() != JsonToken.NULL)
                        token = jsonReader.nextString();
                    else jsonReader.skipValue();
                    }
                }
                LoggedInUser newUser1 = new LoggedInUser(name, email, token, isAdmin, birthday, address); // LOGIN SUCCESS
                return newUser1;
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
            Log.i(TAG, "getInfoUser: "+e);
        }
        return newUser;
    }

    public boolean deleteUser(int id, String token) {
        try {
            URL url = new URL(server + "/deleteUser/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            if (connection.getResponseCode() == 200) {
                String result;
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                if (jsonReader.hasNext())
                    if (jsonReader.nextName().equals("Delete") && jsonReader.peek() != JsonToken.NULL) {
                        result = jsonReader.nextString();
                        if (result.equals("success")) return true;
                        else return false;
                    }
            }
        } catch (Exception e) {
        }
        return false;
    }


//    public Result<String> editInfo(LoggedInUser user) {
//        try {
//            //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//
//                String sqlQuery = "update [User] set [name] = ?, email = ?, birthday = ?, [address] = ? where id in (select userId from [User_Token] where token = ?)";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
////                int userId = Integer.parseInt(payload.getString("uid"));
////                statement.setInt(1, userId);
////                statement.setString(1, key);
//                statement.setString(1, user.getDisplayName());
//                statement.setString(2, user.getEmail());
//                statement.setDate(3, new java.sql.Date(user.getBirthday().getTime()));
//                statement.setString(4, user.getAddress());
//                statement.setString(5, user.getToken());
//                int recordUpdated = statement.executeUpdate();
//                if (recordUpdated > 0) {
//                    // if user updated
//                    // return success
//                    stringResult = "Cập nhật thành công!";
//                } else {
//                    stringResult = "Không tìm thấy người dùng!";
//                }
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");
//        } catch (JWTVerificationException e) {
//            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
//        } catch (Exception e) {
//            return new Result.Error(e);
//        }
//        return new Result.Success<>(stringResult);
//    }
//}
        //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//
//                // check unique info
//                String sqlQuery = "select id, [password] from [User] where email = ?";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
//                statement.setString(1, email);
//                ResultSet creds = statement.executeQuery();
//                if (!creds.next()) throw new SQLException("Không tồn tại email này");
//                if (!BCrypt.checkpw(password, creds.getString(2)))
//                    throw new AuthenticatorException("Sai mật khẩu");
//                else {
//                    Integer userID = creds.getInt(1);
//                    // create token
////                    token = tokenGenerator(userID);
//                    //insert new token to database
//                    sqlQuery = "insert into [User_Token] (userId,token,createdAt) values (?, ?, CURRENT_TIMESTAMP)";
//                    statement = connection.prepareStatement(sqlQuery);
//                    statement.setInt(1, userID);
//                    statement.setString(2, token);
//                    int tokenNewRow = statement.executeUpdate();
//                    if (tokenNewRow > 0) {
//                        // if token inserted
//                        // set LoggedInUser
//                        sqlQuery = "select [name], isAdmin, birthday, [address] from [User] where id = ?";
//                        statement = connection.prepareStatement(sqlQuery);
//                        statement.setInt(1, userID);
//                        ResultSet info = statement.executeQuery();
//                        info.next();
//                        newUser = new LoggedInUser(info.getString(1), email, token, info.getBoolean(2), info.getDate(3), info.getString(4)); // LOGIN SUCCESS
//                    }
//                }
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");
        // Create URL
public Result<LoggedInUser> loginFake (String email,String id){
        LoggedInUser newUser;
        try{
        URL endPoint = new URL(server + "/user/login_fake");
        // Create connection
        HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "On The Go");
        // Create the data
        String postData = "{\"email\":\"" + email + "\",\"id\":\"" + id + "\"}";
        // Enable writing
        connection.setDoOutput(true);
        // Write the data
        connection.getOutputStream().write(postData.getBytes());
        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
            InputStream responseBody = connection.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
            JsonReader jsonReader = new JsonReader(responseBodyReader);
            String name = null, address = null, token = null;
            boolean isAdmin = false;
            Date birthday = null;
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                if (jsonReader.nextName().equals("name") && jsonReader.peek() != JsonToken.NULL) {
                    name = jsonReader.nextString();
                } else jsonReader.skipValue();
                if (jsonReader.nextName().equals("isAdmin") && jsonReader.peek() != JsonToken.NULL) {
                    isAdmin = jsonReader.nextBoolean();
                } else jsonReader.skipValue();
                if (jsonReader.nextName().equals("birthday") && jsonReader.peek() != JsonToken.NULL)
                    birthday = new SimpleDateFormat("yyyy-MM-dd").parse(jsonReader.nextString());
                else jsonReader.skipValue();
                if (jsonReader.nextName().equals("address") && jsonReader.peek() != JsonToken.NULL)
                    address = jsonReader.nextString();
                else jsonReader.skipValue();
                if (jsonReader.nextName().equals("token") && jsonReader.peek() != JsonToken.NULL)
                    token = jsonReader.nextString();
                else jsonReader.skipValue();
            }
            jsonReader.endObject();
            newUser = new LoggedInUser(name, email, token, isAdmin, birthday, address); // LOGIN SUCCESS
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
        connection.disconnect();
    } catch(Exception e){
        return new Result.Error(e);
    }
        return new Result.Success<>(newUser);
    }
}
