package com.leobkdn.onthego.ui.AdminHome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.login.LoggedInUserView;
import com.leobkdn.onthego.ui.login.LoginActivity;
import com.leobkdn.onthego.ui.login.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;
import com.leobkdn.onthego.ui.profile.ProfileActivity;

import java.util.Date;

public class AdminHomeActivity extends AppCompatActivity {
    private LoggedInUserView user;
    private LoginViewModel loginViewModel;
    private boolean pressedOnce = false;
    private ImageButton destination;
    private ImageButton trip;
    private ImageButton persons;
    private ImageButton userAvatar;
    @Override
    public void onBackPressed() {
        if (pressedOnce) {
            super.onBackPressed();
            return;
        }
        this.pressedOnce = true;
        Toast.makeText(this, "Nhấn một lần nữa để thoát", Toast.LENGTH_SHORT).show();
        // reset pressedOnce to false after 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pressedOnce = false;
            }
        }, 2000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        user = new LoggedInUserView(restorePrefsData("username"), restorePrefsData("email"), restorePrefsData("token"), false, new Date(restorePrefsLong("birthday")), restorePrefsData("address"));

        destination = findViewById(R.id.destination_button);
        trip = findViewById(R.id.trip_button);
        persons = findViewById(R.id.person_button);
        ImageButton powerButton = findViewById(R.id.powerButton);
        ImageButton settingButton = findViewById(R.id.settingButton);
        ProgressBar loading = findViewById(R.id.homeLoading);
        userAvatar = findViewById(R.id.userAvatar);
        LinearLayout profileSwitch = findViewById(R.id.linearLayout);
        //set avatar text
        TextView username = findViewById(R.id.home_username);
        username.setText(user.getDisplayName());
        // Log Out Result listener
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    showLogoutFailed(loginResult.getError());
                    updateUi();
                    Toast.makeText(getApplicationContext(), loginResult.getError(), Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                if (loginResult.getSuccessString() != null) {
                    updateUi();
                    Toast.makeText(getApplicationContext(), loginResult.getSuccessString(), Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_OK);
                    //Complete and destroy login activity once successful
                    finish();
                }
            }
        });

        // profile switch listener
        profileSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });



    }

        private void setupActivityButtons(ImageButton button, Intent intent){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });
        }
        private void updateUi() {
            // initiate successful logout in experience
            clearPrefs("userPrefs");
            clearPrefs("currentTrip");
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        private void showLogoutFailed(String errorString) {
            Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
        }

        private void clearPrefs(String prefsName){
            SharedPreferences prefs = getApplicationContext().getSharedPreferences(prefsName, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
        }
    //save Prefs data
    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
    private long restorePrefsLong(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getLong(key, 0);
    }

}
