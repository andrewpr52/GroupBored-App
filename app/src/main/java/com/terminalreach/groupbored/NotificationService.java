package com.terminalreach.groupbored;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    public NotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("firebaseMessageReceived", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("firebaseMessageReceived", "Message data payload: " + remoteMessage.getData());
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("text");
            //String clickAction = remoteMessage.getNotification().getClickAction();
            //String fromUserID = remoteMessage.getData().get("from_user_id");
            String postID = remoteMessage.getData().get("post_id");
            String postUsername = remoteMessage.getData().get("post_username");
            String postTimestamp = remoteMessage.getData().get("post_timestamp");
            String postGroup = remoteMessage.getData().get("post_group");
            String postContents = remoteMessage.getData().get("post_contents");
            String postProfilePictureURL = remoteMessage.getData().get("post_profile_picture_url");

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setSmallIcon(R.drawable.logo_small_primary)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_small_primary))
                            .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                            .setPriority(Notification.PRIORITY_MAX);

            Intent resultIntent = new Intent(this, FullscreenPostActivity.class);
            resultIntent.putExtra("postID", postID);
            resultIntent.putExtra("username", postUsername);
            resultIntent.putExtra("timestamp", postTimestamp);
            resultIntent.putExtra("group", postGroup);
            resultIntent.putExtra("contents", postContents);
            resultIntent.putExtra("profilePictureURL", postProfilePictureURL);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
            int mNotificationId = (int) System.currentTimeMillis();
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel("10001", "NOTIFICATION_CHANNEL_NAME", importance);

                mBuilder.setChannelId("10001");
                if (mNotifyMgr != null) {
                    mNotifyMgr.createNotificationChannel(notificationChannel);
                }
            }
            if (mNotifyMgr != null) {
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        }
    }
}
