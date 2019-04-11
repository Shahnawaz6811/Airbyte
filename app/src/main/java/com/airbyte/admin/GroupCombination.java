package com.airbyte.admin;

import com.google.gson.annotations.SerializedName;

class GroupCombination {

    @SerializedName("group_combination_id")
    private String groupCombinationId;


    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("group_name")
    private String groupName;

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupCombinationId() {
        return groupCombinationId;
    }
}
