package com.leobkdn.onthego.ui.home;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leobkdn.onthego.MainActivity;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.login.LoggedInUserView;
import com.leobkdn.onthego.ui.login.LoginActivity;
import com.leobkdn.onthego.ui.login.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;

public class HomeActivity extends AppCompatActivity {

    private LoggedInUserView user;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        user = new LoggedInUserView(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"));

        ImageButton powerButton = findViewById(R.id.powerButton);
        ProgressBar loading = findViewById(R.id.homeLoading);
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
    private void updateUi() {
        // initiate successful logout in experience
        clearPrefs("userPrefs");
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
}