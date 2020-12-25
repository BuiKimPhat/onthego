package com.leobkdn.onthego.ui.modify_user.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leobkdn.onthego.R;

import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.source.ListUserDataSource;

import com.leobkdn.onthego.tools.VNCharacterUtils;
import com.leobkdn.onthego.ui.destination.DestinationListAdapter;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;
import com.leobkdn.onthego.ui.modify_user.user.AddUserActivity;
import com.leobkdn.onthego.ui.modify_user.user.ModifyUserActivity;

import com.leobkdn.onthego.ui.profile.ProfileActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private ListView listView;
    private User_adapter adapter;
    private ArrayList<Users_class> Users;
    private ArrayList<Users_class> display = new ArrayList<>();
    private LoggedInUser user;
    private LoginViewModel loginViewModel;
    private int Position =0;
    private Spinner sortList;
    private EditText search;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        user = new LoggedInUser(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"),true,new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
        Users = new ArrayList<Users_class>();
        search = findViewById(R.id.user_search);
        listView = findViewById(R.id.user_list_view);
        fab = findViewById(R.id.addNewUserButton);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                display = linearSearch(Users, search.getText().toString());
                listView.setAdapter(new User_adapter(display,UserListActivity.this));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sortList = findViewById(R.id.user_sort);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this, R.array.sortUser, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortList.setAdapter(sortAdapter);
        sortList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                quickSortBy(display, 0, display.size() - 1, selectedItem);
                listView.setAdapter(new User_adapter(display,UserListActivity.this));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Lấy thông tin user
        try {
        ListUserDataSource a = new ListUserDataSource();
        Users = a.getListUsers(user.getToken());
        quickSortBy(Users,0,Users.size()-1," ");
        display = Users;
        }catch (Exception e){
            Toast.makeText(UserListActivity.this," "+e,Toast.LENGTH_LONG).show();
        }
        // Ném thông tin vào list view
        User_adapter adapters= new User_adapter(Users,this);
        listView.setAdapter(adapters);
        // xử lí khi bấm vào 1 item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > parent, View view,
            int position, long id){
                Position = position;
                Position = display.get(Position).getStt();
                Intent intent= new Intent(UserListActivity.this , ModifyUserActivity.class);
                intent.putExtra("Position",Position);
                startActivity(intent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(UserListActivity.this,"add",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserListActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });
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

    private void quickSortBy(ArrayList<Users_class> source, int start, int end, String by) {
        if (by.equals("Theo ID: Cao -> thấp")) {
            if (start < end) {
                int i = start, j = end;
                int chot = source.get((start + end) / 2).getStt(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getStt() > chot) i++;
                    while (source.get(j).getStt() < chot) j--;
                    if (i <= j) {
                        Users_class temp = new Users_class(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo ID: Thấp -> cao")) {
            if (start < end) {
                int i = start, j = end;
                int chot = source.get((start + end) / 2).getStt(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getStt() < chot) i++;
                    while (source.get(j).getStt() > chot) j--;
                    if (i <= j) {
                        Users_class temp = new Users_class(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo tên: A -> Z")) {
            if (start < end) {
                int i = start, j = end;
                String chot;
                chot = source.get((start + end) / 2).getName().toLowerCase(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getName().toLowerCase().compareTo(chot) < 0) i++;
                    while (source.get(j).getName().toLowerCase().compareTo(chot) > 0) j--;
                    if (i <= j) {
                        Users_class temp = new Users_class(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo tên: Z -> A")) {
            if (start < end) {
                int i = start, j = end;
                String chot;
                chot = source.get((start + end) / 2).getName().toLowerCase(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getName().toLowerCase().compareTo(chot) > 0) i++;
                    while (source.get(j).getName().toLowerCase().compareTo(chot) < 0) j--;
                    if (i <= j) {
                        Users_class temp = new Users_class(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else {
            if (start < end) {
                int i = start, j = end;
                float chot;
                chot = source.get((start + end) / 2).getStt(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getStt() > chot) i++;
                    while (source.get(j).getStt() < chot) j--;
                    if (i <= j) {
                        Users_class temp = new Users_class(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        }
    }

    private ArrayList<Users_class> linearSearch(ArrayList<Users_class> source, String str) {
        ArrayList<Users_class> result = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            if (VNCharacterUtils.removeAccent(source.get(i).getName().toLowerCase()).contains(VNCharacterUtils.removeAccent(str.toLowerCase())))
                result.add(source.get(i));
        }
        return result;
    }
}


