package com.leobkdn.onthego.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.leobkdn.onthego.R;

public class Reminder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "onTheGo")
                .setSmallIcon(R.drawable.onthego_logo)
                .setContentTitle("Còn 15 phút nữa là hết thời gian")
                .setContentText("Nhanh chân lên nào, còn 15 phút nữa là hết thời gian dành cho địa điểm này rồi!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(696, builder.build());
    }
}
