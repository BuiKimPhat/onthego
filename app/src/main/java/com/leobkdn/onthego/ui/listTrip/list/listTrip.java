package com.leobkdn.onthego.ui.listTrip.list;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leobkdn.onthego.R;

import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.model.Trip;
import com.leobkdn.onthego.data.source.TripDataSource;
import com.leobkdn.onthego.ui.listDestination.List.destiantionAdapter;

import java.util.ArrayList;
import java.util.Date;

public class listTrip  extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Trip> trips;
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
        try{
            trips = a.getListTrip(user.getToken());
        }catch (Exception err){
            Toast.makeText(listTrip.this," "+err,Toast.LENGTH_SHORT).show();
        }
        listView = findViewById(R.id.trip_list_view);
        fab = findViewById(R.id.addNewTripButton);
        listTripAdapter adapter = new listTripAdapter(trips,this);
        listView.setAdapter(adapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Position = trips.get(position).getId();
//                Intent intent= new Intent(listTrip.this , modifyTripInfoActivity.class);
//                intent.putExtra("Position",Position);
//                startActivity(intent);
//            }
//        });
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(listTrip.this , addTripActivity.class);
//                startActivity(intent);
//            }
//        });

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
}
