package com.leobkdn.onthego.ui.listDestination.modify;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.source.DestinationDataSource;

import java.util.Date;

import static android.content.ContentValues.TAG;

public class addDestinationActivity extends AppCompatActivity {
    private EditText name;
    private EditText address;
    private EditText descrip;
    private EditText kinhDo;
    private EditText viDo;
    private EditText rating;
    private EditText ratingNum;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private Button confirm;
    private LoggedInUser user;
    private Destination destination;
    private String cat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_destination);
        DestinationDataSource a = new DestinationDataSource();

        user = new LoggedInUser(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"),true,new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
        Intent intent = getIntent();

        //setView
        name = findViewById(R.id.ed_ten);
        address = findViewById(R.id.ed_dia_chi);
        descrip = findViewById(R.id.ed_mo_ta);
        kinhDo = findViewById(R.id.ed_kinh_do);
        viDo = findViewById(R.id.ed_vi_do);
        rating = findViewById(R.id.ed_rating);
        ratingNum = findViewById(R.id.ed_rating_num);
        bt2 = findViewById(R.id.noi_o);
        bt1 = findViewById(R.id.an_uong);
        bt3 = findViewById(R.id.di_chuyen);
        confirm = findViewById(R.id.add_button1);

        //setInfo
        name.setText("Tên");
        address.setText("Địa chỉ");
        descrip.setText("Mô tả");
        kinhDo.setText("0");
        viDo.setText("0");
        rating.setText("Điểm đánh giá");
        rating.setText("Số người đánh giá");
        cat = "food";

        //set OnClickListener
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt1.setBackgroundColor(0xFFCC99);
                bt2.setBackgroundColor(0x000000);
                bt3.setBackgroundColor(0x000000);
                cat = "food";
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt1.setBackgroundColor(0x000000);
                bt2.setBackgroundColor(0xFFCC99);
                bt3.setBackgroundColor(0x000000);
                cat = "stay";
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt1.setBackgroundColor(0x000000);
                bt2.setBackgroundColor(0x000000);
                bt3.setBackgroundColor(0xFFCC99);
                cat = "transport";
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(false);
                destination.setAddress(address.getText().toString());
                destination.setName(name.getText().toString());
                destination.setDescription(descrip.getText().toString());
                destination.setLat(Float.valueOf(viDo.getText().toString()));
                destination.setLon(Float.valueOf(kinhDo.getText().toString()));
                destination.setCategory(cat);
                a.addDes(user.getToken(),destination);
                restartActivity(addDestinationActivity.this);
            }
        });
    }

    public static void restartActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }
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
