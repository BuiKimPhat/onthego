package com.leobkdn.onthego.ui.go;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.ui.go.info.TripInfo;

import java.util.ArrayList;

public class GoActivity extends AppCompatActivity {
    private boolean isFABOpen;
    private TripResult tripResult = new TripResult();
    private TextView newTripLabel;
    private TextView existTripLabel;
    private FloatingActionButton addBtn;
    private LinearLayout newTrip;
    private FloatingActionButton newTripBtn;
    private LinearLayout existTrip;
    private FloatingActionButton existTripBtn;
    private ProgressBar progressBar;
    private ListView tripView;
    private ArrayList<Trip> trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go);

        progressBar = findViewById(R.id.tripLoading);
        newTripLabel = findViewById(R.id.newTripLabel);
        existTripLabel = findViewById(R.id.existTripLabel);
        addBtn = findViewById(R.id.addTripFab);
        newTrip = findViewById(R.id.newTrip);
        newTripBtn = findViewById(R.id.newTripBtn);
        existTrip = findViewById(R.id.existTrip);
        existTripBtn = findViewById(R.id.existTripBtn);
        tripView = findViewById(R.id.trip_listView);
        Context actCon = this;
        tripResult.getTripResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                progressBar.setVisibility(View.GONE);
                if (result instanceof Result.Success) {
                    trips = ((Result.Success<ArrayList<Trip>>) result).getData();
                    if (trips != null){
                        TextView emptyWarning = findViewById(R.id.trip_empty);
                        emptyWarning.setVisibility(View.GONE);
                        tripView.setAdapter(new TripListAdapter(actCon, trips));
                    }
                }
                else {
                    trips = new ArrayList<>();
                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                tripResult.fetchUserTrip(restorePrefsData("token"));
            }
        }).start();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFABOpen){
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        newTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actCon, TripInfo.class);
                startActivity(intent);
            }
        });

    }

    private void showFABMenu(){
        isFABOpen = true;
        addBtn.animate().rotationBy(45);
        newTrip.animate().translationY(-350);
        existTrip.animate().translationY(-190);
        newTripLabel.setVisibility(View.VISIBLE);
        existTripLabel.setVisibility(View.VISIBLE);
    }
    private void closeFABMenu(){
        isFABOpen = false;
        addBtn.animate().rotationBy(-45);
        newTrip.animate().translationY(0);
        existTrip.animate().translationY(0);
        newTripLabel.setVisibility(View.GONE);
        existTripLabel.setVisibility(View.GONE);
    }
    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
}