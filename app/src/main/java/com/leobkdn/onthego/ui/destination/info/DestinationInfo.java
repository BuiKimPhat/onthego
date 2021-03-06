package com.leobkdn.onthego.ui.destination.info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.leobkdn.onthego.R;

public class DestinationInfo extends AppCompatActivity {
    private TextView name;
    private TextView address;
    private TextView phone;
    private TextView description;
    private TextView rateNum;
    private TextView coordinate;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_info);
        name = findViewById(R.id.destination_info_name);
        address = findViewById(R.id.destination_info_address);
        phone = findViewById(R.id.destination_info_phone);
        rateNum = findViewById(R.id.destination_info_rateNum);
        coordinate = findViewById(R.id.destination_info_coordinate);
        description = findViewById(R.id.destination_info_description);
        name.setText(getIntent().getStringExtra("name"));
        address.setText(getIntent().getStringExtra("address"));
        phone.setText("Đánh giá: " + getIntent().getFloatExtra("rating", 0) + " / 5.0");
        rateNum.setText("Số người đánh giá: " + getIntent().getIntExtra("rateNum", 0));
        coordinate.setText("Vĩ độ: " + getIntent().getFloatExtra("lat", 0) + "  Kinh độ: " + getIntent().getFloatExtra("lon", 0));
        description.setText(getIntent().getStringExtra("description"));
    }
}