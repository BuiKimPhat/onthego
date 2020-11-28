package com.leobkdn.onthego.ui.modify_user.list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.modify_user.user.ModifyUserActivity;

import java.util.List;

public class User_adapter extends BaseAdapter {
    private List<Users_class> users;
    private LayoutInflater layoutInflater;
    private Context context;
    public User_adapter(List<Users_class> users, Context context){
        this.users = users;
        this.context=context;
    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv_name,tv_email,tv_stt;
        View view;
        if(convertView == null){
            view = View.inflate(parent.getContext(), R.layout.user_list_item,null);
        } else view = convertView;
        //bind date
        Users_class user = (Users_class)getItem(position);
        ((TextView) view.findViewById(R.id.tv_stt)).setText(String.format(" %d ", user.getStt()));
       ((TextView) view.findViewById(R.id.tv_name)).setText(String.format("Tên : %s", user.getName()));
        ((TextView) view.findViewById(R.id.tv_email)).setText(String.format("Email: %s", user.getEmail()));
        return view;
    }
}
