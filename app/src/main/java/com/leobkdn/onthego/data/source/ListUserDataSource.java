package com.leobkdn.onthego.data.source;

import com.leobkdn.onthego.data.ServerData;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.ui.modify_user.list.Users_class;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
// An
public class ListUserDataSource extends ServerData {
    private ArrayList<Users_class> Users;
    private int sum = 0; // lưu số lượng users query được.
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
                    String name = "1", email = "1";
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

    public String editInfo2(LoggedInUser user,int position) {
        String result="Failed";
        try {
            // Create URL
            URL endPoint = new URL(server + "/user/edit2");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + user.getToken());
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            String postData = "{"+"\"id\":\"" + position+ "\","  +"\"email\":\"" + user.getEmail() + "\",\"name\":\""
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
                        result = jsonReader.nextString();
                    } else jsonReader.skipValue();
                }
                jsonReader.endObject();
                connection.disconnect();
                return result;
            } else throw errorReader(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
                    while (jsonReader.hasNext()) {
                        if (jsonReader.nextName().equals("numOfUsers"))
                            sum = jsonReader.nextInt();
                    }
                }
            }
        } catch (Exception e) {
            return "" + e;
        }
        return " " + sum;
    }

    public int getDestinationCount(String token) {
        int destinationCount = 0;
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
        int tripCount = 0;
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
                        if (jsonReader.nextName().equals("numOfTrips") && jsonReader.peek() != JsonToken.NULL)
                            tripCount = jsonReader.nextInt();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return tripCount;
    }

    //lấy thông tin user
    public LoggedInUser getInfoUser(int id, String token1) {
        LoggedInUser newUser = new LoggedInUser();
        try {
            URL url = new URL(server + "/user/getUserInfor/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token1);
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
                    while (jsonReader.hasNext()) {
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
                        if (jsonReader.nextName().equals("birthday") && jsonReader.peek() != JsonToken.NULL) {
                            birthday = new SimpleDateFormat("yyyy-MM-dd").parse(jsonReader.nextString());
                            newUser.setBirthday(birthday);
                        } else jsonReader.skipValue();
                        if (jsonReader.nextName().equals("address") && jsonReader.peek() != JsonToken.NULL) {
                            address = jsonReader.nextString();
                            newUser.setAddress(address);
                        } else jsonReader.skipValue();
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
            e.printStackTrace();
        }
        return newUser;
    }

    public boolean deleteUser(int id, String token) {
        try {
            URL url = new URL(server + "/user/deleteUser/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            if (connection.getResponseCode() == 200) {
                String result;
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                if (jsonReader.hasNext())
                    if (jsonReader.nextName().equals("message") && jsonReader.peek() != JsonToken.NULL) {
                        result = jsonReader.nextString();
                        if (!result.equals("Xóa người dùng không thành công")) return true;
                    }
            }
        } catch (Exception e) {
        }
        return false;
    }
}
