package com.leobkdn.onthego.ui.modify_user.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.ListUserDataSource;
import com.leobkdn.onthego.data.LoginDataSource;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.ui.login.LoggedInUserView;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;
import com.leobkdn.onthego.ui.modify_user.user.AddUserActivity;
import com.leobkdn.onthego.ui.modify_user.user.ModifyUserActivity;
import com.leobkdn.onthego.ui.profile.ProfileActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private ListView listView;
    private User_adapter adapter;
    private List<Users_class> Users;
    private LoggedInUserView user;
    private LoginViewModel loginViewModel;
    private int Position =0;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);
        //fix exception
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        //install http cache
//        try {
//            File httpCacheDir = new File(getCacheDir(), "http");
//            long httpCacheSize = 1024 * 1024; // 1 MiB
//            HttpResponseCache.install(httpCacheDir, httpCacheSize);
//        } catch (IOException e) {
//            Log.i("HTTP cache", "HTTP response cache installation failed:" + e);
//        }

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        user = new LoggedInUserView(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"),true,new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
        Users = new ArrayList<Users_class>();
        //Lấy thông tin user
        try {
        ListUserDataSource a = new ListUserDataSource();
        Users = a.getListUsers(user.getToken());
        }catch (Exception e){
            Toast.makeText(UserListActivity.this," "+e,Toast.LENGTH_LONG).show();
        }
        // Ném thông tin vào list view
        listView = findViewById(R.id.user_list_view);
        fab = findViewById(R.id.addNewUserButton);
        User_adapter adapters= new User_adapter(Users,this);
        listView.setAdapter(adapters);
        // xử lí khi bấm vào 1 item
        ListUserDataSource us = new ListUserDataSource();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > parent, View view,
            int position, long id){
                Position = position;
                Position = Users.get(Position).getStt();
                Intent intent= new Intent(UserListActivity.this , ModifyUserActivity.class);
                intent.putExtra("Position",Position);
                startActivity(intent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(UserListActivity.this,"add",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserListActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });
    }
    private void clearPrefs(String prefsName){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(prefsName, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
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


