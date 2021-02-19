package com.example;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA8NeVvkk:APA91bGZJlCzyEFr8_ji5DMU6qmRIjKC7d8i64IgXWPlS3jBSEnrtL2Gtth-notRuz80jx14kV_0nChASYLqpb17tzTVsBzfcxfvdDfHqH3HHEI5b696vpt_9xJuisIzK_DasQjJ9e-A"

    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
