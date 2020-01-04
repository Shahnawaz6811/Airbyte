package com.airbyte.verification;

import android.util.Log;

import com.airbyte.BaseView;
import com.airbyte.utils.MessageApiService;
import com.airbyte.customer.CustomerApiService;

import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class VerifyOtpActivityPresenter {
    private static final String TAG = "VerifyNumberActivityPre";
     private Retrofit retrofit;
    private String otp;
    private VerifyOtpActivityView view;

    public VerifyOtpActivityPresenter(BaseView view, Retrofit retrofit) {
        this.view = ((VerifyOtpActivityView) view);
        this.retrofit = retrofit;
    }


      //User verification
      void verifyPhone(String number) {

         CustomerApiService customerApiService = retrofit.newBuilder().baseUrl(CustomerApiService.BASE_URL).build().create(CustomerApiService.class);
         Log.d(TAG, "verifyUser: ");
          Disposable subscribe = customerApiService.registerUser(number)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(this::handleUserVerifiedSuccess, this::handleUserVerifiedFailed);
      }


    private void handleUserVerifiedSuccess(Response response) {
        Log.d(TAG, "handleUserVerifiedSuccess: " );
        if (response != null) {
            this.view.onUserVerifiedSuccess(response);

        }
    }

    private void handleUserVerifiedFailed(Throwable throwable) {
        Log.d(TAG, "handleUserVerifiedFailed: " + throwable.toString());
        this.view.onUserVerifiedFailed();
    }




    //Otp verification
    void sendOtp(String phone) {
        Log.d(TAG, "sendOtp: ");
        MessageApiService messageApiService = retrofit.create(MessageApiService.class);
        otp = String.valueOf(new Random().nextInt(900000) + 100000);
        Disposable subscribe = messageApiService.sendOtp(MessageApiService.MSG91_AUTH_KEY, phone, this.otp)
                              .observeOn(AndroidSchedulers.mainThread())
                              .subscribeOn(Schedulers.io()).subscribe(this::handleOtpResult, this::handleOtpError);

    }

    private void handleOtpError(Throwable throwable) {
        Log.d(TAG, "handleError: " + throwable.toString());
        this.view.onOTPSentFailed();


    }

    private void handleOtpResult(OtpResponse otpResponse) {
        if (otpResponse.getType().equals("success")) {
            Log.d(TAG, "handleResults: Success");
            view.onOTPSentSuccess(this.otp);
        } else {
            Log.d(TAG, "handleResults: Failed");
            this.view.onOTPSentFailed();
        }
    }
}
