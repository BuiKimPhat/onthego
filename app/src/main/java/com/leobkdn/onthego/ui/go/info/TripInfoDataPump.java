package com.leobkdn.onthego.ui.go.info;

import android.content.Context;
import android.util.Log;

import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.Trip;
import com.leobkdn.onthego.data.model.TripDestination;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;

public class TripInfoDataPump {
    private ArrayList<TripDestination> data;
    private final static double oo = 999999999;
    double BestConfig = oo;  // best cost
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
                if (startDate == dates.get(i)) {
                    if (!listData.containsKey(groupString))
                        listData.put(groupString, new ArrayList<>());
                    listData.get(groupString).add(data.get(j));
                }
            }
        }
        ArrayList<TripDestination> tsp = tsp(data);
        if (data.size() > 0) listData.put("Tuyến đường tối ưu ("+ (BestConfig != oo ? new DecimalFormat("##.##").format(BestConfig) : 0) + " km)", tsp);
        if (data.size() > 0) listData.put("Chỉnh sửa chuyến đi", data);

        // TODO: Tuyến đi khuyên dùng (TSP)
        return listData;
    }

    private ArrayList<TripDestination> tsp(ArrayList<TripDestination> source) {
//        Kĩ thuật nhánh cận dựa trên phương pháp quay lui
        ArrayList<TripDestination> result = new ArrayList<>();
        int dSize = source.size();
        double distances[][] = new double[dSize][dSize];
        for (int i = 0; i < dSize; i++) {
            float startLat = source.get(i).getLat(), startLon = source.get(i).getLon();
            for (int j = i; j < dSize; j++) {
                float endLat = source.get(j).getLat(), endLon = source.get(j).getLon();
                distances[i][j] = distance(startLat, endLat, startLon, endLon);
                if (i!=j) distances[j][i] = distances[i][j];
            }
        }

        int[] X = new int [dSize];   // store all possible ways
        int[] BestWay = new int [dSize]; // store best way
        double[] T = new double [dSize];   // store cost from x[0] to X[i]
        boolean[] Free = new boolean [dSize];    // Free[i] = True if not visit i

        // calling tsp backtracking function,  starting from 0 for easily manipulate with arrays
        initialize(dSize, X, BestWay, T, Free);
        attemp(distances, dSize, X, BestWay, T, Free,1);
//        print(dSize, BestWay, distances);

        for (int i=0;i<dSize;i++){
            result.add(data.get(BestWay[i]));
        }
        if (data.size() > 1) result.add(data.get(0));

        return result;
    }
    void initialize(int n, int[] X, int[] BestWay, double[] T, boolean[] Free) {
        for (int i = 0; i < n; i ++) {
            Free[i] = true;
            BestWay[i] = 0;
            X[i] = 0;
            T[i] = 0;
        }
        if (n > 0) Free[0] = false;    // start from vertice 0
        BestConfig = oo;
    }

    void attemp(double[][] C, int n, int[] X, int[] BestWay, double[] T, boolean[] Free, int i) {
        for (int j = 1; j < n; j ++) {
            if (Free[j]) {
                X[i] = j;
                T[i] = T[i - 1] + C[X[i - 1]][j];
                if (T[i] < BestConfig) {
                    if (i < n - 1) {
                        Free[j] = false;
                        attemp(C, n, X, BestWay, T, Free, i + 1);
                        Free[j] = true;
                    } else {
                        if (T[i] + C[X[i]][0] < BestConfig) {
                            for (int k = 0; k < n; k ++) {
                                BestWay[k] = X[k];
                            }
                            BestConfig = T[i] + C[X[i]][0];
                        }
                    }
                }
            }
        }
    }

//    void print(int n, int[] BestWay, double[][] c) {
//        String string = "";
//        for (int i = 0; i < n; i ++) {
//            for (int j = 0; j < n; j ++) {
//                string += c[i][j] + "  ";
//            }
//            string += "\n";
//        }
//        for (int i = 0; i < n; i ++) {
//            string += BestWay[i] + 1 + " -> ";
//        }
//        string += 1;
//        string += "\nCost: " + BestConfig;
//        Log.w("TSP", string);
//    }

    private double distance(float lat1, float lat2, float lon1, float lon2) {
        if (lat1==lat2 && lon1==lon2) return 0;
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = (float) Math.toRadians(lon1);
        lon2 = (float) Math.toRadians(lon2);
        lat1 = (float) Math.toRadians(lat1);
        lat2 = (float) Math.toRadians(lat2);

        // Haversine formula
        float dlon = lon2 - lon1;
        float dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;
        // calculate the result
        return (c * r);
    }
}
