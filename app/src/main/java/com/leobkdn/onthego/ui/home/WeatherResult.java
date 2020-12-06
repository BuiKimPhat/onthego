package com.leobkdn.onthego.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.leobkdn.onthego.data.DestinationDataSource;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.data.model.Weather;

import java.util.ArrayList;

public class WeatherResult {
    private MutableLiveData<Result> weatherResult = new MutableLiveData<>();
    private DestinationDataSource destinationDataSource = new DestinationDataSource();

    WeatherResult(DestinationDataSource destinationDataSource) {
        this.destinationDataSource = destinationDataSource;
    }

    public WeatherResult() {
    }

    public LiveData<Result> getWeatherResult() {
        return weatherResult;
    }

    public void getCurrentWeather(String token, float lat, float lon) {
        Result<Weather> result = destinationDataSource.getCurrentWeather(token, lat, lon);
        weatherResult.postValue(result);
    }
}
