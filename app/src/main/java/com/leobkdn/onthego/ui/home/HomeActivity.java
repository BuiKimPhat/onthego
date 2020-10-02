package com.leobkdn.onthego.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leobkdn.onthego.MainActivity;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.login.LoggedInUserView;

public class HomeActivity extends AppCompatActivity {

    private LoggedInUserView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = new LoggedInUserView(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"));

        ImageButton powerButton = findViewById(R.id.powerButton);

        //set avatar text
        TextView username = findViewById(R.id.home_username);
        username.setText(user.getDisplayName());

        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("Thoát");
                alertDialog.setMessage("Bạn muốn đăng xuất hay thoát chương trình?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Đăng xuất",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
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

    // get prefs from storage
    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
}