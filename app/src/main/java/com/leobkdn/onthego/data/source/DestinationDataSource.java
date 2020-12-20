package com.leobkdn.onthego.data.source;

import android.util.JsonReader;
import android.util.JsonToken;

import androidx.annotation.Nullable;

import com.leobkdn.onthego.data.ServerData;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.data.model.Weather;
import com.leobkdn.onthego.data.result.Result;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DestinationDataSource extends ServerData {

    public Result<ArrayList<TripDestination>> fetchTripDestination(String token, int tripId) {
        try {
            // Create URL
            URL endPoint = new URL(server + "/destination/trip/" + tripId);
            // Create connection
            HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();
            connection.setRequestProperty("User-Agent", "On The Go");
            connection.addRequestProperty("Authorization", "Bearer " + token);
            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
                ArrayList<TripDestination> result = new ArrayList();
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
                return new Result.Success<>(result);
            } else throw errorReader(connection);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    public Result<ArrayList<Destination>> fetchDestinations(String token, @Nullable String category) {
        try {
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
                ArrayList<Destination> result = new ArrayList<>();
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
                        result.add(new Destination(id, name, address, description, category, rating, rateNum, lat, lon));
                }
                jsonReader.endArray();
                connection.disconnect();
                return new Result.Success<>(result);
            } else throw errorReader(connection);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    public Result<String> addTripDestination(String token, int tripID, int destinationID) {
        try {
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

    public Result<Weather> getCurrentWeather(String token, float lat, float lon) {
        try {
            // Create URL
            URL endPoint = new URL(server + "/destination/weather?lat=" + lat + "&lon=" + lon);
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
            } else throw errorReader(connection);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }
}