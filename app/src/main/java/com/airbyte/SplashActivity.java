package com.airbyte;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.airbyte.R;
import com.airbyte.admin.HomeActivity;
import com.airbyte.utils.Session;
import com.airbyte.verification.NumberVerifyActivity;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    Activity activity = this;
    Session session;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(() -> {

            session = new Session(activity);

            //Check ifs user session or admin session
            if (session.getAdminId() != null){
                Log.d(TAG, "onCreate: Launching admin");
                launchActivity(HomeActivity.class);
                return;
              }


            if (session.getCustomerId() != null) {
                Log.d(TAG, "onCreate: Launching customer");
                launchActivity(com.airbyte.customer.HomeActivity.class);
                return;
              }

            Log.d(TAG, "onCreate: launching number verfiy");
            launchActivity(NumberVerifyActivity.class);


        },1200);
    }

    public void launchActivity(Class classs) {
        Log.d(TAG, "launchActivity:  ");
            startActivity(new Intent(activity,classs));
            finish();
    }
}
