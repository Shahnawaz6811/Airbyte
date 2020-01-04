package com.airbyte.verification;

import android.content.Intent;
import android.os.Bundle;
 import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.utils.MessageApiService;
import com.airbyte.R;
import com.airbyte.admin.HomeActivity;
import com.airbyte.utils.AppUtils;
import com.airbyte.BaseActivity;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.utils.Session;
import com.chaos.view.PinView;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import javax.inject.Inject;

public class OtpVerifyActivity extends BaseActivity implements VerifyOtpActivityView {
    private static final String TAG = "OtpVerifyActivity";

    String otp;
    String phone;
    @BindView(R.id.tv_phone_number)
    TextView phoneNumberTxt;
    @BindView(R.id.firstPinView)
    PinView pinView;
    @BindView(R.id.tv_resend_code)
    TextView resendCode;
    @BindView(R.id.btn_verify_otp)
    Button verifyNumberBtn;
    @Inject
    VerifyOtpActivityPresenter presenter;
    @Inject
    Session session;
    @Inject
    Retrofit retrofit;
    @BindView(R.id.progress_bar)
    TashieLoader verifyOtpProgress;
    @BindView(R.id.pb_resend_otp)
    TashieLoader progressResendOtp;
    SmsVerifyCatcher smsVerifyCatcher;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);

        getDaggerBuilder(MessageApiService.api91BaseUrl,this).build().inject(this);

        smsVerifyCatcher = new SmsVerifyCatcher(this, message -> {
            Log.d(TAG, "onSmsCatch: "+message);
            String number  = message.replaceAll("[^0-9]", "");
            pinView.setText("");
            pinView.setText(number);

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();

    }


    @Override
    protected void initView() {
        Log.d(TAG, "initView: ");
        ButterKnife.bind(this);
        verifyNumberBtn.setAlpha(0.5f);
        verifyNumberBtn.setClickable(false);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        otp = intent.getStringExtra("otp");
        phoneNumberTxt.setText(phone);

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    verifyNumberBtn.setClickable(true);
                    verifyNumberBtn.setAlpha(0.9f);
                }
                if (s.length() < 6) {
                    verifyNumberBtn.setAlpha(0.5f);
                    verifyNumberBtn.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_otp_verify;
    }




    @OnClick({R.id.btn_verify_otp, R.id.tv_change_number,R.id.tv_resend_code})
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");


        switch (view.getId()) {

            case R.id.btn_verify_otp:
                Log.d(TAG, "onClick: 115");
                verifyNumber();
                break;

            case R.id.tv_change_number:
                Intent intent = new Intent(this, NumberVerifyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;


            case R.id.tv_resend_code:
                Log.d(TAG, "onClick: 115");
                if (!isNetworkAvailable()){
                    showNetworkErrorDialog();
                    return;
                }

                progressResendOtp.setVisibility(View.VISIBLE);
                resendCode.setVisibility(View.GONE);
                presenter.sendOtp(phone);
                 break;
        }

    }


    private void verifyNumber() {

        Log.d(TAG, "verifyNumber: ");
        if (!isNetworkAvailable()) {
            showNetworkErrorDialog();
            return;
        }

        String otpCode = pinView.getText().toString();
        if (!otp.equals(otpCode)) {
            pinView.setError("Invalid otp");
            return;
        }

        verifyNumberBtn.setVisibility(View.GONE);
        verifyOtpProgress.setVisibility(View.VISIBLE);
        presenter.verifyPhone(phone);

    }

    public void onUserVerifiedSuccess(Response response) {
        Log.d(TAG, "onUserVerifiedSuccess: ");

        String customerId;
        String adminId;
        //Check if response is for admin / customer
        if (response.getMessage().equals("success")) {

            customerId = response.getCustomerId();
            adminId    = response.getAdminId();

            if (customerId != null) {
                launchCustomerActivity(response);
            }
            else if(adminId != null) {
                launchAdminActivity(adminId);
            }

        }


    }

    private void launchCustomerActivity(Response response) {

        String customerId  = response.getCustomerId();
        String customerName = response.getCustomerName();
        Log.d(TAG, "launchCustomerActivity: "+ response.getWorkerPhone());
        session.createUserSession(phone,customerId,customerName);

        session.setWorkerPhone(response.getWorkerPhone());
        session.setAdminPhone(response.getAdminPhone());
        Intent intent = new Intent(this, com.airbyte.customer.HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private void launchAdminActivity(String adminId) {
        Log.d(TAG, "launchAdminActivity: ");
        session.createAdminSession(phone,adminId,"Admin");
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    public void onUserVerifiedFailed() {
        Toast.makeText(this, "Unexpected error occured.Cross check your connection", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onUserVerifiedFailed: ");
        verifyOtpProgress.setVisibility(View.GONE);
        verifyNumberBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onErrorOccured() {
        Toast.makeText(this, "Unexpected error occured,Please check your internet connection", Toast.LENGTH_SHORT).show();
        progressResendOtp.setVisibility(View.GONE);
        resendCode.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOTPSentFailed() {
        Toast.makeText(this, "Unexpected error occured,Please check your internet connection", Toast.LENGTH_SHORT).show();
        progressResendOtp.setVisibility(View.GONE);
        resendCode.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOTPSentSuccess(String str) {
        Toast.makeText(this, "Otp sent successfully", Toast.LENGTH_SHORT).show();
        progressResendOtp.setVisibility(View.GONE);
        resendCode.setVisibility(View.VISIBLE);
         this.otp = str;
    }


}
