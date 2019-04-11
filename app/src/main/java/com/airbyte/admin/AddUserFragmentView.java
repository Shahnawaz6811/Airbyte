package com.airbyte.admin;

import com.airbyte.BaseView;

public interface AddUserFragmentView extends BaseView {

    void onCustomerAddSuccess();
    void onCustomerAddFailed(String message);
}
