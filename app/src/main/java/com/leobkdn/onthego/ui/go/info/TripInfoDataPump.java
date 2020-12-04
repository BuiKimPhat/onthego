package com.leobkdn.onthego.ui.go.info;

import android.content.Context;

import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.TripDestination;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;

public class TripInfoDataPump {
    private ArrayList<TripDestination> data;

    public TripInfoDataPump(ArrayList<TripDestination> data) {
        this.data = data;
    }

    public LinkedHashMap<String, ArrayList<TripDestination>> getData() {
        LinkedHashMap<String, ArrayList<TripDestination>> listData = new LinkedHashMap<String, ArrayList<TripDestination>>();
        ArrayList<Date> dates = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Date startDate = data.get(i).getStartTime();
            if (startDate != null) {
                if (!dates.contains(startDate)) dates.add(startDate);
            }
        }
        Collections.sort(dates);
        for (int i = 0; i < dates.size(); i++) {
            String groupString = new SimpleDateFormat("dd/MM/yyyy").format(dates.get(i));
            for (int j = 0; j < data.size(); j++) {
                Date startDate = data.get(j).getStartTime();
                if (startDate == dates.get(i)){
                    if (!listData.containsKey(groupString)) listData.put(groupString, new ArrayList<>());
                    listData.get(groupString).add(data.get(j));
                }
            }
        }
        if (data.size() > 0) listData.put("Chỉnh sửa chuyến đi", data);

        // TODO: Tuyến đi khuyên dùng (TSP)
        return listData;
    }
}
