package com.airbyte.customer.complaints;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Complaint implements Parcelable {

    @SerializedName("ComplaintId")
    private
    String complaintId;

    @SerializedName("CustomerId")
    private String customerId;

    @SerializedName("ComplaintInfo")
    private String complaintInfo;

    @SerializedName("CsPhone1")
    private String customerPhone;

    @SerializedName("Date")
    private String time;


    protected Complaint(Parcel in) {
        complaintId = in.readString();
        customerId = in.readString();
        complaintInfo = in.readString();
        customerPhone = in.readString();
        time = in.readString();
    }

    public static final Creator<Complaint> CREATOR = new Creator<Complaint>() {
        @Override
        public Complaint createFromParcel(Parcel in) {
            return new Complaint(in);
        }

        @Override
        public Complaint[] newArray(int size) {
            return new Complaint[size];
        }
    };

    public String getCustomerId() {
        return customerId;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }


    public String getComplaintInfo() {
        return complaintInfo;
    }



    public String getComplaintTime() {
       return time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(complaintId);
        dest.writeString(customerId);
        dest.writeString(complaintInfo);
        dest.writeString(customerPhone);
        dest.writeString(time);
    }
}
