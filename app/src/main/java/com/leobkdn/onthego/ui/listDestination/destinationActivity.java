package com.leobkdn.onthego.ui.listDestination;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.DestinationDataSource;
import com.leobkdn.onthego.data.Result;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.ui.login.LoggedInUserView;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;
import com.leobkdn.onthego.ui.modify_user.list.UserListActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class destinationActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Destination> des;
    private LoggedInUserView user;
    private int Position =0;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_destination);
        DestinationDataSource a = new DestinationDataSource();

        user = new LoggedInUserView(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"),true,new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
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
