package com.leobkdn.onthego.ui.destination;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.leobkdn.onthego.data.DestinationDataSource;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.TripDataSource;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.ui.go.Trip;

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
}
