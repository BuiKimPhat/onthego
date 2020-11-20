package com.leobkdn.onthego.data;

import android.accounts.AuthenticatorException;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import androidx.annotation.Nullable;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.model.Trip;

import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
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
import java.util.Calendar;
import java.util.Date;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 * (Server class)
 */
public class LoginDataSource extends ServerData {

    // server credentials
//    private static final String secret = "@M1@j0K37oU?";
//    private static final String hostName = "10.20.2.181";
//    private static final String instance = "LEOTHESECOND";
//    private static final String port = "1433";
//    private static final String dbName = "OnTheGo";
//    private static final String dbUser = "user";
//    private static final String dbPassword = "userpass";
//    private static final String dbURI = "jdbc:jtds:sqlserver://" + hostName + ":" + port + ";instance=" + instance + ";user=" + dbUser + ";password=" + dbPassword + ";databasename=" + dbName;
//    private String token;
    private String stringResult = "Done";
    private LoggedInUser newUser;

    //    private String tokenGenerator(Integer UID) {
//        // create token by HS256 algorithm
//        // Payload: userID
//        // Exp: 1 year
//        try {
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.YEAR, 1);
//            Date expDate = calendar.getTime();
//            Algorithm algorithm = Algorithm.HMAC256(secret);
//            return JWT.create()
//                    .withClaim("uid", UID)
//                    .withKeyId(java.util.UUID.randomUUID().toString())
//                    .withExpiresAt(expDate)
//                    .sign(algorithm);
//        } catch (JWTCreationException exception) {
//            //Invalid Signing configuration / Couldn't convert Claims.
//            throw exception;
//        }
//    }
//    private String tokenVerifier(String token){
//        // veirfy if token meets the claims
//
//        try {
//            Algorithm algorithm = Algorithm.HMAC256(secret);
//            JWTVerifier verifier = JWT.require(algorithm)
//                    .build(); //Reusable verifier instance
//            DecodedJWT jwt = verifier.verify(token);
//            return jwt.getPayload();
//        } catch (JWTVerificationException exception){
//            //Invalid signature/claims
//            throw exception;
//        }
//    }
    public Result<LoggedInUser> login(String email, String password) {
        try {
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
            URL endPoint = new URL(server + "/user/login");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "On The Go");
            // Create the data
            String postData = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
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
        } catch (Exception e) {
            return new Result.Error(e);
        }
        return new Result.Success<>(newUser);
    }

    public Result<LoggedInUser> signUp(String email, String password, String fullName, @Nullable Date birthday, @Nullable String address) {
        //  request to create new user
        try {
            //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//
//                // check unique info
//                String sqlQuery = "select id from [User] where email = ?";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
//                statement.setString(1, email);
//                ResultSet checkEmail = statement.executeQuery();
//                if (checkEmail.next()) throw new SQLException("Email đã tồn tại!");
//
//                //  insert data to db
//                sqlQuery = "insert into [User] (email,[password],[name], createdAt, isAdmin, birthday, [address]) values (?, ?, ?, CURRENT_TIMESTAMP, 0, ?, ?)";
//                statement = connection.prepareStatement(sqlQuery);
//                statement.setString(1, email);
//                statement.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
//                statement.setString(3, fullName);
//                if (birthday != null) statement.setDate(4, new java.sql.Date(birthday.getTime()));
//                else statement.setDate(4, null);
//                statement.setString(5, address);
//                int rowInserted = statement.executeUpdate();
//
//                if (rowInserted > 0) {
//                    //if insert success
//                    //get last user id, name from database
//                    sqlQuery = "select id from [User] where id = IDENT_CURRENT('User')";
//                    statement = connection.prepareStatement(sqlQuery);
//                    ResultSet result = statement.executeQuery();
//                    result.next();
//                    int userID = result.getInt(1);
////                    String username = result.getString(2);
//
//                    // create token
//                    token = tokenGenerator(userID);
//
//                    //insert new token to database
//                    sqlQuery = "insert into [User_Token] (userId,token,createdAt) values (?, ?, CURRENT_TIMESTAMP)";
//                    statement = connection.prepareStatement(sqlQuery);
//                    statement.setInt(1, userID);
//                    statement.setString(2, token);
//                    int tokenNewRow = statement.executeUpdate();
//                    if (tokenNewRow > 0) {
//                        // if token inserted
//                        // set LoggedInUser
//                        newUser = new LoggedInUser(fullName, email, token, false, birthday, address); // LOGIN SUCCESS
//                    }
//                }
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");
// Create URL
            URL endPoint = new URL(server + "/user/signup");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "On The Go");
            // Create the data
            String postData = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\""
                    + fullName + "\",\"birthday\":" + (birthday != null ? birthday.getTime() : "null") + ",\"address\":"
                    + (address != null ? "\"" + address + "\"" : "null") + "}";
//            Log.w("httpReq",postData);
            // Enable writing
            connection.setDoOutput(true);
            // Write the data
//            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
//            writer.write(postData);
//            writer.close();
//            out.close();
//            connection.connect();
            connection.getOutputStream().write(postData.getBytes());
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
//                Log.w("httpRes","success");
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                String token = null;
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("token") && jsonReader.peek() != JsonToken.NULL) {
                        token = jsonReader.nextString();
                    } else jsonReader.skipValue();
                }
                jsonReader.endObject();
                newUser = new LoggedInUser(fullName, email, token, false, birthday, address); // LOGIN SUCCESS
                connection.disconnect();
                return new Result.Success<>(newUser);
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
                connection.disconnect();
                throw new Exception(error);
            }
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    public Result<String> logout(String token) {
        // revoke logout
        try {
            //decode jwt
//            DecodedJWT jwt = JWT.decode(token);
//            String base64payload = jwt.getPayload();
//            byte[] decoded = Base64.getDecoder().decode(base64payload);
//            String decodedString = new String(decoded);
//            JSONObject payload = new JSONObject(decodedString);

            //connect
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
////                Log.w("logout", "start connect");
//                // delete token
//                String sqlQuery = "delete from [User_Token] where token = ?";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
////                int userId = Integer.parseInt(payload.getString("uid"));
////                statement.setInt(1, userId);
//                statement.setString(1, token);
//                int tokenDeleted = statement.executeUpdate();
//                if (tokenDeleted > 0) {
//                    // if token deleted
//                    // return success
//                    stringResult = "Đã đăng xuất!";
//                } else {
//                    stringResult = "Không tìm thấy token. Đã cưỡng chế đăng xuất!";
//                }
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");
            // Create URL
            URL endPoint = new URL(server + "/user/logout");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
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
                connection.disconnect();
                throw new Exception(error);
            }
        } catch (Exception exception) {
            //Invalid token
            return new Result.Error(exception);
        }
    }

    public Result<String> editInfo(LoggedInUser user) {
        try {
            //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//                // verify token
//                tokenVerifier(user.getToken());
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
            // Create URL
            URL endPoint = new URL(server + "/user/edit");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + user.getToken());
            connection.setRequestProperty("Content-Type", "application/json");
            String postData = "{\"email\":\"" + user.getEmail() + "\",\"name\":\""
                    + user.getDisplayName() + "\",\"birthday\":" + (user.getBirthday() != null ? user.getBirthday().getTime() : "null") + ",\"address\":"
                    + (user.getAddress() != null ? "\"" + user.getAddress() + "\"" : "null") + "}";
            connection.setDoOutput(true);
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
                connection.disconnect();
                throw new Exception(error);
            }
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    public Result<String> changePassword(String token, String oldPassword, String newPassword) {
        try {
            //Set connection
//            Connection connection = DriverManager.getConnection(dbURI);
//            if (connection != null) {
//                // verify token
//                tokenVerifier(token);
//
//                String sqlQuery = "select id, [password] from [User] where id in (select userId from [User_Token] where token = ?)";
//                PreparedStatement statement = connection.prepareStatement(sqlQuery);
//                statement.setString(1, token);
//                ResultSet creds = statement.executeQuery();
//                if (!creds.next()) throw new SQLException("Không tìm thấy token này");
//                if (!BCrypt.checkpw(oldPassword, creds.getString(2)))
//                    throw new AuthenticatorException("Sai mật khẩu");
//                else {
//                    int userID = creds.getInt(1);
//                    sqlQuery = "update [User] set [password] = ? where id = ?";
//                    statement = connection.prepareStatement(sqlQuery);
//                    statement.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
//                    statement.setInt(2, userID);
//                    int recordUpdated = statement.executeUpdate();
//                    if (recordUpdated > 0) {
//                        // if user updated
//                        // return success
//                        stringResult = "Đổi mật khẩu thành công!";
//                    } else {
//                        stringResult = "Không tìm thấy người dùng!";
//                    }
//                }
//                connection.close();
//            } else throw new SQLException("Lỗi kết nối");
//        } catch (JWTVerificationException e) {
//            return new Result.Error(new Exception("Token đã hết hạn, vui lòng đăng nhập lại!"));
            // Create URL
            URL endPoint = new URL(server + "/user/edit/pwd");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", "application/json");
            String postData = "{\"oldPwd\":\"" + oldPassword + "\",\"newPwd\":\"" + newPassword + "\"}";
            connection.setDoOutput(true);
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
                connection.disconnect();
                throw new Exception(error);
            }
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }
}