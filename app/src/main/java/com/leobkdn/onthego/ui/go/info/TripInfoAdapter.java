package com.leobkdn.onthego.ui.go.info;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.ui.destination.DestinationActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class TripInfoAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groupTitle;
    private LinkedHashMap<String, ArrayList<TripDestination>> destinationList;
    private LayoutInflater layoutInflater;
    public ArrayList<ArrayList<View>> childView = new ArrayList<>();

    public TripInfoAdapter(Context context, List<String> groupTitle, LinkedHashMap<String, ArrayList<TripDestination>> destinationList) {
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
        childView.add(new ArrayList<>());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TripDestination item = (TripDestination) getChild(groupPosition, childPosition);
        if (groupPosition==0){
            if (convertView == null){
                convertView = layoutInflater.inflate(R.layout.trip_info_list_item_editable, parent, false);
            }
            TextView destination = convertView.findViewById(R.id.destination_name);
            destination.setText(item.getName());
            ImageButton editDestination = convertView.findViewById(R.id.trip_destination_edit_button);
            EditText dateEdit = convertView.findViewById(R.id.trip_destination_date_edit);
            TextView date = convertView.findViewById(R.id.trip_destination_date);
            TextView startTime = convertView.findViewById(R.id.trip_destination_startTime);
            EditText startTimeEdit = convertView.findViewById(R.id.trip_destination_startTime_edit);
            Button confirmBtn = ((Activity) context).findViewById(R.id.trip_info_confirm);
            TextView endTime = convertView.findViewById(R.id.trip_destination_endTime);
            EditText endTimeEdit = convertView.findViewById(R.id.trip_destination_endTime_edit);
            ImageButton timeEdit = convertView.findViewById(R.id.trip_time_edit_button);
            if (item.getStartTime() != null) {
                date.setText(new SimpleDateFormat("dd/MM/yyyy").format(item.getStartTime()));
                dateEdit.setText(date.getText().toString());
                startTime.setText(new SimpleDateFormat("HH:mm").format(item.getStartTime()));
                startTimeEdit.setText(startTime.getText().toString());
            }
            if (item.getFinishTime() != null){
                endTime.setText(new SimpleDateFormat("HH:mm").format(item.getFinishTime()));
                endTimeEdit.setText(endTime.getText().toString());
            }
            editDestination.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DestinationActivity.class);
                    intent.putExtra("mode","edit");
                    context.startActivity(intent);
                }
            });
            timeEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeEdit.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                    dateEdit.setVisibility(View.VISIBLE);
                    startTime.setVisibility(View.GONE);
                    startTimeEdit.setVisibility(View.VISIBLE);
                    endTime.setVisibility(View.GONE);
                    endTimeEdit.setVisibility(View.VISIBLE);
                    confirmBtn.setVisibility(View.VISIBLE);
                    dateEdit.clearFocus();
                    startTime.clearFocus();
                    endTime.clearFocus();
                }
            });
//            confirmBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    progressBar.setVisibility(View.VISIBLE);
//                    confirmBtn.setVisibility(View.GONE);
//                    tripNameEdit.setVisibility(View.GONE);
//                    tripName.setText(tripNameEdit.getText());
//                    tripName.setVisibility(View.VISIBLE);
//                    timeEdit.setVisibility(View.VISIBLE);
//                    date.setText(dateEdit.getText());
//                    date.setVisibility(View.VISIBLE);
//                    dateEdit.setVisibility(View.GONE);
//                    startTime.setText(startTimeEdit.getText());
//                    startTime.setVisibility(View.VISIBLE);
//                    startTimeEdit.setVisibility(View.GONE);
//                    endTime.setText(endTimeEdit.getText());
//                    endTime.setVisibility(View.VISIBLE);
//                    endTimeEdit.setVisibility(View.GONE);
//                }
//            });
        } else {
            if (convertView == null){
                convertView = layoutInflater.inflate(R.layout.trip_info_list_item, parent, false);
            }
            TextView destination = convertView.findViewById(R.id.destination_name);
            destination.setText(item.getName());
            TextView startTime = convertView.findViewById(R.id.trip_destination_startTime);
            TextView endTime = convertView.findViewById(R.id.trip_destination_endTime);
            if (item.getStartTime() != null) {
                startTime.setText(new SimpleDateFormat("HH:mm").format(item.getStartTime()));
            }
            if (item.getFinishTime() != null){
                endTime.setText(new SimpleDateFormat("HH:mm").format(item.getFinishTime()));
            }
        }
//        if (groupPosition==0) {
//            LinearLayout wrapper = convertView.findViewById(R.id.trip_destination_item);
//            wrapper.setOrientation(LinearLayout.VERTICAL);
//            editDestination.setVisibility(View.VISIBLE);
//            deleteDestination.setVisibility(View.VISIBLE);
//            ImageButton timeEditBtn = convertView.findViewById(R.id.trip_time_edit_button);
//            timeEditBtn.setVisibility(View.VISIBLE);
//            date.setVisibility(View.VISIBLE);
//            timeEditBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    date.setVisibility(View.GONE);
//                    dateEdit.setVisibility(View.VISIBLE);
//                    startTime.setVisibility(View.GONE);
//                    startTimeEdit.setVisibility(View.VISIBLE);
//                    endTime.setVisibility(View.GONE);
//                    endTimeEdit.setVisibility(View.VISIBLE);
//                    confirmBtn.setVisibility(View.VISIBLE);
////                    confirmBtn.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            progressBar.setVisibility(View.VISIBLE);
////                            confirmBtn.setVisibility(View.GONE);
////                            tripNameEdit.setVisibility(View.GONE);
////                            tripName.setText(tripNameEdit.getText());
////                            tripName.setVisibility(View.VISIBLE);
////                            dateEdit.setVisibility(View.GONE);
////                            date.setText(dateEdit.getText());
////                            date.setVisibility(View.VISIBLE);
////                            startTimeEdit.setVisibility(View.GONE);
////                            startTime.setText(startTime.getText());
////                            startTime.setVisibility(View.VISIBLE);
////                            endTimeEdit.setVisibility(View.GONE);
////                            endTime.setText(endTimeEdit.getText());
////                            endTime.setVisibility(View.VISIBLE);
////                        }
////                    });
//                }
//            });
//        }
        childView.get(groupPosition).add(convertView);
        return convertView;
    }
    public View getChildView(int groupPosition, int childPosition) {
        return childView.get(groupPosition).get(childPosition);
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
