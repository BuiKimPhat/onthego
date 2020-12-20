package com.leobkdn.onthego.data.result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.leobkdn.onthego.data.source.DestinationDataSource;
import com.leobkdn.onthego.data.model.Weather;

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
