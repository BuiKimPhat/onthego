package com.leobkdn.onthego.ui.go.info;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.ui.destination.DestinationActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class TripInfoAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groupTitle;
    private LinkedHashMap<String, ArrayList<TripDestination>> destinationList;
    private LayoutInflater layoutInflater;
    public ArrayList<ArrayList<View>> childView = new ArrayList<>();
    private ArrayList<TripDestination> destinations;

    public TripInfoAdapter(Context context, List<String> groupTitle, LinkedHashMap<String, ArrayList<TripDestination>> destinationList, ArrayList<TripDestination> destinations) {
        this.context = context;
        this.groupTitle = groupTitle;
        this.destinationList = destinationList;
        this.destinations = destinations;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        if (groupPosition == 0) return 0;
        else return 1;
    }

    @Override
    public int getChildTypeCount() {
        return 2;
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
        if (convertView == null) {
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
        Log.w("getChildView", groupPosition + " " + childPosition);
        if (convertView == null){
            switch(getChildType(groupPosition, childPosition)){
                case 0:
                    //inflate type 1
                    if (convertView == null) {
                        convertView = layoutInflater.inflate(R.layout.trip_info_list_item_editable, parent, false);
                    }
                    break;
                case 1:
                    //inflate type 2
                    if (convertView == null) {
                        convertView = layoutInflater.inflate(R.layout.trip_info_list_item, parent, false);
                    }
                    break;
            }
        }
        if (groupPosition == 0) {
//            if (convertView == null) {
//                convertView = layoutInflater.inflate(R.layout.trip_info_list_item_editable, parent, false);
//            }
            TextView destination = convertView.findViewById(R.id.destination_name);
            destination.setText(item.getName());
            EditText dateEdit = convertView.findViewById(R.id.trip_destination_date_edit);
            TextView date = convertView.findViewById(R.id.trip_destination_date);
            TextView startTime = convertView.findViewById(R.id.trip_destination_startTime);
            EditText startTimeEdit = convertView.findViewById(R.id.trip_destination_startTime_edit);
            Button confirmBtn = ((Activity) context).findViewById(R.id.trip_info_confirm);
            TextView endTime = convertView.findViewById(R.id.trip_destination_endTime);
            EditText endTimeEdit = convertView.findViewById(R.id.trip_destination_endTime_edit);
            ImageButton timeEdit = convertView.findViewById(R.id.trip_time_edit_button);
            ImageButton delete = convertView.findViewById(R.id.trip_destination_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    destinations.remove(childPosition);
                    ExpandableListView elv = ((Activity) context).findViewById(R.id.trip_destinations_listView);
                    LinkedHashMap<String, ArrayList<TripDestination>> data = new TripInfoDataPump(destinations).getData();
                    TripInfoAdapter adapter = new TripInfoAdapter(context, new ArrayList<String>(data.keySet()), data, destinations);
                    elv.setAdapter(adapter);
                    for (int i = 0; i < adapter.getGroupCount(); i++) {
                        elv.expandGroup(i);
                    }
                    confirmBtn.setVisibility(View.VISIBLE);
                }
            });
            if (item.getStartTime() != null) {
                date.setText(new SimpleDateFormat("dd/MM/yyyy").format(item.getStartTime()));
                dateEdit.setText(date.getText().toString());
                startTime.setText(new SimpleDateFormat("HH:mm").format(item.getStartTime()));
                startTimeEdit.setText(startTime.getText().toString());
            }
            if (item.getFinishTime() != null) {
                endTime.setText(new SimpleDateFormat("HH:mm").format(item.getFinishTime()));
                endTimeEdit.setText(endTime.getText().toString());
            }
            //TODO: write functions to reuse code
            //setup date, time picker dialog
            dateEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Date Select Listener.
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            dateEdit.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    };
                    Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog dialog = new DatePickerDialog(context, dateSetListener, mYear, mMonth, mDay);
                    dialog.show();
                }
            });
            startTimeEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Time Set Listener.
                    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            startTimeEdit.setText(hourOfDay + ":" + minute);
                        }
                    };
                    TimePickerDialog dialog = new TimePickerDialog(context, timeSetListener, 7, 0, true);
                    dialog.show();
                }
            });
            endTimeEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Time Set Listener.
                    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            endTimeEdit.setText(hourOfDay + ":" + minute);
                        }
                    };
                    TimePickerDialog dialog = new TimePickerDialog(context, timeSetListener, 7, 0, true);
                    dialog.show();
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
//            if (convertView == null) {
//                convertView = layoutInflater.inflate(R.layout.trip_info_list_item, parent, false);
//            }
            TextView destination = convertView.findViewById(R.id.destination_name);
            destination.setText(item.getName());
            TextView startTime = convertView.findViewById(R.id.trip_destination_startTime);
            TextView endTime = convertView.findViewById(R.id.trip_destination_endTime);
            if (item.getStartTime() != null) {
                startTime.setText(new SimpleDateFormat("HH:mm").format(item.getStartTime()));
            }
            if (item.getFinishTime() != null) {
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
