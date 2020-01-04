package com.airbyte.admin;

import android.util.Log;

import com.airbyte.customer.complaints.Complaint;
import com.airbyte.utils.MessageApiService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SendMessagePresenter {

    private static final String TAG = "SendMessagePresenter";

    SendMessageView view;
    private AdminApiService apiService;
    MessageApiService messageApiService;
    SendMessageCustomerView sendMessageCustomerView;
    SmsTemplateFetchListener smsTemplateFetchListener;


    public SendMessagePresenter(SendMessageView view, Retrofit retrofit) {
        this.view = view;
        this.apiService = retrofit.create(AdminApiService.class);
        messageApiService = retrofit.newBuilder().baseUrl(MessageApiService.api91BaseUrl).build().create(MessageApiService.class);

    }

    public SendMessagePresenter(SendMessageCustomerView customerView, Retrofit retrofit) {
        this.sendMessageCustomerView = customerView;
        messageApiService = retrofit.newBuilder().baseUrl(MessageApiService.api91BaseUrl).build().create(MessageApiService.class);


    }

    void fetchSmsTemplates(SmsTemplateFetchListener listener) {
        Log.d(TAG, "fetchSmsTemplates: ");
        this.smsTemplateFetchListener = listener;
        Disposable subscribe = apiService.getSmsTemplates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleFetchSmsTemplateSuccess, this::handleFetchSmsTemplateError);
    }

    private void handleFetchSmsTemplateSuccess(List<String> smsTemplates) {
        Log.d(TAG, "handleFetchSmsTemplateSuccess: " + smsTemplates);
        smsTemplateFetchListener.onSmsTemplateFetchSuccess(smsTemplates);

    }

    private void handleFetchSmsTemplateError(Throwable throwable) {
        Log.d(TAG, "handleFetchSmsTemplateError: "+ throwable.toString());
        smsTemplateFetchListener.onSmsTemplateFetchError();
    }


    public void sendMessageToCustomer(String phone, String message) {
        Log.d(TAG, "sendMessageToCustomer: "+ phone + message);
        Observable<String> phoneObservable = messageApiService.sendMessage(MessageApiService.MSG91_AUTH_KEY, phone, message, "jsja", "airbyt");
        Observable<String> observ = phoneObservable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
        Disposable disposable = observ.subscribe(this::handleSendMessageCustomerSuccess, this::handleSendMessageCustomerError);
    }

    private void handleSendMessageCustomerSuccess(String s) {
        Log.d(TAG, "handleSendMessageCustomerSuccess: " + s);
        sendMessageCustomerView.onSendMessageSuccess();

    }

    private void handleSendMessageCustomerError(Throwable throwable) {
        Log.d(TAG, "handleSendMessageCustomerError: "+ throwable.toString());
        sendMessageCustomerView.onSendMessageError();
    }


    void sendMessage(List<String> phones, String message){


//        for (int i = 0; i < phones.size(); i++) {
//             if (i <  phones.size() - 1) {
//                 stringBuilder.append(phones.get(i)+",");
//             }
//             else{
//                 stringBuilder.append(phones.get(i));
//             }
//         }
//
//        Log.d(TAG, "sendMessage: strs"+ stringBuilder.toString());

         for (String phone : phones) {
             Log.d(TAG, "Sending message : "+ message+ " to: "+phone);
             Observable<String> phoneObservable = messageApiService.sendMessage(MessageApiService.MSG91_AUTH_KEY, phone, message, "jsja", "airbyt");
             Observable<String> observ = phoneObservable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
             Disposable disposable = observ.subscribe(this::handleSendMessageSuccess, this::handleSendMessageError);
         }


    }

    private void handleSendMessageSuccess(String s) {
        Log.d(TAG, "handleSendMessageSuccess: " + s);
        view.onSendMessageSuccess();

    }

    private void handleSendMessageError(Throwable throwable) {
        Log.d(TAG, "handleSendMessageError: "+ throwable.toString());
        view.onSendMessageError();
    }

    void fetchGroups(){
        Log.d(TAG, "fetchGroups: ");
        Disposable subscribe = apiService.getGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleGroupFetchSuccess, this::handleGroupFetchError);

    }

    private void handleGroupFetchSuccess(List<Group> groups) {
        Log.d(TAG, "handleGroupFetchSuccess: " + groups);
        view.onGroupFetchSuccess(groups);

    }

    private void handleGroupFetchError(Throwable throwable) {
        Log.d(TAG, "handleGroupFetchError: "+ throwable.toString());
        view.onGroupFetchError();
    }

    void fetchCustomersPhoneByGroupId(String groupId){
        Log.d(TAG, "fetchCustomersPhoneByGroupId: ");
        Disposable subscribe = apiService.fetchCustomerPhoneByGroupId(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleCustomerPhoneFetchSuccess, this::handleCustomerPhoneFetchError);
    }

    private void handleCustomerPhoneFetchSuccess(List<String> phone) {
        Log.d(TAG, "handleCustomerPhoneFetchSuccess: " + phone);
        view.onCustomersPhoneFetchSuccesss(phone);

    }

    private void handleCustomerPhoneFetchError(Throwable throwable) {
        Log.d(TAG, "handleCustomerPhoneFetchError: "+ throwable.toString());
        view.onCustomersPhoneFetchError();
    }
}
