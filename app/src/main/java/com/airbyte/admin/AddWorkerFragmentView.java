package com.airbyte.admin;

import com.airbyte.BaseView;

import java.util.List;

public interface AddWorkerFragmentView extends BaseView {

    void onWorkerAddSuccess();
    void onWorkerAddFailed(String message);

    void onWorkerFetchSuccess(List<Worker> workers);

    void onWorkerFetchFailed();

    void onWorkerDeleteSuccess();
    void onWorkerDeleteFailed();



}
