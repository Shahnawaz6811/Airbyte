package com.airbyte.verification;

import com.airbyte.BaseView;

public interface VerifyOtpActivityView extends BaseView {
    void onUserVerifiedFailed();

    void onUserVerifiedSuccess(Response response);

    void onErrorOccured();


    void onOTPSentFailed();

    void onOTPSentSuccess(String str);
}
