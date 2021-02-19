package com.example.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.IncomingCall;
import com.example.MainActivity;
import com.example.mycallingapp.R;

public class NotificationHelper {

    public static  void displayNotification(Context context, String title, String body, String icon) {
Intent intent=new Intent(context, IncomingCall.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(
                context,
                100,
                intent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_videocam_24)
                        .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(title.hashCode(), mBuilder.build());
    }
}
