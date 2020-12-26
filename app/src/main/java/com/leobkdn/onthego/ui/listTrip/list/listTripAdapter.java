package com.leobkdn.onthego.ui.listTrip.list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.Trip;

import java.util.List;

public class listTripAdapter extends BaseAdapter {
    private List<Trip> trips;
    private Context context;
    public listTripAdapter(List<Trip> trip, Context context){
        this.trips = trip;
        this.context=context;
        Log.i("size1"," "+ trip.size());
        for(int i=0;i<trip.size();i++)
            Log.i("size3"," "+ trip.get(i).getOwnerId());
    }


    @Override
    public int getCount() {
        return trips.size();
    }

    @Override
    public Object getItem(int position) {
        return trips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            view = View.inflate(parent.getContext(), R.layout.trip_list_item_admin,null);
        } else view = convertView;
        //bind date
        Trip trip = (Trip) getItem(position);
        ((TextView) view.findViewById(R.id.trip_id)).setText(String.format(" %s ", trip.getId()));
        ((TextView) view.findViewById(R.id.trip_name)).setText(String.format("Tên : %s", trip.getName()));
        Log.i("err"," "+ trip.getOwnerId());
        ((TextView) view.findViewById(R.id.trip_owner)).setText(String.format("Id người tạo : %s .", trip.getOwnerId()));
        return view;
    }
}
