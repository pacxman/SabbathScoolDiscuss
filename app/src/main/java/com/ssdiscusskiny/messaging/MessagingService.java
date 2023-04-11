package com.ssdiscusskiny.messaging;

import android.app.PendingIntent;
import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ssdiscusskiny.MainActivity;
import com.ssdiscusskiny.R;


public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("MESSAGING_SERVICES", "MESSAGING SERVICES");

        if (remoteMessage.getFrom().equals("ssdiscusskiny")) {
            notify(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        } else if (remoteMessage.getFrom().equals("webtopic")) {
            //action was discarded

            //webTopicNot(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        }

        Log.d("MESSAGING_SERVICES", "Remote message called... from " + remoteMessage.getFrom());

    }

    private void notify(String topic, String title) {

        Intent startActivity = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, startActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "SSD_MESSAGING");
        mBuilder.setSmallIcon(R.drawable.ic_personal_ministry)
                .setContentTitle(topic)
                .setContentText(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(3, mBuilder.build());

    }

    /*private void webTopicNot(String topic, String title) {

        String ttle = title;
        String extra = "";
        if (title.contains("@")) {
            ttle = title.substring(0, title.indexOf("@"));
            extra = title.substring(title.indexOf("@"), title.length());
        }

        Intent startActivity = new Intent(getApplicationContext(), WebActivity.class);
        if (!extra.isEmpty()) {
            startActivity.putExtra("link", extra);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, startActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "SSD_MESSAGING");
        mBuilder.setSmallIcon(R.drawable.ic_personal_ministry)
                .setContentTitle(topic)
                .setContentText(ttle)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(3, mBuilder.build());
    }*/
}
