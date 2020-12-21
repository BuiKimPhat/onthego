package com.leobkdn.onthego.ui.listDestination.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.source.DestinationDataSource;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.ui.modify_user.list.UserListActivity;
import com.leobkdn.onthego.ui.listDestination.modify.modifyDetinatiobInfoActivity;
import com.leobkdn.onthego.ui.modify_user.user.ModifyUserActivity;
import com.leobkdn.onthego.ui.listDestination.modify.addDestinationActivity;

import java.util.ArrayList;
import java.util.Date;

public class destinationActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Destination> des;
    private LoggedInUser user;
    private int Position =0;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_destination);
        DestinationDataSource a = new DestinationDataSource();

        user = new LoggedInUser(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"),true,new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
        //des = new Result<ArrayList<Destination>()>;
        try{
            des = a.fetchDestinations2(user.getToken());
        }catch (Exception err){
            Toast.makeText(destinationActivity.this," "+err,Toast.LENGTH_SHORT).show();
        }
        listView = findViewById(R.id.destination_list_view);
        fab = findViewById(R.id.addNewDestinationButton);
        destiantionAdapter adapter = new destiantionAdapter(des,this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Position = des.get(position).getId();
                Intent intent= new Intent(destinationActivity.this , modifyDetinatiobInfoActivity.class);
                intent.putExtra("Position",Position);
                startActivity(intent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(destinationActivity.this , addDestinationActivity.class);
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
}
