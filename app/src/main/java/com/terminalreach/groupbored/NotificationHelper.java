package com.terminalreach.groupbored;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationHelper {

    private static final String CHANNEL_ID = "groupbored";


    public static void displayNotification(Context context, String title, String body) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_small_primary)
                        .setContentTitle("Is it working?")
                        .setContentText("First notification...")
                        .setPriority(Notification.PRIORITY_DEFAULT);
        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(context);
        mNotificationMgr.notify(0, mBuilder.build());

        Log.d("firebaseMessageReceived", "display notification");
        /*Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.logo_small_primary)
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(Notification.PRIORITY_MAX);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }*/
    }
}
