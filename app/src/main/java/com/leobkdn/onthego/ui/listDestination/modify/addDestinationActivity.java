package com.leobkdn.onthego.ui.listDestination.modify;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.source.DestinationDataSource;
import com.leobkdn.onthego.ui.listDestination.List.destiantionAdapter;
import com.leobkdn.onthego.ui.listDestination.List.destinationActivity;

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
    private Spinner opt;
    private Button confirm;
    private LoggedInUser user;
    private Destination destination;
    private boolean turn=true;
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
        confirm = findViewById(R.id.add_button1);
        opt = findViewById(R.id.destination_opt);

        //setInfo
        name.setText("Tên");
        address.setText("Địa chỉ");
        descrip.setText("Mô tả");
        kinhDo.setText("0");
        viDo.setText("0");
        rating.setText("0");
        ratingNum.setText("1");
        cat = "food";

        ArrayAdapter<CharSequence> optAdapter = ArrayAdapter.createFromResource(this, R.array.spi_des, android.R.layout.simple_spinner_item);
        optAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opt.setAdapter(optAdapter);
        opt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(turn){
                    opt.setSelection(0);
                    cat=opt.getSelectedItem().toString();
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
                if(address.getText().toString()==null || name.getText().toString() == null ) {
                    Toast.makeText(addDestinationActivity.this,"Tên và địa chỉ không được để trốmg",Toast.LENGTH_SHORT).show();
                    confirm.setEnabled(true);
                }else {
                    destination = new Destination();
                    destination.setAddress(address.getText().toString());
                    destination.setName(name.getText().toString());
                    destination.setDescription(descrip.getText().toString());
                    destination.setLat(Float.valueOf(viDo.getText().toString()));
                    destination.setLon(Float.valueOf(kinhDo.getText().toString()));
                    if(cat==null) destination.setCategory(cat);
                    else if(cat.equals("Chưa rõ")) destination.setCategory(null);
                    else if(cat.equals("Nơi ở")) destination.setCategory("stay");
                    else if (cat.equals("Quán ăn")) destination.setCategory("food");
                    else if(cat.equals("Di chuyển")) destination.setCategory("transport");
                    a.addDes(user.getToken(), destination);
                    Toast.makeText(addDestinationActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                    confirm.setEnabled(true);
                }
            }
        });
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
