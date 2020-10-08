package com.leobkdn.onthego.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.login.LoggedInUserView;

import java.util.ArrayList;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    private LoggedInUserView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new LoggedInUserView(getIntent().getStringExtra("name"), getIntent().getStringExtra("email"), null, false, new Date(getIntent().getLongExtra("birthday", 1000000)), getIntent().getStringExtra("address"));
        setContentView(R.layout.activity_profile);
    }
}