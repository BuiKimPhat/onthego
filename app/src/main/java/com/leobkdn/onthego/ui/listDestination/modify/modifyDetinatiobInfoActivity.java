package com.leobkdn.onthego.ui.listDestination.modify;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.leobkdn.onthego.R;

import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.source.DestinationDataSource;
import com.leobkdn.onthego.ui.listDestination.List.destinationActivity;


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
    private Spinner opt;
    private Button confirm;
    private Button delete;
    private LoggedInUser user;
    private Destination destination;
    private int positon;
    private String cat;
    private boolean turn = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_destination_info);
        DestinationDataSource a = new DestinationDataSource();

        user = new LoggedInUser(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"),true,new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
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
        delete = findViewById(R.id.delete_button);
        confirm = findViewById(R.id.confirm_button1);
        opt = findViewById(R.id.destination_opt);

        //setInfo
        name.setText(destination.getName());
        address.setText(destination.getAddress());
        descrip.setText(destination.getDescription());
        kinhDo.setText(String.format("%s",destination.getLat()));
        viDo.setText(String.format("%s",destination.getLon()));
        rating.setText(String.format("Đánh giá điểm đến : %s",destination.getRating()));
        ratingNum.setText(String.format("Số người đánh gía : %s",destination.getRateNum()));
        cat = destination.getCategory();

        ArrayAdapter<CharSequence> optAdapter = ArrayAdapter.createFromResource(this, R.array.spi_des, android.R.layout.simple_spinner_item);
        optAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opt.setAdapter(optAdapter);
        opt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(turn){
                if(cat==null){
                }else if(cat.equals("food")) opt.setSelection(2);
                else if(cat.equals("stay")) opt.setSelection(1);
                else opt.setSelection(3);
                turn=false;
                }else {
                    cat=opt.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
