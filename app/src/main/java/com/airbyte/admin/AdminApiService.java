package com.airbyte.admin;

import com.airbyte.customer.complaints.Complaint;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AdminApiService {

    String baseUrl = "http://www.airbyte.in/android/airbyte/admin/";

    @GET("customer_complaints")
    Observable<List<Complaint>> getComplaints();

    @GET("customers")
    Observable<List<Customer>> getCustomers();

    @GET("payments")
    Observable<List<Payment>> getPayments();

    @FormUrlEncoded
    @POST("add_user")
    Observable<String> addNewCustomer(@Field("c_id") String customerId, @Field("c_name")String customerName, @Field("phone1")String customerPhone1,@Field("phone2") String customerPhone2, @Field("c_address")String customerAddress);

    @FormUrlEncoded
    @POST("add_worker")
    Observable<String> addNewWorker(@Field("w_name") String name, @Field("w_phone1")String phone1,@Field("w_phone2")String phone2);

    @GET("groups")
    Observable<List<Group>> getGroups();

    @FormUrlEncoded
    @POST("add_group")
    Observable<String> addNewGroup(@Field("group_name")String grpName);


    @FormUrlEncoded
    @POST("delete_group")
    Observable<String> deleteGroupById(@Field("group_id") String id);

    @FormUrlEncoded
    @POST("delete_worker")
    Observable<String> deleteWorkerById(@Field("worker_id") String id);


    @FormUrlEncoded
    @POST("update_customer")
    Observable<String> updateCustomerDetail(@Field("ct_id") String id, @Field("c_name") String name, @Field("address") String address,@Field("phone1")String phone1,  @Field("phone2") String phone2);

    @GET("workers")
    Observable<List<Worker>>  getWorkers();

    @FormUrlEncoded
    @POST("insert_combination")
    Observable<String> addNewGroupCombination(@Field("group_id") String group, @Field("customer_id") String customer);

    @GET("get_combination")
    Observable<List<GroupCombination>> getGroupCombination();

    @FormUrlEncoded
     @POST("delete_combination")
    Observable<String> deleteGroupCombinationById(@Field("group_combination_id") String id);


    @FormUrlEncoded
    @POST("get_customer_phone_by_group_id")
    Observable<List<String>> fetchCustomerPhoneByGroupId(@Field("group_id")String groupId);

    @GET("sms_template_list")
    Observable<List<Template>> getSmsTemplate();

    @GET("sms_templates")
    Observable<List<String>> getSmsTemplates();

    @GET("complaint_templates")
    Observable<List<Template>> getComplaintTemplates();

    @FormUrlEncoded
    @POST("insert_sms_template")
    Observable<String>  insertSmsTemplate(@Field("sms_template")String smsTemplate);

    @FormUrlEncoded
    @POST("insert_complaint_template")
    Observable<String>  insertComplaintTemplate(@Field("complaint_template")String smsTemplate);


    @FormUrlEncoded
    @POST("delete_sms_template")
    Observable<String> deleteSmsTemplate(@Field("id")String id);

    @FormUrlEncoded
    @POST("delete_complaint_template")
    Observable<String> deleteComplaintTemplate(@Field("id")String id);



}
