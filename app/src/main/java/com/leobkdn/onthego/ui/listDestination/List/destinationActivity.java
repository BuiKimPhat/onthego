package com.leobkdn.onthego.ui.listDestination.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.source.DestinationDataSource;
import com.leobkdn.onthego.data.model.Destination;
import com.leobkdn.onthego.tools.VNCharacterUtils;
import com.leobkdn.onthego.ui.destination.DestinationListAdapter;
import com.leobkdn.onthego.ui.modify_user.list.UserListActivity;
import com.leobkdn.onthego.ui.listDestination.modify.modifyDetinatiobInfoActivity;
import com.leobkdn.onthego.ui.modify_user.user.ModifyUserActivity;
import com.leobkdn.onthego.ui.listDestination.modify.addDestinationActivity;

import java.util.ArrayList;
import java.util.Date;

public class destinationActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Destination> des;
    private LoggedInUser user;
    private ArrayList<Destination> display = new ArrayList<>();
    private Spinner sortList;
    private EditText search;
    private int Position =0;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_destination);
        DestinationDataSource a = new DestinationDataSource();

        user = new LoggedInUser(restorePrefsData("username"),restorePrefsData("email"),restorePrefsData("token"),true,new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
        //des = new Result<ArrayList<Destination>()>;

        search = findViewById(R.id.destination_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                display = linearSearch(des, search.getText().toString());
                quickSortBy(display, 0, display.size() - 1, sortList.getSelectedItem().toString());
                listView.setAdapter(new destiantionAdapter(display,destinationActivity.this));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sortList = findViewById(R.id.destination_sort);
        // Create an ArrayAdapter using the cities string array and a default spinner layout
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this, R.array.sortDestinations, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sortList.setAdapter(sortAdapter);
        sortList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                quickSortBy(display, 0, display.size() - 1, selectedItem);
                listView.setAdapter(new destiantionAdapter( display,destinationActivity.this));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try{
            des = a.fetchDestinations2(user.getToken());
            display = des;
        }catch (Exception err){
            Toast.makeText(destinationActivity.this," "+err,Toast.LENGTH_SHORT).show();
        }
        listView = findViewById(R.id.destination_list_view);
        fab = findViewById(R.id.addNewDestinationButton);
        destiantionAdapter adapter = new destiantionAdapter(des,this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Position = display.get(position).getId();
                Intent intent= new Intent(destinationActivity.this , modifyDetinatiobInfoActivity.class);
                intent.putExtra("Position",Position);
                startActivity(intent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(destinationActivity.this , addDestinationActivity.class);
                startActivity(intent);
            }
        });
    }
    // get prefs from storage
    private String restorePrefsData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
    private long restorePrefsLong(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("userPrefs", MODE_PRIVATE);
        return prefs.getLong(key, 0);
    }
    private String restoreCurrentTripData(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("currentTrip", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
    private void quickSortBy(ArrayList<Destination> source, int start, int end, String by) {
        if (by.equals("Theo số người đánh giá: Cao -> thấp")) {
            if (start < end) {
                int i = start, j = end;
                int chot = source.get((start + end) / 2).getRateNum(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getRateNum() > chot) i++;
                    while (source.get(j).getRateNum() < chot) j--;
                    if (i <= j) {
                        Destination temp = new Destination(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo số người đánh giá: Thấp -> cao")) {
            if (start < end) {
                int i = start, j = end;
                int chot = source.get((start + end) / 2).getRateNum(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getRateNum() < chot) i++;
                    while (source.get(j).getRateNum() > chot) j--;
                    if (i <= j) {
                        Destination temp = new Destination(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo đánh giá: Thấp -> cao")) {
            if (start < end) {
                int i = start, j = end;
                float chot;
                chot = source.get((start + end) / 2).getRating(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getRating() < chot) i++;
                    while (source.get(j).getRating() > chot) j--;
                    if (i <= j) {
                        Destination temp = new Destination(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo tên: A -> Z")) {
            if (start < end) {
                int i = start, j = end;
                String chot;
                chot = source.get((start + end) / 2).getName().toLowerCase(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getName().toLowerCase().compareTo(chot) < 0) i++;
                    while (source.get(j).getName().toLowerCase().compareTo(chot) > 0) j--;
                    if (i <= j) {
                        Destination temp = new Destination(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else if (by.equals("Theo tên: Z -> A")) {
            if (start < end) {
                int i = start, j = end;
                String chot;
                chot = source.get((start + end) / 2).getName().toLowerCase(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getName().toLowerCase().compareTo(chot) > 0) i++;
                    while (source.get(j).getName().toLowerCase().compareTo(chot) < 0) j--;
                    if (i <= j) {
                        Destination temp = new Destination(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        } else {
            if (start < end) {
                int i = start, j = end;
                float chot;
                chot = source.get((start + end) / 2).getRating(); // chon phan tu o giua lam chot
                while (i < j) {
                    while (source.get(i).getRating() > chot) i++;
                    while (source.get(j).getRating() < chot) j--;
                    if (i <= j) {
                        Destination temp = new Destination(source.get(i));
                        source.set(i, source.get(j));
                        source.set(j, temp);
                        i++;
                        j--;
                    }
                }
                quickSortBy(source, start, j, by);
                quickSortBy(source, i, end, by);
            }
        }
    }

    private ArrayList<Destination> linearSearch(ArrayList<Destination> source, String str) {
        ArrayList<Destination> result = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            if (VNCharacterUtils.removeAccent(source.get(i).getName().toLowerCase()).contains(VNCharacterUtils.removeAccent(str.toLowerCase())))
                result.add(source.get(i));
        }
        return result;
    }
}
