package com.leobkdn.onthego.ui.destination;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.ui.destination.info.DestinationInfo;
import com.leobkdn.onthego.ui.go.Trip;

import java.util.ArrayList;

public class DestinationListAdapter extends BaseAdapter {
    private ArrayList<Destination> destinations;
    private LayoutInflater layoutInflater;
    private Context context;

    public DestinationListAdapter(Context context, ArrayList<Destination> destinations) {
        this.destinations = destinations;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return destinations.size();
    }

    @Override
    public Object getItem(int position) {
        return destinations.get(position);
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
        TextView name = convertView.findViewById(R.id.trip_listItem_name);
        TextView city = convertView.findViewById(R.id.trip_listItem_owner);
        LinearLayout textLayout = convertView.findViewById(R.id.trip_listItem_text);
        name.setText(destinations.get(position).getName());
        city.setText(destinations.get(position).getCity());
        textLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DestinationInfo.class);
                intent.putExtra("name", destinations.get(position).getName());
                intent.putExtra("address", destinations.get(position).getAddress());
                intent.putExtra("phone", destinations.get(position).getPhone());
                intent.putExtra("description", destinations.get(position).getDescription());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
