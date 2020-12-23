package com.shorbgy.elwhats.notification;

import com.shorbgy.elwhats.pojo.MyResponse;
import com.shorbgy.elwhats.pojo.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationApi {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAqua-3ow:APA91bFK1nPe8r7iA94osdQlZIHAue7hMj5Xaw262MTH8dBSKdwgqDAHzWMPih7gApSHfypIh15cxSXOTxepZsvJItuVuIw3n_iiGLVDpEkwg0umAnQl5wN4LEbfak8_whVngpVPgvzz"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
