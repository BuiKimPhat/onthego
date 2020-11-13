package com.leobkdn.onthego.ui.modify_user.list;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.leobkdn.onthego.R;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    ListView listView;
    User_adapter adapter;
    List<Users_class> Users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        Users = new ArrayList<Users_class>();
        // tu phat sinh
        for(int i=0;i<=50;i++){
            Users.add(new Users_class("nguoi"+i,"email"+i,i));
        }
        listView = (ListView) findViewById(R.id.user_list_view);
        User_adapter adapters= new User_adapter(Users,this);
        listView.setAdapter(adapters);
    }
}


