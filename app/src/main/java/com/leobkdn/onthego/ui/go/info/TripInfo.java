package com.leobkdn.onthego.ui.go.info;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
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

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private int tripID = -1;
    private boolean isNew = false;
    private Context actCon = this;

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
        tripID = getIntent().getIntExtra("tripId", -1);
        isNew = getIntent().getBooleanExtra("isNew", false);
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
        if (isNew) {
            deleteTrip.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            confirmButton.setVisibility(View.VISIBLE);
            tripName.setVisibility(View.GONE);
            tripNameEdit.setVisibility(View.VISIBLE);
            tripNameBtn.setVisibility(View.GONE);
            tripNameEdit.requestFocus();

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

                            if (dateEdit.getText().toString().equals("")) {
                                destinations.get(i).setStartTime(null);
                                destinations.get(i).setFinishTime(null);
                            } else {
                                try {
                                    String startString = dateEdit.getText().toString() + " " + (startTimeEdit.getText().toString().equals("") ? "00:00" : startTimeEdit.getText().toString());
                                    String endString = dateEdit.getText().toString() + " " + (endTimeEdit.getText().toString().equals("") ? "00:00" : endTimeEdit.getText().toString());
                                    destinations.get(i).setStartTime(new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(startString).getTime()));
                                    destinations.get(i).setFinishTime(new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(endString).getTime()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            tripResult.addTrip(restorePrefsData("token"), tripNameEdit.getText().toString(), destinations);
                        }
                    }).start();
                }
            });
        } else {
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

                            if (dateEdit.getText().toString().equals("")) {
                                destinations.get(i).setStartTime(null);
                                destinations.get(i).setFinishTime(null);
                            } else {
                                try {
                                    String startString = dateEdit.getText().toString() + " " + (startTimeEdit.getText().toString().equals("") ? "00:00" : startTimeEdit.getText().toString());
                                    String endString = dateEdit.getText().toString() + " " + (endTimeEdit.getText().toString().equals("") ? "00:00" : endTimeEdit.getText().toString());
                                    destinations.get(i).setStartTime(new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(startString).getTime()));
                                    destinations.get(i).setFinishTime(new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(endString).getTime()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            tripResult.editTrip(restorePrefsData("token"), tripID, tripNameEdit.getText().toString(), destinations);
                        }
                    }).start();
                }
            });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    tripDestinationResult.fetchTripDestination(restorePrefsData("token"), tripID);
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
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        tripResult.deleteTrip(restorePrefsData("token"), tripID);
                    }
                });
                setResult(RESULT_OK);
                finish();
            }
        });
        addDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //update edit date/time
                if (adapter != null) {
                    for (int i = 0; i < adapter.getChildrenCount(0); i++) {
                        View child = adapter.getChildView(0, i);
                        EditText dateEdit = child.findViewById(R.id.trip_destination_date_edit);
                        TextView date = child.findViewById(R.id.trip_destination_date);
                        TextView startTime = child.findViewById(R.id.trip_destination_startTime);
                        EditText startTimeEdit = child.findViewById(R.id.trip_destination_startTime_edit);
                        TextView endTime = child.findViewById(R.id.trip_destination_endTime);
                        EditText endTimeEdit = child.findViewById(R.id.trip_destination_endTime_edit);
                        date.setText(dateEdit.getText());
                        startTime.setText(startTimeEdit.getText());
                        endTime.setText(endTimeEdit.getText());
                        if (dateEdit.getText().toString().equals("")) {
                            destinations.get(i).setStartTime(null);
                            destinations.get(i).setFinishTime(null);
                        } else {
                            try {
                                String startString = dateEdit.getText().toString() + " " + (startTimeEdit.getText().toString().equals("") ? "00:00" : startTimeEdit.getText().toString());
                                String endString = dateEdit.getText().toString() + " " + (endTimeEdit.getText().toString().equals("") ? "00:00" : endTimeEdit.getText().toString());
                                destinations.get(i).setStartTime(new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(startString).getTime()));
                                destinations.get(i).setFinishTime(new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(endString).getTime()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                Intent intent = new Intent(actCon, DestinationActivity.class);
                intent.putExtra("mode", "add");
                startActivityForResult(intent, 1);
            }
        });
        if (!isTextValid(tripNameEdit.getText().toString())) {
            tripNameEdit.setError("Không được bỏ trống");
            confirmButton.setEnabled(false);
        } else confirmButton.setEnabled(true);
        tripNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isTextValid(tripNameEdit.getText().toString())) {
                    tripNameEdit.setError("Không được bỏ trống");
                    confirmButton.setEnabled(false);
                } else confirmButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
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
                if (result instanceof Result.Success){
                    if (isNew){
                        isNew = false;
                        deleteTrip.setVisibility(View.VISIBLE);
                        tripID = Integer.parseInt(result.toString().substring(34));
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

                                        if (dateEdit.getText().toString().equals("")) {
                                            destinations.get(i).setStartTime(null);
                                            destinations.get(i).setFinishTime(null);
                                        } else {
                                            try {
                                                String startString = dateEdit.getText().toString() + " " + (startTimeEdit.getText().toString().equals("") ? "00:00" : startTimeEdit.getText().toString());
                                                String endString = dateEdit.getText().toString() + " " + (endTimeEdit.getText().toString().equals("") ? "00:00" : endTimeEdit.getText().toString());
                                                destinations.get(i).setStartTime(new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(startString).getTime()));
                                                destinations.get(i).setFinishTime(new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(endString).getTime()));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tripResult.editTrip(restorePrefsData("token"), tripID, tripNameEdit.getText().toString(), destinations);
                                    }
                                }).start();
                            }
                        });
                    }
                }
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

    //TODO: write functions for init UI and listeners
}