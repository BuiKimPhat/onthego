package com.leobkdn.onthego.ui.go.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.TripDestination;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TripInfoAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groupTitle;
    private HashMap<String, ArrayList<TripDestination>> destinationList;
    private LayoutInflater layoutInflater;

    public TripInfoAdapter(Context context, List<String> groupTitle, HashMap<String, ArrayList<TripDestination>> destinationList) {
        this.context = context;
        this.groupTitle = groupTitle;
        this.destinationList = destinationList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return groupTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return destinationList.get(groupTitle.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return destinationList.get(groupTitle.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String thisTitle = (String) getGroup(groupPosition);
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.trip_info_list_group, parent, false);
        }
        TextView groupTitle = convertView.findViewById(R.id.trip_info_group_title);
        groupTitle.setText(thisTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TripDestination item = (TripDestination) getChild(groupPosition, childPosition);
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.trip_info_list_item, parent, false);
        }
        TextView destination = convertView.findViewById(R.id.destination_name);
        destination.setText(item.getName());
        ImageButton editDestination = convertView.findViewById(R.id.trip_destination_edit_button);
        ImageButton deleteDestination = convertView.findViewById(R.id.trip_destination_delete);
        TextView date = convertView.findViewById(R.id.trip_destination_date);
        EditText dateEdit = convertView.findViewById(R.id.trip_destination_date_edit);
        TextView startTime = convertView.findViewById(R.id.trip_destination_startTime);
        EditText startTimeEdit = convertView.findViewById(R.id.trip_destination_startTime_edit);
        if (item.getStartTime() != null) {
            date.setText(new SimpleDateFormat("dd/MM/yyyy").format(item.getStartTime()));
            dateEdit.setText(new SimpleDateFormat("dd/MM/yyyy").format(item.getStartTime()));
            startTime.setText(new SimpleDateFormat("HH:mm").format(item.getStartTime()));
            startTimeEdit.setText(new SimpleDateFormat("HH:mm").format(item.getStartTime()));
        }
        TextView endTime = convertView.findViewById(R.id.trip_destination_endTime);
        EditText endTimeEdit = convertView.findViewById(R.id.trip_destination_endTime_edit);
        if (item.getFinishTime() != null){
            endTime.setText(new SimpleDateFormat("HH:mm").format(item.getFinishTime()));
            endTimeEdit.setText(new SimpleDateFormat("HH:mm").format(item.getFinishTime()));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
