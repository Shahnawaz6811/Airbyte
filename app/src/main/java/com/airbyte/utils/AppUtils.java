package com.airbyte.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.util.Log;

import com.airbyte.R;
import com.airbyte.admin.NetworkErrorDialogCallback;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

public class AppUtils {
    private static final String TAG = "AppUtils";
     AlertDialog alertDialog ;

    /* renamed from: com.airbyte.utils.AppUtils$1 */
     class MyOnclickListener implements OnClickListener {
        MyOnclickListener() {
        }

        public void onClick(DialogInterface dialog, int which) {
        }
    }

    /* renamed from: com.airbyte.utils.AppUtils$3 */
    public class DismissListener implements OnDismissListener {
        DismissListener() {
        }

        public void onDismiss(DialogInterface dialog) {
        }
    }

    public  void showNetworkErrorDialog(final Context context) {
        Log.d(TAG, "showNetworkErrorDialog: ");
        alertDialog = new Builder(context).setTitle((CharSequence) "Info").setIcon((int) R.drawable.ic_error_outline_black_24dp).setMessage((CharSequence) "Internet not available, Cross check your internet connectivity and try again").setPositiveButton((CharSequence) "Settings", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent("android.settings.SETTINGS"));
            }
        }).setNegativeButton((CharSequence) "Close", new MyOnclickListener()).create();
        alertDialog.show();
        alertDialog.setOnDismissListener(new DismissListener());
    }

    public  AlertDialog getAlertDialog() {
        return alertDialog;
    }

    public  void dismissDialog() {
        AlertDialog alertDialog = getAlertDialog();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public void showConnectionErrorPopup(Context context, NetworkErrorDialogCallback callback){
        AlertDialog alertDialog = new AlertDialog.Builder(context,android.R.style.Theme_DeviceDefault_Light_Dialog)
                                    .setTitle("Cannot connect to Airbyte").setMessage("Make sure your device is connected to wifi or mobile network and try again")

                                    .setIcon(R.drawable.ic_error_outline_black_24dp).setPositiveButton("Retry", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onRetry();
                    }
                }).setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onCancel();
                    }
                }).create();

          alertDialog.show();
    }
}
