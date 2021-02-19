package com.example.Service;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.Calling;
import com.example.IncomingCall;
import com.example.mycallingapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    Context  context;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {


        System.out.println("background!");
        if(remoteMessage.getNotification()!=null)
        {
            String title=remoteMessage.getNotification().getTitle();
            String body=remoteMessage.getNotification().getBody();
            String icon=remoteMessage.getNotification().getIcon();
            NotificationHelper.displayNotification(this,title,body,icon);
        }
        else {
            Map<String, String> data = remoteMessage.getData();
            String Name = data.get("body").toString();
            String ChannelName = data.get("ChannelName");
            String valid = data.get("valid");

            System.out.println("name" + Name);

            System.out.println("ChannelName" + ChannelName);

            System.out.println("Data getting" + remoteMessage.getData());
            NotificationHelper.displayNotification(this,"Incoming Call",Name,"");

            Intent intent = new Intent(getBaseContext(), IncomingCall.class);
            intent.putExtra("Name", Name);
            intent.putExtra("ChannelName", ChannelName);
            intent.putExtra("valid", valid);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}
