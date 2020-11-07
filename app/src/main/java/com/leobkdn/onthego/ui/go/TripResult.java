package com.leobkdn.onthego.ui.go;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.TripDataSource;

import java.util.ArrayList;

public class TripResult {
    private MutableLiveData<Result> tripResult = new MutableLiveData<>();
    private TripDataSource tripDataSource = new TripDataSource();

    TripResult(TripDataSource tripDataSource) {
        this.tripDataSource = tripDataSource;
    }
    TripResult(){}

    public LiveData<Result> getTripResult() {
        return tripResult;
    }

    public void fetchUserTrip(String token){
        Result<ArrayList<Trip>> result = tripDataSource.fetchUserTrip(token);
        tripResult.postValue(result);
    }

    public void addUserTrip(String token, int tripId){
        Result<ArrayList<Trip>> result = tripDataSource.addUserTrip(token, tripId);
        tripResult.postValue(result);
    }
    public void addUserTrip(String token, Trip newTrip){
        Result<ArrayList<Trip>> result = tripDataSource.addUserTrip(token, newTrip);
        tripResult.postValue(result);
    }
}
