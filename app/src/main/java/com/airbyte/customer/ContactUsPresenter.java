package com.airbyte.customer;

import android.util.Log;

import com.airbyte.admin.SendMessageCustomerView;
import com.airbyte.utils.MessageApiService;
import com.airbyte.verification.Api;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ContactUsPresenter {

    private static final String TAG = "ContactUsPresenter";

    private SendMessageCustomerView sendMessageCustomerView;
    private Retrofit retrofit;
    private MessageApiService messageApiService;
    private CustomerApiService customerApiService;

    public ContactUsPresenter(SendMessageCustomerView view, Retrofit retrofit) {
        this.sendMessageCustomerView = view;
        this.retrofit = retrofit;
        messageApiService = retrofit.create(MessageApiService.class);
        customerApiService = retrofit.newBuilder().baseUrl(CustomerApiService.BASE_URL).build().create(CustomerApiService.class);
    }

    //Send message to User
    public void sendMessageToUser(String phone, String complaint) {
        Log.d(TAG, "sendMessageToUser: "+phone);
        String query = "We have received your query "+ complaint + " Thanks for contacting us. Looking forward to server your better";
        Disposable subscribe = messageApiService.sendMessage(MessageApiService.MSG91_AUTH_KEY, phone, query, "klwn", "airbyt")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(this::handleSendMessageUserSuccess, this::handleSendMessageUserFailed);
    }

    private void handleSendMessageUserFailed(Throwable throwable) {
        Log.d(TAG, "handleSendMessageUserFailed: "+ throwable.toString());
//        sendMessageCustomerView.onSendMessageError();
    }

    private void handleSendMessageUserSuccess(String string) {
        Log.d(TAG, "handleSendMessageUserSuccess: "+ string);
//        sendMessageCustomerView.onSendMessageSuccess();
    }

    //Send message to Admin
    void sendMessageToAdmin(String adminPhone, String complaint, String userPhone, String userId) {
        Api api = (Api) this.retrofit.newBuilder().baseUrl(Api.baseUrl).build().create(Api.class);
        String str = "You have a new message  complaint +  from user" + userId + "   "+  userPhone + "\n" + complaint ;
        Log.d(TAG, "sendMessageToAdmin: "+adminPhone);
        Disposable subscribe = messageApiService.sendMessage(Api.authKey, adminPhone, str, "klwn", "airbyt")
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(this::handleSendMessageAdminSuccess, this::handleSendMessageAdminFailed);
    }


    //Send message to Admin
    void sendMessageToWorker(String worker, String complaint, String userPhone, String userId) {
        Api api = (Api) this.retrofit.newBuilder().baseUrl(Api.baseUrl).build().create(Api.class);
        String str = "You have a new message  complaint +  from user" + userId + "   "+  userPhone + "\n" + complaint ;
        Log.d(TAG, "sendMessageToWorker: "+worker);
        Disposable subscribe = messageApiService.sendMessage(Api.authKey, worker, str, "klwn", "airbyt")
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(this::handleSendMessageWorkerSuccess, this::handleSendMessageWorkerFailed);
    }

    private void handleSendMessageWorkerSuccess(String string) {
        Log.d(TAG, "handleSendMessageWorkerSuccess: "+ string);
        sendMessageCustomerView.onSendMessageSuccess();
    }

    private void handleSendMessageWorkerFailed(Throwable throwable) {
        Log.d(TAG, "handleSendMessageWorkerFailed: "+ throwable.toString());
        sendMessageCustomerView.onSendMessageError();
    }


    private void handleSendMessageAdminSuccess(String string) {
        Log.d(TAG, "handleSendMessageAdminSuccess: "+ string);
        sendMessageCustomerView.onSendMessageSuccess();
    }

    private void handleSendMessageAdminFailed(Throwable throwable) {
        Log.d(TAG, "handleSendMessageAdminFailed: "+ throwable.toString());
        sendMessageCustomerView.onSendMessageError();
    }


    void insertContactQuery(String customerId,String query) {
        Log.d(TAG, "insertContactQuery: ");
         Disposable subscribe = customerApiService.insertCustomerContactQuery(customerId,query)
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(this::handleinsertContactQuerySuccess, this::handleinsertContactQueryFailed);
    }


    private void handleinsertContactQuerySuccess(String string) {
        Log.d(TAG, "handleinsertContactQuerySuccess: "+ string);
     }

    private void handleinsertContactQueryFailed(Throwable throwable) {
        Log.d(TAG, "handleinsertContactQueryFailed: "+ throwable.toString());
     }



}
