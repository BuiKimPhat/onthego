package com.leobkdn.onthego.data.source;

import android.util.JsonReader;
import android.util.JsonToken;

import androidx.annotation.Nullable;

import com.leobkdn.onthego.data.ServerData;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.result.Result;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginDataSource extends ServerData {

    public Result<LoggedInUser> login(String email, String password) {
        try {
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
                connection.disconnect();
                return new Result.Success<>(new LoggedInUser(name, email, token, isAdmin, birthday, address));
            } else throw errorReader(connection);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    public Result<LoggedInUser> signUp(String email, String password, String fullName, @Nullable Date birthday, @Nullable String address) {
        //  request to create new user
        try {
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
            // Enable writing
            connection.setDoOutput(true);
            // Write the data
            connection.getOutputStream().write(postData.getBytes());
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
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
                connection.disconnect();
                return new Result.Success<>(new LoggedInUser(fullName, email, token, false, birthday, address));
            } else throw errorReader(connection);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    public Result<String> logout(String token) {
        // revoke logout
        try {
            // Create URL
            URL endPoint = new URL(server + "/user/logout");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                String stringResult = "Done";
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
            } else throw errorReader(connection);
        } catch (Exception exception) {
            return new Result.Error(exception);
        }
    }

    public Result<String> editInfo(LoggedInUser user) {
        try {
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
                String stringResult = "Done";
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
            } else throw errorReader(connection);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    public Result<String> changePassword(String token, String oldPassword, String newPassword) {
        try {
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
                String stringResult = "Done";
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
            } else throw errorReader(connection);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }
}