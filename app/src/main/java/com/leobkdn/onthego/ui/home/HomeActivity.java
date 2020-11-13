package com.leobkdn.onthego.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leobkdn.onthego.ui.destination.DestinationActivity;
import com.leobkdn.onthego.ui.food.FoodActivity;
import com.leobkdn.onthego.ui.go.GoActivity;
import com.leobkdn.onthego.ui.profile.ProfileActivity;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.login.LoggedInUserView;
import com.leobkdn.onthego.ui.login.LoginActivity;
import com.leobkdn.onthego.ui.login.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;
import com.leobkdn.onthego.ui.stay.StayActivity;
import com.leobkdn.onthego.ui.transport.TransportActivity;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private LoggedInUserView user;
    private LoginViewModel loginViewModel;
    private boolean pressedOnce = false;
    private ImageButton destination;
    private ImageButton transport;
    private ImageButton stay;
    private ImageButton food;
    private ImageButton goButton;
    private ImageButton userAvatar;
    private TextView currentTrip;

    // double-tap to exit activity
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
        setContentView(R.layout.activity_home);
        // install http cache
        try {
            File httpCacheDir = new File(getCacheDir(), "http");
            long httpCacheSize = 1024 * 1024; // 1 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("HTTP cache", "HTTP response cache installation failed:" + e);
        }

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        user = new LoggedInUserView(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"),false,new Date(restorePrefsLong("birthday")), restorePrefsData("address"));

        goButton = findViewById(R.id.home_button_0);
        destination = findViewById(R.id.home_button_1);
        transport = findViewById(R.id.home_button_2);
        stay = findViewById(R.id.home_button_3);
        food = findViewById(R.id.home_button_4);
        ImageButton powerButton = findViewById(R.id.powerButton);
        ImageButton settingButton = findViewById(R.id.settingButton);
        ProgressBar loading = findViewById(R.id.homeLoading);
        userAvatar = findViewById(R.id.userAvatar);
        LinearLayout profileSwitch = findViewById(R.id.linearLayout);
        //set avatar text
        TextView username = findViewById(R.id.home_username);
        username.setText(user.getDisplayName());
        // current trip
        currentTrip = findViewById(R.id.home_currentTrip);
        currentTrip.setText(restoreCurrentTripData("name") != null ? restoreCurrentTripData("name") : "Chưa chọn chuyến đi");

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
        setupActivityButtons(userAvatar, new Intent(getApplicationContext(), ProfileActivity.class));
        setupActivityButtons(settingButton, new Intent(getApplicationContext(), ProfileActivity.class));
        setupActivityButtons(goButton, new Intent(getApplicationContext(), GoActivity.class));
        setupActivityButtons(destination, new Intent(getApplicationContext(), DestinationActivity.class));
        setupActivityButtons(transport, new Intent(getApplicationContext(), TransportActivity.class));
        setupActivityButtons(stay, new Intent(getApplicationContext(), StayActivity.class));
        setupActivityButtons(food, new Intent(getApplicationContext(), FoodActivity.class));

        // log out button listener
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("Thoát");
                alertDialog.setMessage("Bạn muốn đăng xuất hay thoát chương trình?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Đăng xuất",
                        new DialogInterface.OnClickListener() {

                            //Log out
                            public void onClick(DialogInterface dialog, int which) {
                                loading.setVisibility(View.VISIBLE);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginViewModel.logOut(user.getToken());
                                    }
                                }).start();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Thoát",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
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