package com.leobkdn.onthego.ui.destination;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.ui.destination.info.DestinationInfo;
import com.leobkdn.onthego.ui.go.GoActivity;

import java.util.ArrayList;

public class DestinationListAdapter extends BaseAdapter {
    private ArrayList<Destination> destinations;
    private LayoutInflater layoutInflater;
    private Context context;
    private Intent intent;

    public DestinationListAdapter(Context context, Intent intent, ArrayList<Destination> destinations) {
        this.destinations = destinations;
        this.context = context;
        this.intent = intent;
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
        ImageButton moreBtn = convertView.findViewById(R.id.trip_listItem_more);
        name.setText(destinations.get(position).getName());
        city.setText(destinations.get(position).getCity());
        if (intent.getStringExtra("mode") != null && intent.getStringExtra("mode").equals("add")) {
            moreBtn.setOnClickListener(new View.OnClickListener() {
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
            textLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent result = new Intent();
                    result.putExtra("destinationID", destinations.get(position).getId());
                    result.putExtra("destinationName", destinations.get(position).getName());
                    ((Activity) context).setResult(Activity.RESULT_OK, result);
                    ((Activity) context).finish();
                }
            });
        } else {
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
            moreBtn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_add_24));
            moreBtn.setColorFilter(R.color.colorTextPrimary);
            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GoActivity.class);
                    intent.putExtra("mode", "add");
                    intent.putExtra("destinationID", destinations.get(position).getId());
//                    Log.w("destinationID", String.valueOf(destinations.get(position).getId()));
                    ((Activity) context).startActivityForResult(intent, 2);
                }
            });
        }
        return convertView;
    }
}
