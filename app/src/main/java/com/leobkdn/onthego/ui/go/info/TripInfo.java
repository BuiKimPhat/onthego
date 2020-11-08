package com.leobkdn.onthego.ui.go.info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.ui.destination.DestinationActivity;
import com.leobkdn.onthego.ui.go.Trip;
import com.leobkdn.onthego.ui.go.TripListAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TripInfo extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView tripName;
    private EditText tripNameEdit;
    private ImageButton tripNameBtn;
    private TextView tripOwner;
    private Button deleteTrip;
    private Button confirmButton;
    private FloatingActionButton addDestination;
    private ExpandableListView listDestinations;
    private ArrayList<TripDestination> destinations;
    private TripDestinationResult tripDestinationResult = new TripDestinationResult();
    private TripInfoAdapter adapter;

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
        confirmButton = findViewById(R.id.trip_info_confirm);
        listDestinations = findViewById(R.id.trip_destinations_listView);
        addDestination = findViewById(R.id.trip_info_add_destination_fab);
        Context actCon = this;
        if (getIntent().getBooleanExtra("isNew", false)) {
            progressBar.setVisibility(View.GONE);
            confirmButton.setVisibility(View.VISIBLE);
            tripName.setVisibility(View.GONE);
            tripNameEdit.setVisibility(View.VISIBLE);
            tripNameBtn.setVisibility(View.GONE);
            tripNameEdit.requestFocus();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    tripDestinationResult.fetchTripDestination(restorePrefsData("token"), getIntent().getIntExtra("tripId", -1));
                }
            }).start();
        }
        tripNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripName.setVisibility(View.GONE);
                tripNameEdit.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.VISIBLE);
            }
        });
        deleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                finish();
            }
        });
        addDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actCon, DestinationActivity.class);
                intent.putExtra("mode", "add");
                startActivity(intent);
            }
        });

        tripNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isTextValid(tripNameEdit.getText().toString())) tripNameEdit.setError("Không được bỏ trống");
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
                if (!isTextValid(tripNameEdit.getText().toString())) {
                    tripNameEdit.setError("Không được bỏ trống");
                    return;
                }
                tripNameBtn.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.GONE);
                tripNameEdit.setVisibility(View.GONE);
                tripName.setText(tripNameEdit.getText());
                tripName.setVisibility(View.VISIBLE);
                if (adapter != null) {
                    for (int i = 0; i < adapter.getChildrenCount(0); i++) {
                        View child = adapter.getChildView(0, i);
                        EditText dateEdit = child.findViewById(R.id.trip_destination_date_edit);
                        TextView date = child.findViewById(R.id.trip_destination_date);
                        TextView startTime = child.findViewById(R.id.trip_destination_startTime);
                        EditText startTimeEdit = child.findViewById(R.id.trip_destination_startTime_edit);
                        TextView endTime = child.findViewById(R.id.trip_destination_endTime);
                        EditText endTimeEdit = child.findViewById(R.id.trip_destination_endTime_edit);
                        ImageButton timeEdit = child.findViewById(R.id.trip_time_edit_button);
                        timeEdit.setVisibility(View.VISIBLE);
                        date.setText(dateEdit.getText());
                        date.setVisibility(View.VISIBLE);
                        dateEdit.setVisibility(View.GONE);
                        startTime.setText(startTimeEdit.getText());
                        startTime.setVisibility(View.VISIBLE);
                        startTimeEdit.setVisibility(View.GONE);
                        endTime.setText(endTimeEdit.getText());
                        endTime.setVisibility(View.VISIBLE);
                        endTimeEdit.setVisibility(View.GONE);
                    }
                }
            }
        });

        tripDestinationResult.getDestinationResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                progressBar.setVisibility(View.GONE);
                if (result instanceof Result.Success) {
                    destinations = ((Result.Success<ArrayList<TripDestination>>) result).getData();
                    if (destinations != null) {
                        LinkedHashMap<String, ArrayList<TripDestination>> data = new TripInfoDataPump(destinations).getData();
                        adapter = new TripInfoAdapter(actCon, new ArrayList<String>(data.keySet()), data);
                        listDestinations.setAdapter(adapter);
                        for (int i = 0; i < data.keySet().size(); i++)
                            listDestinations.expandGroup(i);
                    }
                } else {
                    destinations = new ArrayList<>();
                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    private boolean isTextValid(String text){
        if (text == null) return false;
        return !text.trim().isEmpty();
    }
}