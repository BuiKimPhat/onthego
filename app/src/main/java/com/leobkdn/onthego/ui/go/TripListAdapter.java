package com.leobkdn.onthego.ui.go;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.Trip;
import com.leobkdn.onthego.ui.go.info.TripInfo;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TripListAdapter extends BaseAdapter {
    private ArrayList<Trip> trips;
    private LayoutInflater layoutInflater;
    private Context context;
    private ImageView oldActive;

    public TripListAdapter(Context context, ArrayList<Trip> trips) {
        this.trips = trips;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
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
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.trip_list_item, parent, false);
        }
        ImageView activeImg = convertView.findViewById(R.id.trip_listItem_active);
        TextView tripName = convertView.findViewById(R.id.trip_listItem_name);
        TextView tripOwner = convertView.findViewById(R.id.trip_listItem_owner);
        LinearLayout tripText = convertView.findViewById(R.id.trip_listItem_text);
        ImageButton tripMore = convertView.findViewById(R.id.trip_listItem_more);

        if (getItem(position) != null) {
            tripName.setText(trips.get(position).getName());
            tripOwner.setText(trips.get(position).getOwner());
            if (trips.get(position).getId() == restorePrefsInt("id")) {
                activeImg.setVisibility(View.VISIBLE);
                oldActive = activeImg;
            } else {
                activeImg.setVisibility(View.GONE);
            }
            Intent reqIntent = ((Activity) context).getIntent();
            if (reqIntent.getStringExtra("mode") != null && reqIntent.getStringExtra("mode").equals("add")) {
                tripText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent resIntent = new Intent();
                        resIntent.putExtra("tripID", trips.get(position).getId());
                        resIntent.putExtra("destinationID", reqIntent.getIntExtra("destinationID", -1));
                        ((Activity) context).setResult(Activity.RESULT_OK, resIntent);
                        ((Activity) context).finish();
                    }
                });
            } else {
                tripText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savePrefsData("id", trips.get(position).getId());
                        savePrefsData("name", trips.get(position).getName());
                        if (oldActive != null) oldActive.setVisibility(View.GONE);
                        activeImg.setVisibility(View.VISIBLE);
                        oldActive = activeImg;
                    }
                });
            }
            tripMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TripInfo.class);
                    intent.putExtra("tripId", trips.get(position).getId());
                    intent.putExtra("tripName", trips.get(position).getName());
                    intent.putExtra("tripOwner", trips.get(position).getOwner());
                    ((Activity) context).startActivityForResult(intent, 3);
                }
            });
        }
        return convertView;
    }

    private int restorePrefsInt(String key) {
        SharedPreferences prefs = context.getSharedPreferences("currentTrip", MODE_PRIVATE);
        return prefs.getInt(key, -1);
    }

    private void savePrefsData(String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences("currentTrip", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void savePrefsData(String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences("currentTrip", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
