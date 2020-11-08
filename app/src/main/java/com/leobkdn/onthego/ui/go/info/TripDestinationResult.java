package com.leobkdn.onthego.ui.go.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.leobkdn.onthego.data.DestinationDataSource;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.TripDataSource;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.ui.go.Trip;

import java.util.ArrayList;

public class TripDestinationResult {
    private MutableLiveData<Result> destinationResult = new MutableLiveData<>();
    private DestinationDataSource destinationDataSource = new DestinationDataSource();

    TripDestinationResult(DestinationDataSource destinationDataSource) {
        this.destinationDataSource = destinationDataSource;
    }

    TripDestinationResult() {
    }

    public LiveData<Result> getDestinationResult() {
        return destinationResult;
    }

    public void fetchTripDestination(String token, int tripId) {
        Result<ArrayList<TripDestination>> result = destinationDataSource.fetchTripDestination(token, tripId);
        destinationResult.postValue(result);
    }

}
