package com.leobkdn.onthego.ui.go.info;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.ui.destination.DestinationActivity;
import com.leobkdn.onthego.ui.go.TripResult;

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
    private ArrayList<TripDestination> destinations = new ArrayList<>();
    private TripDestinationResult tripDestinationResult = new TripDestinationResult();
    private TripResult tripResult = new TripResult();
    private TripInfoAdapter adapter;
    private Context actCon = this;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int destinationID = data.getIntExtra("destinationID", -1);
                String destinationName = data.getStringExtra("destinationName");
                for (int i = 0; i < destinations.size(); i++)
                    if (destinations.get(i).getId() == destinationID) return;
                destinations.add(new TripDestination(destinationID, destinationName, null, null));
                if (destinations != null) {
                    LinkedHashMap<String, ArrayList<TripDestination>> map = new TripInfoDataPump(destinations).getData();
                    adapter = new TripInfoAdapter(actCon, new ArrayList<String>(map.keySet()), map);
                    listDestinations.setAdapter(adapter);
                    for (int i = 0; i < map.keySet().size(); i++)
                        listDestinations.expandGroup(i);
                }
            }
        }
    }

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
                startActivityForResult(intent, 1);
            }
        });
        // TODO: text listener for start and finish time to change destinations
        tripNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isTextValid(tripNameEdit.getText().toString()))
                    tripNameEdit.setError("Không được bỏ trống");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        tripResult.newTrip(restorePrefsData("token"), tripNameEdit.getText().toString(), destinations);
                    }
                }).start();
            }
        });

        tripDestinationResult.getDestinationResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                progressBar.setVisibility(View.GONE);
                if (result instanceof Result.Success) {
                    if (((Result.Success) result).checkTypeString()) {
                        Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                    } else {
                        destinations = ((Result.Success<ArrayList<TripDestination>>) result).getData();
                        if (destinations != null) {
                            LinkedHashMap<String, ArrayList<TripDestination>> data = new TripInfoDataPump(destinations).getData();
                            adapter = new TripInfoAdapter(actCon, new ArrayList<String>(data.keySet()), data);
                            listDestinations.setAdapter(adapter);
                            for (int i = 0; i < data.keySet().size(); i++)
                                listDestinations.expandGroup(i);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        tripResult.getTripResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    private boolean isTextValid(String text) {
        if (text == null) return false;
        return !text.trim().isEmpty();
    }
}