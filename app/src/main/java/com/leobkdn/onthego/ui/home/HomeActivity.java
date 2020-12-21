package com.leobkdn.onthego.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leobkdn.onthego.data.model.LoggedInUser;
import com.leobkdn.onthego.data.result.Result;
import com.leobkdn.onthego.data.model.TripDestination;
import com.leobkdn.onthego.data.model.Weather;
import com.leobkdn.onthego.data.result.WeatherResult;
import com.leobkdn.onthego.tools.Reminder;
import com.leobkdn.onthego.ui.destination.DestinationActivity;
import com.leobkdn.onthego.ui.food.FoodActivity;
import com.leobkdn.onthego.ui.go.GoActivity;
import com.leobkdn.onthego.data.result.TripDestinationResult;
import com.leobkdn.onthego.ui.go.info.TripInfo;
import com.leobkdn.onthego.ui.profile.ProfileActivity;
import com.leobkdn.onthego.R;
import com.leobkdn.onthego.ui.login.LoginActivity;
import com.leobkdn.onthego.data.result.LoginResult;
import com.leobkdn.onthego.ui.login.LoginViewModel;
import com.leobkdn.onthego.ui.login.LoginViewModelFactory;
import com.leobkdn.onthego.ui.stay.StayActivity;
import com.leobkdn.onthego.ui.transport.TransportActivity;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private LoggedInUser user;
    private LoginViewModel loginViewModel;
    private boolean pressedOnce = false;
    private ArrayList<TripDestination> destinations = new ArrayList<>();
    private TripDestinationResult tripDestinationResult = new TripDestinationResult();
    private WeatherResult weatherResult = new WeatherResult();
    private ImageButton destination;
    private ImageButton transport;
    private ImageButton stay;
    private ImageButton food;
    private ImageButton goButton;
    private ImageButton userAvatar;
    private TextView username;
    private TextView currentTrip;
    private LinearLayout currentTripInfo;
    private TextView currentPos;
    private TextView nextTime;
    private Weather currentWeather;
    private TextView weatherAdvise;
    private ImageView weatherIcon;
    private TextView weatherDescription;

    @Override
    public void onRestart() {
        // back button pressed and return to this activity
        super.onRestart();
        user = new LoggedInUser(restorePrefsData("username"), restorePrefsData("email"), restorePrefsData("token"), false, new Date(restorePrefsLong("birthday")), restorePrefsData("address"));
        username.setText(user.getDisplayName());
        currentTrip.setText(restoreCurrentTripData("name") != null ? restoreCurrentTripData("name") : "Chưa chọn chuyến đi");

        if (restoreCurrentTripInt("id") != -1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    tripDestinationResult.fetchTripDestination(restorePrefsData("token"), restoreCurrentTripInt("id"));
                }
            }).start();
        }
    }

    // double-tap to exit activity
    @Override
    public void onBackPressed() {
        if (pressedOnce) {
            super.onBackPressed();
            return;
        }
        this.pressedOnce = true;
        Toast.makeText(this, "Nhấn một lần nữa để thoát", Toast.LENGTH_SHORT).show();
        // reset pressedOnce to false after 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // notification channel
        createNotificationChannel();

        // install http cache
        try {
            File httpCacheDir = new File(getCacheDir(), "http");
            long httpCacheSize = 1024 * 1024; // 1 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("HTTP cache", "HTTP response cache installation failed:" + e);
        }

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        user = new LoggedInUser(restorePrefsData("username"), restorePrefsData("email"), restorePrefsData("token"), false, new Date(restorePrefsLong("birthday")), restorePrefsData("address"));

        goButton = findViewById(R.id.home_button_0);
        destination = findViewById(R.id.home_button_1);
        transport = findViewById(R.id.home_button_2);
        stay = findViewById(R.id.home_button_3);
        food = findViewById(R.id.home_button_4);
        ImageButton powerButton = findViewById(R.id.powerButton);
        ImageButton settingButton = findViewById(R.id.settingButton);
        ProgressBar loading = findViewById(R.id.homeLoading);
        userAvatar = findViewById(R.id.userAvatar);
        LinearLayout profileSwitch = findViewById(R.id.linearLayout);
        //set avatar text
        username = findViewById(R.id.home_username);
        username.setText(user.getDisplayName());
        // current trip
        currentTrip = findViewById(R.id.home_currentTrip);
        currentTrip.setText(restoreCurrentTripData("name") != null ? restoreCurrentTripData("name") : "Chưa chọn chuyến đi");

        currentTripInfo = findViewById(R.id.home_currentTrip_info);
        currentPos = findViewById(R.id.home_currentPosition);
        nextTime = findViewById(R.id.home_nextStart);
        weatherAdvise = findViewById(R.id.home_weather_advise);
        weatherDescription = findViewById(R.id.home_weather_description);
        weatherIcon = findViewById(R.id.home_weather_icon);

        // Log Out Result listener
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    showLogoutFailed(loginResult.getError());
                    updateUi();
                    Toast.makeText(getApplicationContext(), loginResult.getError(), Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                if (loginResult.getSuccessString() != null) {
                    updateUi();
                    Toast.makeText(getApplicationContext(), loginResult.getSuccessString(), Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_OK);
                    //Complete and destroy login activity once successful
                    finish();
                }
            }
        });

        // profile switch listener
        profileSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        setupActivityButtons(userAvatar, new Intent(getApplicationContext(), ProfileActivity.class));
        setupActivityButtons(settingButton, new Intent(getApplicationContext(), ProfileActivity.class));
        setupActivityButtons(goButton, new Intent(getApplicationContext(), GoActivity.class));
        setupActivityButtons(destination, new Intent(getApplicationContext(), DestinationActivity.class));
        setupActivityButtons(transport, new Intent(getApplicationContext(), TransportActivity.class));
        setupActivityButtons(stay, new Intent(getApplicationContext(), StayActivity.class));
        setupActivityButtons(food, new Intent(getApplicationContext(), FoodActivity.class));

        // log out button listener
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("Thoát");
                alertDialog.setMessage("Bạn muốn đăng xuất hay thoát chương trình?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Đăng xuất",
                        new DialogInterface.OnClickListener() {

                            //Log out
                            public void onClick(DialogInterface dialog, int which) {
                                loading.setVisibility(View.VISIBLE);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginViewModel.logOut(user.getToken());
                                    }
                                }).start();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Thoát",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                alertDialog.show();
            }
        });
        currentTripInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restoreCurrentTripInt("id") != -1) {
                    Intent intent = new Intent(HomeActivity.this, TripInfo.class);
                    intent.putExtra("tripId", restoreCurrentTripInt("id"));
                    intent.putExtra("tripName", restoreCurrentTripData("name"));
                    intent.putExtra("tripOwner", restoreCurrentTripData("owner"));
                    startActivity(intent);
                }
            }
        });

        tripDestinationResult.getDestinationResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result instanceof Result.Success) {
                    if (((Result.Success) result).checkTypeString()) {
                        Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                    } else {
                        destinations = ((Result.Success<ArrayList<TripDestination>>) result).getData();
                        if (destinations != null) {
                            //set current trip info text
                            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                            long min = 999999999; int minI = -1;
                            for (int i=0;i<destinations.size();i++){
                                Timestamp start = destinations.get(i).getStartTime();
                                if (start != null && currentTime.getTime() - start.getTime() > 0 && currentTime.getTime() - start.getTime() < min){
                                    min = currentTime.getTime() - start.getTime();
                                    minI = i;
                                }
                            }
                            if (minI >= 0) {
                                currentPos.setText(destinations.get(minI).getName());
                                int finalMinI = minI;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        weatherResult.getCurrentWeather(restorePrefsData("token"), destinations.get(finalMinI).getLat(), destinations.get(finalMinI).getLon());
                                    }
                                }).start();
                            } else currentPos.setText(user.getAddress());

                            if (minI >=0 && destinations.get(minI).getFinishTime() != null) {
                                nextTime.setText(new SimpleDateFormat("HH:mm").format(destinations.get(minI).getFinishTime()));

                                // notification
                                Intent intent = new Intent(HomeActivity.this, Reminder.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 69, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                long fifteenMins = 15 * 60 * 1000;
                                long endTime = destinations.get(minI).getFinishTime().getTime();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && currentTime.getTime() <= endTime - fifteenMins)
                                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTime - fifteenMins, pendingIntent);
                            }
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        weatherResult.getWeatherResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result instanceof Result.Success) {
                    currentWeather = ((Result.Success<Weather>) result).getData();
                    if (currentWeather != null) {
                        String advise = "";
                        if (currentWeather.getTemp() < 21) advise += "Ngoài trời lạnh lắm, nhớ mang áo lạnh nhé! ";
                        weatherDescription.setText(currentWeather.getTemp()+"\u00B0C "+currentWeather.getDescription());
                        switch (currentWeather.getIcon()){
                            case "01d":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.d01));
                                break;
                            }
                            case "01n":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.n01));
                                break;
                            }
                            case "02d":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.d02));
                                break;
                            }
                            case "02n":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.n02));
                                break;
                            }
                            case "03n":
                            case "03d": {
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.d03));
                                break;
                            }
                            case "04n":
                            case "04d":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.d04));
                                advise += "Ngoài trời có nhiều mây, có khả năng trời sẽ mưa đấy!";
                                break;
                            }
                            case "09n":
                            case "09d":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.d09));
                                advise += "Ngoài trời đang mưa, nhớ mang theo áo mưa nhé!";
                                break;
                            }
                            case "10d":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.d10));
                                advise += "Ngoài trời đang mưa, nhớ mang theo áo mưa nhé!";
                                break;
                            }
                            case "10n":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.n10));
                                advise += "Ngoài trời đang mưa, nhớ mang theo áo mưa nhé!";
                                break;
                            }
                            case "11n":
                            case "11d":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.d11));
                                advise += "Ngoài trời có sấm chớp, hãy cẩn thận khi ra đường!";
                                break;
                            }
                            case "13n":
                            case "13d":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.d13));
                                advise += "Ngoài trời đang có tuyết, hãy đảm bảo bạn mặc đồ đủ ấm.";
                                break;
                            }
                            case "50n":
                            case "50d":{
                                weatherIcon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.d50));
                                advise += "Ngoài trời đang có sương mù, hãy cẩn thận khi ra đường!";
                                break;
                            }
                        }
                        weatherAdvise.setText(advise);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        if (restoreCurrentTripInt("id") != -1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    tripDestinationResult.fetchTripDestination(restorePrefsData("token"), restoreCurrentTripInt("id"));
                }
            }).start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    private void setupActivityButtons(ImageButton button, Intent intent) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    private void updateUi() {
        // initiate successful logout in experience
        clearPrefs("userPrefs");
        clearPrefs("currentTrip");
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    private void showLogoutFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void clearPrefs(String prefsName) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(prefsName, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
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

    private int restoreCurrentTripInt(String key) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("currentTrip", MODE_PRIVATE);
        return prefs.getInt(key, -1);
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "OnTheGoChannel";
            String description = "Channel for On The Go app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("onTheGo", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}