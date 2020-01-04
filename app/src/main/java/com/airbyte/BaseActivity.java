package com.airbyte;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.airbyte.SplashActivity;
import com.airbyte.dagger.AppModule;
import com.airbyte.dagger.DaggerAppComponent;
import com.airbyte.dagger.NetModule;
import com.airbyte.utils.AppUtils;
import com.airbyte.utils.Session;
import com.airbyte.verification.NumberVerifyActivity;
import com.airbyte.verification.OtpVerifyActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public abstract class  BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Session session = new Session(this);

        if (this instanceof NumberVerifyActivity || this instanceof OtpVerifyActivity) {
            Log.d(TAG, "onCreate: 25");
        }
        else{
            if (!session.isSessionActive()) {
                Log.d(TAG, "onCreate: 29");
                startActivity(new Intent(this, SplashActivity.class));
                finish();
            }
            else{
                Log.d(TAG, "onCreate: Session active");
            }
        }


        setContentView(getLayoutRes());
        initView();
    }

    protected abstract void initView();

    public DaggerAppComponent.Builder getDaggerBuilder(String baseUrl, BaseView baseView){
       return DaggerAppComponent.builder().appModule(new AppModule(this,baseView)).netModule(new NetModule(baseUrl));
    }

    protected abstract int getLayoutRes();

    public  void showNetworkErrorDialog() {
        Log.d(TAG, "showNetworkErrorDialog: ");
        AlertDialog  alertDialog = new AlertDialog.Builder(this)
                                .setTitle((CharSequence) "Info")
                                .setIcon((int) R.drawable.ic_error_outline_black_24dp)
                                .setMessage(R.string.no_internet_error)
                                .setPositiveButton((CharSequence) "Settings", (dialog, which) -> startActivity(new Intent("android.settings.SETTINGS")))
                                .setNegativeButton((CharSequence) "Close", (dialog, which) -> {
                                    dialog.dismiss();
                                }).create();

        alertDialog.show();
     }

    public   boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() && activeNetworkInfo.isAvailable();
     }
}
