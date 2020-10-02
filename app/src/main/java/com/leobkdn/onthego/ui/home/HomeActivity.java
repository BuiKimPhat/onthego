package com.leobkdn.onthego.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.login.LoggedInUserView;

public class HomeActivity extends AppCompatActivity {

    private LoggedInUserView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = new LoggedInUserView(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"));

        //set avatar text
        TextView username = findViewById(R.id.home_username);
        username.setText(user.getDisplayName());
    }

    // get prefs from storage
    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
}