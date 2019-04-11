package com.airbyte.admin;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Payment  implements Parcelable {


    @SerializedName("id")
    private String paymentId;

    @SerializedName("AmountPay")
    private String paymentAmount;

    @SerializedName("CustomerId")
    private String customerId;

    @SerializedName("CsPhone1")
    private String phone1;

    @SerializedName("AmountDate")
    private String paymentDate;

    @SerializedName("TransactionId")
    private String txnId;


    protected Payment(Parcel in) {
        paymentId = in.readString();
        paymentAmount = in.readString();
        customerId = in.readString();
        phone1 = in.readString();
        paymentDate = in.readString();
        txnId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(paymentId);
        dest.writeString(paymentAmount);
        dest.writeString(customerId);
        dest.writeString(phone1);
        dest.writeString(paymentDate);
        dest.writeString(txnId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel in) {
            return new Payment(in);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };

    public String getPaymentId() {
        return paymentId;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getTxnId() {
        return txnId;
    }
}
