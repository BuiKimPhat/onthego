package com.leobkdn.onthego.ui.food;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.result.Result;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.tools.VNCharacterUtils;
import com.leobkdn.onthego.ui.destination.DestinationListAdapter;
import com.leobkdn.onthego.data.result.DestinationResult;

import java.util.ArrayList;

public class FoodActivity extends AppCompatActivity {
    private DestinationResult destinationResult = new DestinationResult();
    private ArrayList<Destination> destinations = new ArrayList<>();
    private ArrayList<Destination> display = new ArrayList<>();
    private ListView destinationList;
    private EditText search;
    private Spinner sortList;
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
        progressBar = findViewById(R.id.destinationsLoading);
        destinationList = findViewById(R.id.destinations_listView);
        search = findViewById(R.id.destination_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
                display = linearSearch(destinations, search.getText().toString());
                quickSortBy(display, 0, display.size() - 1, sortList.getSelectedItem().toString());
                progressBar.setVisibility(View.GONE);
                destinationList.setAdapter(new DestinationListAdapter(context, getIntent(), display));
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        sortList = findViewById(R.id.destination_sort);
        // Create an ArrayAdapter using the cities string array and a default spinner layout
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this, R.array.sortDestinations, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sortList.setAdapter(sortAdapter);
        sortList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                quickSortBy(display, 0, display.size() - 1, selectedItem);
                destinationList.setAdapter(new DestinationListAdapter(context, getIntent(), display));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        destinationResult.getDestinationResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                progressBar.setVisibility(View.GONE);
                if (result instanceof Result.Success) {
                    if (((Result.Success) result).checkTypeString()) {
                        Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                    } else {
                        destinations = ((Result.Success<ArrayList<Destination>>) result).getData();
                        display = new ArrayList<>(destinations);
                        if (destinations != null) {
                            destinationList.setAdapter(new DestinationListAdapter(context, getIntent(), display));
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
                destinationResult.fetchDestinations(restorePrefsData("token"), "food");
            }
        }).start();
    }

    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    private void quickSortBy(ArrayList<Destination> source, int start, int end, String by) {
        if (by.equals("Theo số người đánh giá: Cao -> thấp")) {
            if (start < end) {
                int i = start, j = end;
                int chot = source.get((start + end) / 2).getRateNum(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getRateNum() > chot) i++;
                    while (source.get(j).getRateNum() < chot) j--;
                    if (i <= j) {
                        Destination temp = new Destination(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo số người đánh giá: Thấp -> cao")) {
            if (start < end) {
                int i = start, j = end;
                int chot = source.get((start + end) / 2).getRateNum(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getRateNum() < chot) i++;
                    while (source.get(j).getRateNum() > chot) j--;
                    if (i <= j) {
                        Destination temp = new Destination(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo đánh giá: Thấp -> cao")) {
            if (start < end) {
                int i = start, j = end;
                float chot;
                chot = source.get((start + end) / 2).getRating(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getRating() < chot) i++;
                    while (source.get(j).getRating() > chot) j--;
                    if (i <= j) {
                        Destination temp = new Destination(source.get(i));
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
                        Destination temp = new Destination(source.get(i));
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
                        Destination temp = new Destination(source.get(i));
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
                float chot;
                chot = source.get((start + end) / 2).getRating(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getRating() > chot) i++;
                    while (source.get(j).getRating() < chot) j--;
                    if (i <= j) {
                        Destination temp = new Destination(source.get(i));
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

    private ArrayList<Destination> linearSearch(ArrayList<Destination> source, String str){
        ArrayList<Destination> result = new ArrayList<>();
        for (int i=0;i<source.size();i++){
            if (VNCharacterUtils.removeAccent(source.get(i).getName().toLowerCase()).contains(VNCharacterUtils.removeAccent(str.toLowerCase())))
                result.add(source.get(i));
        }
        return result;
    }
}