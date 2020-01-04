package com.airbyte.customer.payment;

import com.airbyte.BaseView;

interface PaymentFragmentView extends BaseView {
    void onSendMessageFailed();

    void onSendMessageSuccess(String str);

    void onPaymentSuccess(Payment payment);
    void onPaymentFailed();

    void onLastPaymentInfoFetchSuccess(Payment payment);
    void onLastPaymentInfoFetchFailed();

}
