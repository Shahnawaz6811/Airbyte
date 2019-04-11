package com.airbyte.admin;

import com.airbyte.BaseView;

public interface CustomerUpdateView extends BaseView {

    void onCustomerUpdateSuccess(String message);
    void onCustomerUpdateFailed();

}
