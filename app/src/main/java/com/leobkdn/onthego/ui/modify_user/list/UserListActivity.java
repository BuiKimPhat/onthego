package com.leobkdn.onthego.ui.modify_user.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.ListUserDataSource;
import com.leobkdn.onthego.data.LoginDataSource;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    ListView listView;
    User_adapter adapter;
    List<Users_class> Users;
    ListUserDataSource us;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        Users = new ArrayList<Users_class>();
        //Lấy thông tin user
        try {
        ListUserDataSource a = new ListUserDataSource();
        Users = a.getListUsers();
        }catch (Exception e){}
        // Ném thông tin vào list view
        listView = (ListView) findViewById(R.id.user_list_view);
        User_adapter adapters= new User_adapter(Users,this);
        listView.setAdapter(adapters);
        // xử lí khi bấm vào 1 item
        us = new ListUserDataSource();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > parent, View view,
            int position, long id){
                LoggedInUser ex = us.getInfoUser(Users.get(position).getStt());
                Intent intent= new Intent(getApplicationContext() , ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}


