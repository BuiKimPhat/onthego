package com.leobkdn.onthego.ui.go;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.Trip;
import com.leobkdn.onthego.tools.VNCharacterUtils;
import com.leobkdn.onthego.ui.destination.DestinationListAdapter;
import com.leobkdn.onthego.ui.go.info.TripInfo;

import java.text.Normalizer;
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
    private Spinner tripSort;
    private EditText search;
    private ProgressBar progressBar;
    private ListView tripView;
    private ArrayList<Trip> trips = new ArrayList<>();
    private ArrayList<Trip> display = new ArrayList<>();
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
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        // TODO: change to onRestart() to refresh data
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 3){
//            if (resultCode == RESULT_OK){
//                finish();
//                startActivity(getIntent());
//            }
//        }
//    }

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
        search = findViewById(R.id.trip_search);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
                display = linearSearch(trips, search.getText().toString());
                quickSortBy(display, 0, display.size() - 1, tripSort.getSelectedItem().toString());
                progressBar.setVisibility(View.GONE);
                tripView.setAdapter(new TripListAdapter(actCon, display));
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        tripSort = findViewById(R.id.trip_sort);
        // Create an ArrayAdapter using the cities string array and a default spinner layout
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this, R.array.sortTrips, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        tripSort.setAdapter(sortAdapter);
        tripSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                quickSortBy(display, 0, display.size() - 1, selectedItem);
                tripView.setAdapter(new TripListAdapter(actCon, display));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        tripResult.getTripResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                progressBar.setVisibility(View.GONE);
                if (result instanceof Result.Success) {
                    if (((Result.Success) result).checkTypeString()){
                        Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(getIntent());
                    } else {
                        trips = ((Result.Success<ArrayList<Trip>>) result).getData();
                        display = new ArrayList<>(trips);
                        if (trips != null && trips.size() > 0) {
                            TextView emptyWarning = findViewById(R.id.trip_empty);
                            emptyWarning.setVisibility(View.GONE);
                            TripListAdapter adapter = new TripListAdapter(actCon, display);
                            adapter.notifyDataSetChanged();
                            tripView.setAdapter(adapter);
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
                tripResult.fetchUserTrip(restorePrefsData("token"));
            }
        }).start();

        if (getIntent().getStringExtra("mode") != null && getIntent().getStringExtra("mode").equals("add")){
            addBtn.setVisibility(View.GONE);
            newTripBtn.setVisibility(View.GONE);
            existTripBtn.setVisibility(View.GONE);
        } else {
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFABOpen) {
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
                    intent.putExtra("isNew", true);
                    startActivityForResult(intent, 3);
                }
            });
            existTripBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dialog = new ExistTripAddDialog(restorePrefsData("token"), tripResult);
                    dialog.show(getSupportFragmentManager(), "existTripAdd");
                }
            });
        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        addBtn.animate().rotationBy(45).setDuration(100);
        newTrip.animate().translationY(-350);
        existTrip.animate().translationY(-190);
        newTripLabel.setVisibility(View.VISIBLE);
        existTripLabel.setVisibility(View.VISIBLE);
    }

    private void closeFABMenu() {
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
    private void quickSortBy(ArrayList<Trip> source, int start, int end, String by) {
        if (by.equals("Theo ngày thêm: Xa -> gần")) {
            if (start < end) {
                int i = start, j = end;
                long chot;
                chot = source.get((start + end) / 2).getCreatedAt() != null ? source.get((start + end) / 2).getCreatedAt().getTime() : -1; // chon phan tu o giua lam chot
                while (i < j) {
                    while ((source.get(i).getCreatedAt() != null ? source.get(i).getCreatedAt().getTime() : -1) < chot) i++;
                    while ((source.get(j).getCreatedAt() != null ? source.get(j).getCreatedAt().getTime() : -1) > chot) j--;
                    if (i <= j) {
                        Trip temp = new Trip(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo tên: A -> Z")) {
            if (start < end) {
                int i = start, j = end;
                String chot;
                chot = source.get((start + end) / 2).getName().toLowerCase(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getName().toLowerCase().compareTo(chot) < 0) i++;
                    while (source.get(j).getName().toLowerCase().compareTo(chot) > 0) j--;
                    if (i <= j) {
                        Trip temp = new Trip(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo tên: Z -> A")) {
            if (start < end) {
                int i = start, j = end;
                String chot;
                chot = source.get((start + end) / 2).getName().toLowerCase(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getName().toLowerCase().compareTo(chot) > 0) i++;
                    while (source.get(j).getName().toLowerCase().compareTo(chot) < 0) j--;
                    if (i <= j) {
                        Trip temp = new Trip(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else {
            if (start < end) {
                int i = start, j = end;
                long chot;
                chot = source.get((start + end) / 2).getCreatedAt() != null ? source.get((start + end) / 2).getCreatedAt().getTime() : -1; // chon phan tu o giua lam chot
                while (i < j) {
                    while ((source.get(i).getCreatedAt() != null ? source.get(i).getCreatedAt().getTime() : -1) > chot) i++;
                    while ((source.get(j).getCreatedAt() != null ? source.get(j).getCreatedAt().getTime() : -1) < chot) j--;
                    if (i <= j) {
                        Trip temp = new Trip(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        }
    }

    private ArrayList<Trip> linearSearch(ArrayList<Trip> source, String str){
        ArrayList<Trip> result = new ArrayList<>();
        for (int i=0;i<source.size();i++){
            if (VNCharacterUtils.removeAccent(source.get(i).getName().toLowerCase()).contains(VNCharacterUtils.removeAccent(str.toLowerCase())))
                result.add(source.get(i));
        }
        return result;
    }
}