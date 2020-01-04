package com.airbyte.admin;

import com.google.gson.annotations.SerializedName;

public class Group {

    @SerializedName("group_id")
    String groupId;

    @SerializedName("group_name")
    String groupName;

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }


}
