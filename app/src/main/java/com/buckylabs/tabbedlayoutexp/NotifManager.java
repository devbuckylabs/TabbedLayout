package com.buckylabs.tabbedlayoutexp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotifManager {

    private static final String CHANNEL_ID = "Auto_Backup";
    private static final String CHANNEL_NAME = "Spooks";
    private static final String CHANNEL_DESC = "Adios";

    private static final int NOTIFICATION_ID = 404;

    private Context context;

    public NotifManager(Context context) {
        this.context = context;
    }


    public void displayNotification(String appname) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        builder.setContentTitle("Auto Backup Agent");
        builder.setContentText("New App : " + appname.toUpperCase() + "  successfully archived");
        builder.setAutoCancel(true);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());


    }


}
