package com.airbyte.verification;

import android.util.Log;

import com.airbyte.BaseView;
import com.airbyte.utils.MessageApiService;

import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class NumberVerifyPresenter {

    private static final String TAG = "NumberVerifyPresenter";
    private String otp;
    private MessageApiService messageApiService;
    private NumberVerifyView view;


    public NumberVerifyPresenter(BaseView view, Retrofit retrofit) {
        Log.d(TAG, "NumberVerifyPresenter: ");
        this.view = ((NumberVerifyView) view);
        messageApiService = retrofit.create(MessageApiService.class);
     }



     void sendOtp(String phone) {
        Log.d(TAG, "sendOtp: ");

          otp = String.valueOf(new Random().nextInt(900000) + 100000);
         Disposable subscribe = messageApiService.sendOtp(MessageApiService.MSG91_AUTH_KEY, phone, otp)
                               .observeOn(AndroidSchedulers.mainThread())
                               .subscribeOn(Schedulers.io()).subscribe(this::handleOtpResposne, this::handleOtpError);
    }


    private void handleOtpResposne(OtpResponse otpResponse) {

        if (otpResponse.getType().equals("success")) {
            Log.d(TAG, "handleResults: Success");
            view.onOTPSentSuccess(otp);
        } else {
            Log.d(TAG, "handleResults: Failed");
             view.onOTPSentFailed();
        }
    }


    private void handleOtpError(Throwable throwable) {
        Log.d(TAG, "handleError: "+ throwable.toString());
         view.onOTPSentFailed();

    }


}
