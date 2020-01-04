package com.airbyte.verification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbyte.BaseView;
import com.airbyte.R;
import com.airbyte.admin.NetworkErrorDialogCallback;
import com.airbyte.dagger.AppModule;
import com.airbyte.dagger.DaggerAppComponent;
import com.airbyte.dagger.NetModule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(),container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    protected abstract int getLayoutRes();
    abstract public void initView();

     public DaggerAppComponent.Builder getDaggerBuilder(String baseUrl, BaseView baseView){
        return DaggerAppComponent.builder().appModule(new AppModule(getActivity(),baseView)).netModule(new NetModule(baseUrl));
    }

    public void showConnectionErrorPopup(Context context, NetworkErrorDialogCallback callback){
        Log.d(TAG, "showConnectionErrorPopup: "+ context);
        if (context == null) {
            return;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context,android.R.style.Theme_DeviceDefault_Light_Dialog)
                .setTitle("Cannot connect to Airbyte").setMessage("Make sure your device is connected to wifi or mobile network and try again")

                .setIcon(R.drawable.ic_error_outline_black_24dp).setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onRetry();
                    }
                }).setNegativeButton("Cancel", (dialog, which) -> callback.onCancel()).create();

        alertDialog.show();


    }

    @Override
    public void onResume() {
        super.onResume();
        registerNetworkReceiver();

    }

    public void registerNetworkReceiver() {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: ");
                if (checkInternet()) {
                    Log.d(TAG, "onReceive: Available");
                    onNetworkActive();
                }
            }
        };

        if (getActivity() != null){
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            getActivity().registerReceiver(receiver,intentFilter);
        }


    }

    protected abstract void onNetworkActive();

    private boolean checkInternet() {
         if (getActivity() != null) {
             ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
             NetworkInfo netInfo = cm.getActiveNetworkInfo();
             //should check null because in airplane mode it will be null
             return (netInfo != null && netInfo.isConnected());
         }
        return false;
    }

}
