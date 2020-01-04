package com.airbyte.admin;

import com.google.gson.annotations.SerializedName;

public class Template {

    @SerializedName("id")
    String id;

    @SerializedName("template")
    private String template;

    public String getId() {
        return id;
    }

    public String getTemplate() {
        return template;
    }


}

