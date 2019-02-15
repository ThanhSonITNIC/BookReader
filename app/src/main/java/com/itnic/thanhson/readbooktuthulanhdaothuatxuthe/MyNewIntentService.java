package com.itnic.thanhson.readbooktuthulanhdaothuatxuthe;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by ThanhSon on 3/23/2018.
 */

public class MyNewIntentService extends IntentService{
    private static final int NOTIFICATION_ID = 3;
    public static String Title = "Đến giờ đọc sách!";
    public static String Text = "Đọc tiếp...";

    public MyNewIntentService() {
        super("MyNewIntentService");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(Title);
        builder.setContentText(Text);
        builder.setSmallIcon(R.drawable.rate);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        builder.setAutoCancel(true);

        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }
}
