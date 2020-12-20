package com.leobkdn.onthego.ui.listDestination.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.Destination;

import java.util.List;

public class destiantionAdapter extends BaseAdapter {
    private List<Destination> des;
    private LayoutInflater layoutInflater;
    private Context context;
    public destiantionAdapter(List<Destination> dess, Context context){
        this.des = dess;
        this.context=context;
    }


    @Override
    public int getCount() {
        return des.size();
    }

    @Override
    public Object getItem(int position) {
        return des.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button detailButton ;
        View view;
        if(convertView == null){
            view = View.inflate(parent.getContext(), R.layout.destination_info_list_item,null);
        } else view = convertView;
        //bind date
        Destination dess = (Destination) getItem(position);
        ((TextView) view.findViewById(R.id.tv_id)).setText(String.format(" %d ", dess.getId()));
        ((TextView) view.findViewById(R.id.tv_name2)).setText(String.format("Tên : %s", dess.getName()));
        ((TextView) view.findViewById(R.id.tv_address)).setText(String.format("Địa chỉ: %s", dess.getAddress()));
        if( dess.getCategory()==null) ((TextView) view.findViewById(R.id.tv_cat)).setText(String.format("Chưa rõ!"));
        else{
            if(dess.getCategory().equals("food")) ((TextView) view.findViewById(R.id.tv_cat)).setText(String.format("Quán \n ăn"));
            if(dess.getCategory().equals("stay")) ((TextView) view.findViewById(R.id.tv_cat)).setText(String.format("Nơi \n ở"));
            if(dess.getCategory().equals("transport")) ((TextView) view.findViewById(R.id.tv_cat)).setText(String.format("Di \nchuyển"));
            //((TextView) view.findViewById(R.id.tv_cat)).setText(String.format("%s",dess.getCategory()));
        }
        return view;
    }
}
