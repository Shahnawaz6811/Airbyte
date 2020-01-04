package com.airbyte.customer;

import com.airbyte.customer.payment.Payment;
import com.airbyte.verification.Response;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CustomerApiService {

    String BASE_URL = "http://www.airbyte.in/android/airbyte/user/";
    String GENRATE_CHECKSUM = "http://www.airbyte.in/android/payment/paytm/generateChecksum.php";

    @FormUrlEncoded
    @POST("register")
    Observable<Response> registerUser(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("register_complaint")
    Observable<String> registerComplaint(@Field("customer_id") String userId, @Field("complaint") String complaint);

    @GET("complaints")
    Observable<List<String>> getComplaintTemplates();

    @FormUrlEncoded
    @POST("payment")
    Observable<Payment> insertPaymentInfo(@Field("phone")String customerPhone, @Field("customer_id") String customerId, @Field("pay_amount")String amount, @Field("txn_id")String txnId,@Field("txn_date")String txnDate,@Field("order_id")String orderId,@Field("txn_status")String txnStatus);

    @FormUrlEncoded
    @POST("last_payment")
    Observable<Payment> getLastPaymentInfo(@Field("customer_id") String customerId);

    @FormUrlEncoded
    @POST("index.php")
    Observable<String>  makePayment(@Field("amount") String amount);

    @FormUrlEncoded
    @POST("insert_customer_query")
    Observable<String> insertCustomerContactQuery(@Field("ct_id") String customerId,@Field("query") String query);

     @GET("get_order_id")
     Observable<String>  getOrderId();

}
