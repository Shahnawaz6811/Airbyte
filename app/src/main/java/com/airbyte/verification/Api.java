package com.airbyte.verification;

import com.airbyte.verification.OtpResponse;
import io.reactivex.Observable;
import java.util.List;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {
     public static final String authKey = "231717AJIj0MewC5b72b70c";
    public static final String baseUrl = "http://api.msg91.com/api/";

    @FormUrlEncoded
    @POST("create_new_user.php")
    Observable<String> createNewUser(@Field("phone") String str);

    @GET("get_admin.php")
    Observable<String> getAdminPhone();

    @GET("showcomplaint.php")
    Observable<List<String>> getComplaints();

    @GET("get_worker.php")
    Observable<String> getWorkerPhone();




    @GET("sendotp.php")
    Observable<OtpResponse> sendOtp(@Query("authkey") String str, @Query("mobile") String str2, @Query("otp") String str3);

    @FormUrlEncoded
    @POST("insert_payment_info.php")
    Observable<String> insertPaymentInfo(@Field("phone") String phone, @Field("c_id") String customerId, @Field("amount") String amount, @Field("txn_id") String txnId);
}
