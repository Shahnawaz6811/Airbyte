package com.airbyte.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.Map;

public class Session {

    private static final String ID = "id";
    private static String CUSTOMER_PHONE = "customer_phone";
    private static String CUSTOMER_NAME = "customer_name";
    private static String CUSTOMER_ID = "customer_id";

    private static final String ADMIN_PHONE = "admin_phone";
    private static final String ADMIN_ID = "admin_id";
    private static final String ADMIN_NAME = "admin_name";

    private static final String WORKER_PHONE = "worker_phone";



    public static final String PREF_NAME = "app_pref";
    private static final String TAG = "Session";
    private Context context;
    Editor editor   ;
    private SharedPreferences preferences;

    public Session(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREF_NAME, 0);
        editor = preferences.edit();
    }

    public void createUserSession(String phone, String id,String name) {
        Log.d(TAG, "initSession: ");
         editor.putString(CUSTOMER_PHONE, phone);
         editor.putString(CUSTOMER_ID, id);
         editor.putString(CUSTOMER_NAME, name);
         editor.apply();
    }

    public void createAdminSession(String phone, String id,String name) {
        Log.d(TAG, "initSession: ");
        editor.putString(ADMIN_PHONE, phone);
        editor.putString(ADMIN_ID, id);
        editor.putString(ADMIN_NAME, name);
        editor.apply();
    }

   public boolean isSessionActive(){
        Map<String, ?> all = preferences.getAll();
        
        if (all.size() == 0){
            Log.d(TAG, "isSessionActive: 40");
            return false;
        }
        Log.d(TAG, "isSessionActive: 43");
        return true;
    }

    public void destroySession() {
        editor.clear();
        editor.commit();
     }

    public String getCustomerPhone() {
        Log.d(TAG, "getPhone: ");
        return this.preferences.getString(CUSTOMER_PHONE, null);
    }

    public String getCustomerName() {
        Log.d(TAG, "getPhone: ");
        return this.preferences.getString(CUSTOMER_NAME, null);
    }



    public String getCustomerId() {
        return this.preferences.getString(CUSTOMER_ID, null);
    }


    public void setAdminPhone(String adminPhone) {
        this.editor.putString(ADMIN_PHONE, adminPhone);
        this.editor.apply();
    }

    public String getAdminPhone() {
        Log.d(TAG, "getAdminPhone: ");
        return this.preferences.getString(ADMIN_PHONE, null);
    }

    public String getAdminId(){
        return preferences.getString(ADMIN_ID,null);

    }

    public String getAdminName(){
        return preferences.getString(ADMIN_NAME,null);
    }

    public void setWorkerPhone(String workerPhone) {
        this.editor.putString(WORKER_PHONE, workerPhone);
        this.editor.apply();
    }

    public String getWorkerPhone() {
        Log.d(TAG, "getAdminPhone: ");
        return this.preferences.getString(WORKER_PHONE, null);
    }


    public void saveMerchantId(String merchantId) {
        editor.putString("merchant_id",merchantId);
        editor.apply();
    }

    public String getMerchantId(){
        return preferences.getString("merchant_id",null);
    }
}
