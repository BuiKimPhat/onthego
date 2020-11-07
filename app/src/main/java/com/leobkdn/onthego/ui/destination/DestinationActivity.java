package com.leobkdn.onthego.ui.destination;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.ui.go.Trip;
import com.leobkdn.onthego.ui.go.TripListAdapter;
import com.leobkdn.onthego.ui.go.TripResult;

import java.util.ArrayList;

public class DestinationActivity extends AppCompatActivity {
    private DestinationResult destinationResult = new DestinationResult();
    private ArrayList<Destination> destinations;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        progressBar = findViewById(R.id.destinationsLoading);
        destinationList = findViewById(R.id.destinations_listView);
        destinationResult.getDestinationResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                progressBar.setVisibility(View.GONE);
                if (result instanceof Result.Success) {
                    destinations = ((Result.Success<ArrayList<Destination>>) result).getData();
                    if (destinations != null){
                        destinationList.setAdapter(new DestinationListAdapter(context, destinations));
                    }
                }
                else {
                    destinations = new ArrayList<>();
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