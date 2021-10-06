package com.mats.giveawayapp.ui.fragments;

import com.mats.giveawayapp.notifications.MyResponse;
import com.mats.giveawayapp.notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAExLgej4:APA91bFaxa7YnW80McVYVbr0Rn_6TJgnBxq1E_MzYJ86OFpxH2sAepKhY2YK7eh71wvYMYsW-lzygcpNqeWks47LfTylK6Xxjxqhr3ORZeDsxxz8k4JKW0Pn9KESEibdB7yiIDtjCbD2"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
