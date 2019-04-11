package com.airbyte.verification;

import com.airbyte.BaseView;

public interface NumberVerifyView extends BaseView {



    void onOTPSentFailed();

    void onOTPSentSuccess(String str);
}
