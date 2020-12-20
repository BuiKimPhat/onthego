package com.leobkdn.onthego.ui.listDestination.modify;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.DestinationDataSource;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.ui.listDestination.List.destinationActivity;
import com.leobkdn.onthego.ui.login.LoggedInUserView;

import java.util.Date;

import static android.content.ContentValues.TAG;

public class modifyDetinatiobInfoActivity extends AppCompatActivity {
    private EditText name;
    private EditText address;
    private EditText descrip;
    private EditText kinhDo;
    private EditText viDo;
    private TextView rating;
    private TextView ratingNum;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private Button confirm;
    private Button delete;
    private LoggedInUserView user;
    private Destination destination;
    private int positon;
    private String cat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_destination_info);
        DestinationDataSource a = new DestinationDataSource();

        user = new LoggedInUserView(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"),true,new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
        Intent intent = getIntent();
        positon = intent.getIntExtra("Position",1);
        try{
            destination = a.getDestination(user.getToken(),positon);
        }catch (Exception err){
            Toast.makeText(modifyDetinatiobInfoActivity.this," "+err,Toast.LENGTH_SHORT).show();
        }

        //setView
        name = findViewById(R.id.ed_ten);
        address = findViewById(R.id.ed_dia_chi);
        descrip = findViewById(R.id.ed_mo_ta);
        kinhDo = findViewById(R.id.ed_kinh_do);
        viDo = findViewById(R.id.ed_vi_do);
        rating = findViewById(R.id.rating);
        ratingNum = findViewById(R.id.rating_num);
        bt2 = findViewById(R.id.noi_o);
        bt1 = findViewById(R.id.an_uong);
        bt3 = findViewById(R.id.di_chuyen);
        delete = findViewById(R.id.delete_button);
        confirm = findViewById(R.id.confirm_button1);

        //setInfo
        name.setText(destination.getName());
        address.setText(destination.getAddress());
        descrip.setText(destination.getDescription());
        kinhDo.setText(String.format("%s",destination.getLat()));
        viDo.setText(String.format("%s",destination.getLon()));
        rating.setText(String.format("Đánh giá điểm đến : %s",destination.getRating()));
        ratingNum.setText(String.format("Số người đánh gía : %s",destination.getRateNum()));
        cat = destination.getCategory();
        if(cat!=null){
             if(cat.equals("food")) bt1.setBackgroundColor(0xFFCC99);
             if(cat.equals("stay")) bt2.setBackgroundColor(0xFFCC99);
             if(cat.equals("transport")) bt3.setBackgroundColor(0xFFCC99);
        }else Toast.makeText(this,"Loại điểm dến chưa xác định",Toast.LENGTH_SHORT).show();

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
                destination.setRateNum(Integer.valueOf(ratingNum.getText().toString()));
                destination.setRating(Float.valueOf(rating.getText().toString()));
                destination.setLat(Float.valueOf(viDo.getText().toString()));
                destination.setLon(Float.valueOf(kinhDo.getText().toString()));
                destination.setCategory(cat);
                a.editInfo(user.getToken(),destination);
                restartActivity(modifyDetinatiobInfoActivity.this);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                a.delete(positon);
//                finish();
                Toast.makeText(modifyDetinatiobInfoActivity.this,"Không thể xóa vì ảnh hưởng đến trải nghiệm người dùng",Toast.LENGTH_SHORT).show();
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
