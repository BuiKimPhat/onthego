package com.leobkdn.onthego.ui.go;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.TripDataSource;
import com.leobkdn.onthego.data.model.Trip;
import com.leobkdn.onthego.data.model.TripDestination;

import java.util.ArrayList;

public class TripResult {
    private MutableLiveData<Result> tripResult = new MutableLiveData<>();
    private TripDataSource tripDataSource = new TripDataSource();

    TripResult(TripDataSource tripDataSource) {
        this.tripDataSource = tripDataSource;
    }
    public TripResult(){}

    public LiveData<Result> getTripResult() {
        return tripResult;
    }

    public void fetchUserTrip(String token){
        Result<ArrayList<Trip>> result = tripDataSource.fetchUserTrip(token);
        tripResult.postValue(result);
    }

    public void addTrip(String token, int tripId){
        Result<String> result = tripDataSource.addTrip(token, tripId);
        tripResult.postValue(result);
    }
    public void addTrip(String token, String newTripName, ArrayList<TripDestination> destinations){
        Result<String> result = tripDataSource.addTrip(token, newTripName, destinations);
        tripResult.postValue(result);
    }
    public void editTrip(String token, int tripId, String newTripName, ArrayList<TripDestination> destinations){
        Result<String> result = tripDataSource.editTrip(token, tripId, newTripName, destinations);
        tripResult.postValue(result);
    }
    public void deleteTrip(String token, int tripId){
        Result<String> result = tripDataSource.deleteTrip(token, tripId);
        tripResult.postValue(result);
    }
}
