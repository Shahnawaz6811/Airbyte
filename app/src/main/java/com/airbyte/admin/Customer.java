package com.airbyte.admin;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

class Customer implements Parcelable {
    private static final String TAG = "Customer";
    @SerializedName("CustomerId")
    private String customerId;

    @SerializedName("Id")
    private String ctId;

    @SerializedName("CustomerName")
    private String customerName;

    @SerializedName("c_address")
    String customerAddress;

    @SerializedName("CsPhone2")
    private String customerPhone2;


    @SerializedName("CsPhone1")
    private String customerPhone1;

    protected Customer(Parcel in) {
        customerId = in.readString();
        ctId = in.readString();
        customerName = in.readString();
        customerAddress = in.readString();
        customerPhone2 = in.readString();
        customerPhone1 = in.readString();
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    public String getCustomerId() {
        Log.d(TAG, "getCustomerId: ");
        return customerId;
    }


    public String getCtId() {
        return ctId;
    }

    public String getCustomerName() {
        return customerName;
    }



    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getCustomerPhone2() {
        return customerPhone2;
    }

    public String getCustomerPhone1() {
        return customerPhone1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerId);
        dest.writeString(ctId);
        dest.writeString(customerName);
        dest.writeString(customerAddress);
        dest.writeString(customerPhone2);
        dest.writeString(customerPhone1);
    }
}
