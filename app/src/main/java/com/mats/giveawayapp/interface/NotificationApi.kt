package com.mats.giveawayapp.`interface`

import com.mats.giveawayapp.models.PushNotification
import com.mats.giveawayapp.utils.Constants.CONTENT_TYPE
import com.mats.giveawayapp.utils.Constants.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization: key=$SERVER_KEY","Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}