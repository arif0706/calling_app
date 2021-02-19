package com.example;

public class Common {

    public static final String fcmURL="https://fcm.googleapis.com/";


    public static FCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(FCMService.class);
    }
}
