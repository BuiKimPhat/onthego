package com.leobkdn.onthego.ui.destination;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.model.Destination;

import java.util.ArrayList;

public class DestinationActivity extends AppCompatActivity {
    private DestinationResult destinationResult = new DestinationResult();
    private ArrayList<Destination> destinations = new ArrayList<>();
    private ListView destinationList;
    private ProgressBar progressBar;
    private Context context = this;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        destinationResult.addTripDestination(restorePrefsData("token"), data.getIntExtra("tripID", -1), data.getIntExtra("destinationID", -1));
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        HttpResponseCache httpCache = HttpResponseCache.getInstalled();
        Log.w("HTTP cache", "cache hits: " + httpCache.getHitCount());
        progressBar = findViewById(R.id.destinationsLoading);
        destinationList = findViewById(R.id.destinations_listView);
        destinationResult.getDestinationResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                progressBar.setVisibility(View.GONE);
                if (result instanceof Result.Success) {
                    if (((Result.Success) result).checkTypeString()) {
                        Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                    } else {
                        destinations = ((Result.Success<ArrayList<Destination>>) result).getData();
                        if (destinations != null) {
                            destinationList.setAdapter(new DestinationListAdapter(context, getIntent(), destinations));
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                destinationResult.fetchDestinations(restorePrefsData("token"), null);
            }
        }).start();
    }

    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
}