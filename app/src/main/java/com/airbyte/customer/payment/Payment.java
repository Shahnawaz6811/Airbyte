package com.airbyte.customer.payment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Payment implements Serializable {

    String payeeName;

    @SerializedName("payment_id")
    private String paymentId;

    @SerializedName("pay_amount")
    private String paymentAmount;

    @SerializedName("txn_id")
    private String txnId;

    @SerializedName("payment_date")
    private String paymentDate;

    @SerializedName("merchant_id")
    private String merchantId;

    @SerializedName("txn_status")
    private String txnStatus;

    @SerializedName("order_id")
    private String order_id;

    public String getMerchantId() {
        return merchantId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public String getOrderId(){
        return order_id;
    }
}
