package com.leobkdn.onthego.ui.go.info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.ui.go.Trip;
import com.leobkdn.onthego.ui.go.TripListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class TripInfo extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView tripName;
    private EditText tripNameEdit;
    private ImageButton tripNameBtn;
    private TextView tripOwner;
    private Button deleteTrip;
    private Button confirmButton;
    private ExpandableListView listDestinations;
    private ArrayList<TripDestination> destinations;
    private TripDestinationResult tripDestinationResult = new TripDestinationResult();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);

        progressBar = findViewById(R.id.trip_info_loading);
        tripName = findViewById(R.id.trip_name);
        tripNameEdit = findViewById(R.id.trip_name_edit);
        tripName.setText(getIntent().getStringExtra("tripName"));
        tripNameEdit.setText(getIntent().getStringExtra("tripName"));
        tripNameBtn = findViewById(R.id.trip_name_edit_button);
        tripOwner = findViewById(R.id.trip_owner_name);
        tripOwner.setText(getIntent().getStringExtra("tripOwner"));
        deleteTrip = findViewById(R.id.trip_delete);
        listDestinations = findViewById(R.id.trip_destinations_listView);
        Context actCon = this;
        tripDestinationResult.getDestinationResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                progressBar.setVisibility(View.GONE);
                if (result instanceof Result.Success) {
                    destinations = ((Result.Success<ArrayList<TripDestination>>) result).getData();
                    if (destinations != null) {
                        HashMap<String, ArrayList<TripDestination>> data = new TripInfoDataPump(destinations).getData();
                        listDestinations.setAdapter(new TripInfoAdapter(actCon, new ArrayList<String>(data.keySet()), data));
                    }
                } else {
                    destinations = new ArrayList<>();
                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                tripDestinationResult.fetchTripDestination(restorePrefsData("token"), getIntent().getIntExtra("tripId", -1));
            }
        }).start();
    }
    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
}