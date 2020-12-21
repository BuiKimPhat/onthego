package com.leobkdn.onthego.ui.AdminHome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
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

import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.source.ListUserDataSource;
import com.leobkdn.onthego.ui.listDestination.List.destinationActivity;
import com.leobkdn.onthego.ui.listTrip.list.listTrip;
import com.leobkdn.onthego.ui.login.LoginActivity;
import com.leobkdn.onthego.data.result.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;
import com.leobkdn.onthego.ui.modify_user.list.UserListActivity;
import com.leobkdn.onthego.ui.profile.ProfileActivity;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class AdminHomeActivity extends AppCompatActivity {
    private LoggedInUser user;

    private LoginViewModel loginViewModel;
    private boolean pressedOnce = false;
    private TextView tv1,tv2,tv3;

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
        //fix exception
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            File httpCacheDir = new File(getCacheDir(), "http");
            long httpCacheSize = 1024 * 1024; // 1 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("HTTP cache", "HTTP response cache installation failed:" + e);
        }

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        user = new LoggedInUser(restorePrefsData("username"), restorePrefsData("email"), restorePrefsData("token"), false, new Date(restorePrefsLong("birthday")), restorePrefsData("address"));

        ImageButton destination = findViewById(R.id.destination_button);
        ImageButton trip = findViewById(R.id.trip_button);
        ImageButton persons = findViewById(R.id.person_button);
        tv1 = findViewById(R.id.infor_users);
        tv2 = findViewById(R.id.info_trip);
        tv3 = findViewById(R.id.infor_destination);
        ImageButton powerButton = findViewById(R.id.powerButton);
        ImageButton settingButton = findViewById(R.id.settingButton);
        ProgressBar loading = findViewById(R.id.homeLoading);
        ImageButton userAvatar = findViewById(R.id.userAvatar);
        LinearLayout profileSwitch = findViewById(R.id.linearLayout);
        //set avatar text
        TextView username = findViewById(R.id.home_username);
        username.setText(user.getDisplayName());

        //setButton
        setupActivityButtons(persons,new Intent(AdminHomeActivity.this, UserListActivity.class));
        setupActivityButtons(destination,new Intent(AdminHomeActivity.this, destinationActivity.class));
        setupActivityButtons(trip,new Intent(AdminHomeActivity.this, listTrip.class));
//        setupActivityButtons(persons,new Intent(getApplicationContext(),UserListActivity.class));
//        setupActivityButtons(persons,new Intent(getApplicationContext(),UserListActivity.class));
//        persons.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(AdminHomeActivity.this,"aaaaa",Toast.LENGTH_LONG).show();
//            }
//        });

        //set db info
        try { ListUserDataSource a = new ListUserDataSource();

            a.getListUsers(user.getToken());
            tv1.setText(String.format("Tổng số tài khoản : "+ a.getSum(user.getToken())));
       }catch (Exception e){
            Toast.makeText(AdminHomeActivity.this," "+ e,Toast.LENGTH_LONG).show();
        }

        try{
            ListUserDataSource a = new ListUserDataSource();
            tv2.setText(String.format("Tổng số chuyến đi : " + a.getTripCount(user.getToken())));
        }catch (Exception e){
            Toast.makeText(AdminHomeActivity.this," "+ e,Toast.LENGTH_LONG).show();
        }
        try{
            ListUserDataSource a = new ListUserDataSource();
            tv3.setText(String.format("Tổng số điểm đến : " + a.getDestinationCount(user.getToken())));
        }catch (Exception e){
            Toast.makeText(AdminHomeActivity.this," "+ e,Toast.LENGTH_LONG).show();
        }
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

        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(AdminHomeActivity.this).create();
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
