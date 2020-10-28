package com.leobkdn.onthego.ui.go.info;

import android.content.Context;

import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.TripDestination;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TripInfoDataPump {
    private ArrayList<TripDestination> data;

    public TripInfoDataPump(ArrayList<TripDestination> data) {
        this.data = data;
    }

    public HashMap<String, ArrayList<TripDestination>> getData() {
        HashMap<String, ArrayList<TripDestination>> listData = new HashMap<String, ArrayList<TripDestination>>();
        listData.put("Chuyến đi của bạn", data);
        for (int i = 0; i < data.size(); i++) {
            Date startDate = data.get(i).getStartTime();
            String startString = null;
            if (startDate != null) {
                startString = new SimpleDateFormat("dd/MM/yyyy").format(startDate);
            }
            if (startString != null){
                if (!listData.containsKey(startString)) listData.put(startString, new ArrayList<TripDestination>());
                listData.get(startString).add(data.get(i));
            }
        }
        // TODO: Tuyến đi khuyên dùng
        return listData;
    }
}
