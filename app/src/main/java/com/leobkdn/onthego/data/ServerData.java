package com.leobkdn.onthego.data;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public abstract class ServerData {
    protected static String server = "http://34.126.123.226/api";

    protected static Exception errorReader(HttpURLConnection connection) throws Exception {
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
        return new Exception(error);
    }
}
