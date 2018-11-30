package com.ua.cs495f2018.berthaIRT;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import static android.support.v4.app.NotificationCompat.DEFAULT_SOUND;
import static android.support.v4.app.NotificationCompat.DEFAULT_VIBRATE;


public class FirebaseNet extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        String extra0 = "";
        String extra1 = "";

        //if you passed data
        if(remoteMessage.getData() != null) {
            extra0 = remoteMessage.getData().get("extra0");
            extra1 = remoteMessage.getData().get("extra1");
        }

        String title = "";
        String body = "";
        String action = "";

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            action = remoteMessage.getNotification().getClickAction();
        }

        //display that notification
        doNotification(title, body, action, extra0, extra1);

    }

    @Override
    public void onNewToken(String token) {
        System.out.println("FCMTOKEN" + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void doNotification(String messageTitle, String messageBody, String action, String extra0, String extra1) {
        //force the user to login by default
        Intent intent = new Intent(this, AdminLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //if they're logged in then don't force login, instead handle the action sent from server
        if(Client.userAttributes != null) {
            intent = new Intent(action);
            intent.putExtra("id", extra0);
            intent.putExtra("frag", extra1);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.ic_report_red_24dp)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "BerthaIRT Notification Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        Objects.requireNonNull(notificationManager).notify(0, notificationBuilder.build());
    }
}
