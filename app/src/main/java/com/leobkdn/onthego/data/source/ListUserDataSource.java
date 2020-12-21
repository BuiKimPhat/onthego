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
    private int sum = 0 ; // lưu số lượng users query được.
    private String stringResult = " Hello world";

    public ArrayList<Users_class> getListUsers(String token)  {
        try {
            URL endPoint = new URL(server + "/admin_User");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
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
            connection.setRequestMethod("GET");
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
    public boolean deleteUser(int id,String token){
        try{
        URL url = new URL(server + "/deleteUser/"+id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "On The Go");
        connection.addRequestProperty("Authorization", "Bearer " + token);
        if (connection.getResponseCode() == 200){
            String result;
            InputStream responseBody = connection.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
            JsonReader jsonReader = new JsonReader(responseBodyReader);
            if (jsonReader.hasNext())
                if (jsonReader.nextName().equals("Delete") && jsonReader.peek() != JsonToken.NULL){
                    result = jsonReader.nextString();
                    if(result.equals("success")) return true;
                    else return false;
                }
        }
        }catch (Exception e){}
        return false;
    }
}
