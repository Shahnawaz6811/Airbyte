package com.airbyte.verification;

import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("ct_id")
    private String customerTableId;

    @SerializedName("c_id")
    private String customerId;

    @SerializedName("c_name")
    private String customerName;

    @SerializedName("message")
    private String message;

    @SerializedName("admin_id")
    private String adminId;

    @SerializedName("admin_phone")
    private String adminPhone;

    @SerializedName("worker_phone")
    private String workerPhone;



    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getMessage() {
        return message;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public String getWorkerPhone() {
        return this.workerPhone;
    }


}
