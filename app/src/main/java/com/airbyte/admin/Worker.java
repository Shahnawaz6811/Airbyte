package com.airbyte.admin;

import com.google.gson.annotations.SerializedName;

public class Worker {

    @SerializedName("WorkerId")
    private String id;

    @SerializedName("WorkerName")
    private String workerName;

    @SerializedName("WorkerPhone1")
    private String workerPhone1;

    @SerializedName("WorkerPhone2")
    private String getWorkerPhone2;

    public String getId() {
        return id;
    }

    public String getWorkerName() {
        return workerName;
    }

    public String getWorkerPhone1() {
        return workerPhone1;
    }

    public String getGetWorkerPhone2() {
        return getWorkerPhone2;
    }
}
