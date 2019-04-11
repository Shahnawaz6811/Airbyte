package com.airbyte.utils;

import com.airbyte.verification.OtpResponse;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MessageApiService {

      String MSG91_AUTH_KEY = "231717AJIj0MewC5b72b70c";
     String api91BaseUrl = "https://api.msg91.com/api/";


    @GET("sendhttp.php")
    Observable<String> sendMessage(@Query("authkey") String authKey, @Query("mobiles") String phone,
                                   @Query("message") String message, @Query("route") String route,
                                   @Query("sender") String sender);

    @GET("sendotp.php")
    Observable<OtpResponse> sendOtp(@Query("authkey") String str, @Query("mobile") String str2, @Query("otp") String str3);

}
