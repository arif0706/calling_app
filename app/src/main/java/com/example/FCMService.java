package com.example;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=<Authorization_key>"

    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
