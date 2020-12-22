package com.leobkdn.onthego.data.source;

import android.util.JsonReader;
import android.util.JsonToken;

import com.leobkdn.onthego.data.ServerData;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.data.model.Trip;
import com.leobkdn.onthego.data.result.Result;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TripDataSource extends ServerData {

    public Result<ArrayList<Trip>> fetchUserTrip(String token) {
        try {
            // Create URL
            URL endPoint = new URL(server + "/trip");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/json");
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                ArrayList<Trip> result = new ArrayList<>();
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
                        result.add(new Trip(id, name, (owner != null ? owner : "Không rõ"), createdAt));
                }
                jsonReader.endArray();
                connection.disconnect();
                return new Result.Success<>(result);
            } else throw errorReader(connection);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }
    // An
    public ArrayList<Trip> getListTrip(String token) {
        ArrayList<Trip> trips = new ArrayList<Trip>();
        try{
        URL endPoint = new URL(server + "/trip/getListTrip");
        // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/json");
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                int id = -1;
                String name = "an", owner = "an";
                String ownerName = "an";
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("id") && jsonReader.peek() != JsonToken.NULL) {
                        id = jsonReader.nextInt();
                    } else jsonReader.skipValue();
                    if (jsonReader.nextName().equals("ownerId") && jsonReader.peek() != JsonToken.NULL) {
                        owner = ""+jsonReader.nextInt();
                    } else jsonReader.skipValue();
                    if (jsonReader.nextName().equals("name") && jsonReader.peek() != JsonToken.NULL){
                        name = jsonReader.nextString();
                    }
                    else jsonReader.skipValue();
                }
                jsonReader.endObject();
                trips.add(new Trip(id, name, owner));
            }
            jsonReader.endArray();
            return trips;
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
            trips.add(new Trip(0,"Faild","Faild"));
            return trips;
        }
    }
    public int getTripCout(String token){
        int num =-1;
        try{
            URL endPoint = new URL(server + "/trip/getListTrip");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/json");
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        if (jsonReader.nextName().equals("numOfTrips") && jsonReader.peek() != JsonToken.NULL) {
                            num = jsonReader.nextInt();
                        } else jsonReader.skipValue();
                    }
                    jsonReader.endObject();
                    return num;
                }
            }
        }catch (Exception err){
            err.printStackTrace();
            return num;
        }
        return -1;
}
    public Result<String> addTrip(String token, int tripId) {
        try {
            // Create URL
            URL endPoint = new URL(server + "/trip/add");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            String postData = "{\"tripId\":" + tripId + "}";
            connection.getOutputStream().write(postData.getBytes());
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                String stringResult = "Error";
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

    public Result<String> addTrip(String token, String newTripName, ArrayList<TripDestination> destinations) {
        try {
            // Create URL
            URL endPoint = new URL(server + "/trip/add");
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
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
                String stringResult = "Error";
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
            connection.setRequestProperty("Accept", "application/json");
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
                String stringResult = "Error";
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
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            String postData = "{\"tripId\":" + tripId + "}";
            connection.getOutputStream().write(postData.getBytes());
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                String stringResult = "Error";
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
            } else throw errorReader(connection);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }
}