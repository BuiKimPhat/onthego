package com.leobkdn.onthego.data.result;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.leobkdn.onthego.data.source.DestinationDataSource;
import com.leobkdn.onthego.data.model.Destination;

import java.util.ArrayList;

public class DestinationResult {
    private MutableLiveData<Result> destinationResult = new MutableLiveData<>();
    private DestinationDataSource destinationDataSource = new DestinationDataSource();

    public DestinationResult(DestinationDataSource destinationDataSource) {
        this.destinationDataSource = destinationDataSource;
    }
    public DestinationResult(){}

    public LiveData<Result> getDestinationResult() {
        return destinationResult;
    }

    public void fetchDestinations(String token, @Nullable String category){
        Result<ArrayList<Destination>> result = destinationDataSource.fetchDestinations(token, category);
        destinationResult.postValue(result);
    }
    public void addTripDestination(String token, int tripID, int destinationID){
        Result<String> result = destinationDataSource.addTripDestination(token, tripID, destinationID);
        destinationResult.postValue(result);
    }
}
