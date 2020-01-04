package com.airbyte.admin;


import com.airbyte.BaseView;

import java.util.List;

public interface PaymentFragmentListView extends BaseView {

    void onPaymentListFetchSuccess(List<Payment> paymentList);
    void onPaymentListFetchFailed();

 }
