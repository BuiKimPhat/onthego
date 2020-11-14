package com.leobkdn.onthego.data;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.ui.modify_user.list.Users_class;

import android.util.JsonReader;
import android.util.JsonToken;

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

public class ListUserDataSource extends ServerData {
//    private static final String secret = "@M1@j0K37oU?";
//    private static final String hostName = "110.20.2.181";
//    private static final String instance = "LEOTHESECOND";
//    private static final String port = "1433";
//    private static final String dbName = "OnTheGo";
//    private static final String dbUser = "user";
//    private static final String dbPassword = "userpass";
//    private static final String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";instance=" + instance + ";user=" + dbUser + ";password=" + dbPassword + ";databasename=" + dbName;
    private ArrayList<Users_class> Users;
    private int sum = 0 ; // lưu số lượng users query được.
    private String stringResult = " Hello world";

    public ArrayList<Users_class> getListUsers(String token)  {
        try {
            URL endPoint = new URL(server + "/admin_User");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            if (connection.getResponseCode() == 200) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    int id = -1;
                    String name = null;
                    String email = null;
                    Timestamp startTime = null, finishTime = null;
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        if (jsonReader.nextName().equals("id") && jsonReader.peek() != JsonToken.NULL) {
                            id = jsonReader.nextInt();
                        } else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("name") && jsonReader.peek() != JsonToken.NULL) {
                            name = jsonReader.nextString();
                        } else jsonReader.skipValue();
                        if(jsonReader.nextName().equals("email") && jsonReader.peek() != JsonToken.NULL){
                            email = jsonReader.nextName();
                            Users.add(new Users_class(name,email,id));
                            sum++;
                        }else jsonReader.skipValue();
                    }
                    jsonReader.endObject();
                }
                jsonReader.endArray();
                connection.disconnect();
            }
        }catch ( Exception e){
            System.out.println(e);
        }
        return Users;
    }
    public int getSum(){
        return sum;
    }
    //lấy thông tin user
    public LoggedInUser getInfoUser(int id,String token){
        LoggedInUser newUser = null;
        try{
            URL url = new URL(server + "/getUserInfor");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);

            String postData = "{\"id\":\""+ id +"}";
            connection.setDoOutput(true);
            if (connection.getResponseCode() == 200) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                String name = null, address = null,email = null; token = null;
                boolean isAdmin = false;
                Date birthday = null;
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("name") && jsonReader.peek() != JsonToken.NULL) {
                        name = jsonReader.nextString();
                    } else jsonReader.skipValue();
                    if (jsonReader.nextName().equals("email") && jsonReader.peek() != JsonToken.NULL) {
                        email = jsonReader.nextString();
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
                return newUser;
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

        }catch (Exception e){
        }
        return newUser;
    }
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
