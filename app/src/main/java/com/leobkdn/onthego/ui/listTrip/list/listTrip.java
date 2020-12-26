package com.leobkdn.onthego.ui.listTrip.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leobkdn.onthego.R;

import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.model.Trip;
import com.leobkdn.onthego.data.source.TripDataSource;
import com.leobkdn.onthego.tools.VNCharacterUtils;
import com.leobkdn.onthego.ui.destination.DestinationListAdapter;
import com.leobkdn.onthego.ui.listDestination.List.destiantionAdapter;
import com.leobkdn.onthego.ui.listTrip.modify.modifyTrip;
import com.leobkdn.onthego.ui.listTrip.modify.addTrip;
import com.leobkdn.onthego.ui.modify_user.list.Users_class;

import java.util.ArrayList;
import java.util.Date;

public class listTrip  extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Trip> trips;
    private ArrayList<Trip> display = new ArrayList<Trip>();
    private Spinner sortList;
    private EditText search;
    private LoggedInUser user;
    private int Position =0;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trip);
        TripDataSource a = new TripDataSource();

        user = new LoggedInUser(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"),true,new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
        //des = new Result<ArrayList<Destination>()>;

        search = findViewById(R.id.trip_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                display = linearSearch(trips, search.getText().toString());
                quickSortBy(display, 0, display.size() - 1, sortList.getSelectedItem().toString());
                listView.setAdapter(new listTripAdapter(display,listTrip.this));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sortList = findViewById(R.id.trip_sort);
        // Create an ArrayAdapter using the cities string array and a default spinner layout
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this, R.array.sortTrips, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sortList.setAdapter(sortAdapter);
        sortList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                for(int i=0;i<display.size();i++)
                quickSortBy(display, 0, display.size()-1, selectedItem);
                listView.setAdapter(new listTripAdapter(display,listTrip.this));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try{
            trips = a.getListTrip(user.getToken());
            display = trips;
        }catch (Exception err){
            Toast.makeText(listTrip.this," "+err,Toast.LENGTH_SHORT).show();
        }
        listView = findViewById(R.id.trip_list_view);
        fab = findViewById(R.id.addNewTripButton);
        listTripAdapter adapter = new listTripAdapter(display,this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Position = display.get(position).getId();
                Intent intent= new Intent(listTrip.this , modifyTrip.class);
                intent.putExtra("Position",Position);
                intent.putExtra("tripName", display.get(position).getName());
                intent.putExtra("tripId", display.get(position).getId());
                intent.putExtra("isNew", false);
                intent.putExtra("tripOwnerId", display.get(position).getOwnerId());
                startActivity(intent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(listTrip.this , modifyTrip.class);
                intent.putExtra("tripName", "an");
                intent.putExtra("tripId", 1);
                intent.putExtra("isNew", true);
                intent.putExtra("tripOwnerId", 1);
                startActivity(intent);
            }
        });
    }

    // get prefs from storage
    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
    private long restorePrefsLong(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getLong(key, 0);
    }
    private String restoreCurrentTripData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("currentTrip", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
    private void quickSortBy(ArrayList<Trip> source, int start, int end, String by) {
        if (by.equals("Theo ngày thêm: Xa -> gần")) {
            if (start < end) {
                int i = start, j = end;
                long chot;
                chot = source.get((start + end) / 2).getOwnerId(); // chon phan tu o giua lam chot
                while (i < j) {
                    while ((source.get(i).getOwnerId()) < chot) i++;
                    while ((source.get(j).getOwnerId()) > chot) j--;
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
                float chot;
                chot = source.get((start + end) / 2).getOwnerId(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getOwnerId() > chot) i++;
                    while (source.get(j).getOwnerId() < chot) j--;
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
