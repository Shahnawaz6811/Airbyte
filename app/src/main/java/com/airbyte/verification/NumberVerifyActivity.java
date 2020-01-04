package com.airbyte.verification;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.airbyte.utils.MessageApiService;
import com.airbyte.R;
import com.airbyte.utils.AppUtils;
import com.airbyte.BaseActivity;
import com.airbyte.utils.NetworkUtil;
import com.airbyte.utils.Session;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javax.inject.Inject;
import retrofit2.Retrofit;

public class NumberVerifyActivity extends BaseActivity implements NumberVerifyView {

    private static final String TAG = "NumberVerificationActiv";

    @BindView(R.id.btn_get_verification_code)
    Button getOtpBtn;
    @BindView(R.id.progress_bar)
    TashieLoader getOtpProgress;
    @BindView(R.id.et_phone_num)
    EditText phoneTxt;
    @Inject
    Session session;
    @Inject
    NumberVerifyPresenter presenter;
    String phone;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerBuilder(MessageApiService.api91BaseUrl,this).build().inject(this);

        Log.d(TAG, "onCreate: "+presenter);

    }



    @Override
    protected void initView() {
        Log.d(TAG, "initView: ");
        ButterKnife.bind( this);

        getOtpBtn.setAlpha(0.5f);
        getOtpBtn.setClickable(false);

        phoneTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 10) {
                    Log.d(NumberVerifyActivity.TAG, "onTextChanged: 48");
                    getOtpBtn.setClickable(true);
                    getOtpBtn.setAlpha(0.9f);
                }
                if (s.length() < 10) {
                    getOtpBtn.setClickable(false);
                    getOtpBtn.setAlpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected int getLayoutRes() {
        Log.d(TAG, "getLayoutRes: ");
        return R.layout.activity_number_verifiy;
    }


    @OnClick({R.id.btn_get_verification_code})
    public void onClick(View view) {

        if (!isNetworkAvailable()) {
            showNetworkErrorDialog();
            return;
        }

        Log.d(TAG, "onClick: ");
        phone = phoneTxt.getText().toString();
        getOtpProgress.setVisibility(View.VISIBLE);
        getOtpBtn.setVisibility(View.GONE);
        presenter.sendOtp(phone);



    }
    
    public void onOTPSentSuccess(String otp) {
        Log.d(TAG, "onOTPSentSuccess: ");

        getOtpBtn.setVisibility(View.VISIBLE);
        getOtpProgress.setVisibility(View.GONE);

        Intent intent = new Intent(this, OtpVerifyActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("otp", otp);
        startActivity(intent);
     }


    public void onOTPSentFailed() {
        getOtpBtn.setVisibility(View.VISIBLE);
        getOtpProgress.setVisibility(View.GONE);
        Toast.makeText(this, "Unexpected error occured,Please check your internet connection", Toast.LENGTH_SHORT).show();
    }


}
