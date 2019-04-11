package com.airbyte.customer.complaints;

import android.util.Log;

import com.airbyte.admin.SendMessageCustomerView;
import com.airbyte.customer.CustomerApiService;
import com.airbyte.utils.MessageApiService;
import com.airbyte.verification.Api;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

import retrofit2.Retrofit;

public class ComplaintFragmentPresenter {

    private static final String TAG = "ComplaintFragmentPrese";
     private Retrofit retrofit;
    private ComplaintFragmentView view;
    private CustomerApiService customerApiService;
    private MessageApiService messageApiService;
    private SendMessageCustomerView sendMessageCustomerView;

     ComplaintFragmentPresenter(ComplaintFragmentView view, Retrofit retrofit) {
        this.view = view;
        this.retrofit = retrofit;
        messageApiService = retrofit.create(MessageApiService.class);
        customerApiService = retrofit.newBuilder().baseUrl(CustomerApiService.BASE_URL).build().create(CustomerApiService.class);
    }

    public ComplaintFragmentPresenter(SendMessageCustomerView view, Retrofit retrofit) {
        this.sendMessageCustomerView = view;
        this.retrofit = retrofit;
        messageApiService = retrofit.create(MessageApiService.class);
        customerApiService = retrofit.newBuilder().baseUrl(CustomerApiService.BASE_URL).build().create(CustomerApiService.class);
    }






    //Register new complaint
    void registerComplaint(String customerId,String complaint) {
        Log.d(TAG, "register complaint: ");
         Disposable subscribe = customerApiService.registerComplaint(customerId, complaint)
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribeOn(Schedulers.io())
                             .subscribe(this::handleRegisterComplaintSuccess, this::handleRegisterComplaintError);
    }


    private void handleRegisterComplaintSuccess(String response) {
        Log.d(TAG, "handleRegisterComplaintResponse: " + response);
        if (response.equals("success")) {
            view.onComplaintRegisterSuccess(response);
        } else {
            view.onComplaintRegisterFailed();
        }
    }

    private void handleRegisterComplaintError(Throwable throwable) {
        Log.d(TAG, "handleRegisterComplaintError: "+ throwable.toString());
         view.onComplaintRegisterFailed();
    }


    //Get complaint templates
    public void getComplaints() {
        Log.d(TAG, "getComplaints: ");
         Disposable subscribe = customerApiService.getComplaintTemplates().observeOn(AndroidSchedulers.mainThread())
                              .subscribeOn(Schedulers.io())
                              .subscribe(this::handleComplaintTemplatesSuccess, this::handleComplaintTemplatesError);
    }

    private void handleComplaintTemplatesSuccess(List<String> complaintTemplates) {
        Log.d(TAG, "handleComplaintList: ");

        if (complaintTemplates != null && !complaintTemplates.isEmpty()) {
            view.onComplaintTemplatesSuccess(complaintTemplates);
        }
    }
    private void handleComplaintTemplatesError(Throwable throwable) {
        Log.d(TAG, "handleComplaintListError:"+ throwable.toString());
        this.view.handleComplaintTemplatesError();
    }





    //Send message to User
    public void sendMessageToUser(String phone, String complaint) {
        Log.d(TAG, "sendMessageToUser: "+phone);
        String userComplaint = "Your complaint for "+ complaint + " is now registered with us";

        Disposable subscribe = messageApiService.sendMessage(MessageApiService.MSG91_AUTH_KEY, phone, userComplaint, "klwn", "airbyt")
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribeOn(Schedulers.io()).subscribe(this::handleSendMessageUserSuccess, this::handleSendMessageUserFailed);
    }

    private void handleSendMessageUserFailed(Throwable throwable) {
        Log.d(TAG, "handleSendMessageUserFailed: "+ throwable.toString());
        view.onSendMessageFailed();
    }

    private void handleSendMessageUserSuccess(String string) {
        Log.d(TAG, "handleSendMessageUserSuccess: "+ string);
        view.onSendMessageFailed();
     }


      //Send message to Admin
      void sendMessageToAdmin(String adminPhone, String complaint, String userPhone, String userId) {
        Api api = (Api) this.retrofit.newBuilder().baseUrl(Api.baseUrl).build().create(Api.class);
        String str = "You have a new complaint for " + complaint + " from user "+ userId + "   "+  userPhone ;
        Log.d(TAG, "sendMessageToAdmin: "+adminPhone);
        Disposable subscribe = messageApiService.sendMessage(Api.authKey, adminPhone, str, "klwn", "airbyt")
                                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                               .subscribe(this::handleSendMessageAdminSuccess, this::handleSendMessageAdminFailed);
      }

    private void handleSendMessageAdminSuccess(String string) {
        Log.d(TAG, "handleSendMessageAdminSuccess: "+ string);
        view.onSendMessageSuccess(string);
    }

    private void handleSendMessageAdminFailed(Throwable throwable) {
        Log.d(TAG, "handleSendMessageAdminFailed: "+ throwable.toString());
        view.onSendMessageFailed();
    }





      //Send Message to worker
     void sendMessageToWorker(String worker, String complaint, String userPhone) {
        Log.d(TAG, "sendMessageToWorker: "+worker);
         String str = "You have a new complaint for " + complaint + " from "+ userPhone;
         Disposable subscribe = messageApiService.sendMessage(Api.authKey, worker, str, "klwn", "airbyt")
                              .observeOn(AndroidSchedulers.mainThread())
                              .subscribeOn(Schedulers.io()).subscribe(this::handleSendMessageWorkerSuccess, this::handleSendMessageWorkerFailed);
    }

    private void handleSendMessageWorkerSuccess(String string) {
        Log.d(TAG, "handleSendMessageWorkerSuccess: "+ string);
        view.onSendMessageSuccess(string);
    }

    private void handleSendMessageWorkerFailed(Throwable throwable) {
        Log.d(TAG, "handleSendMessageWorkerFailed: "+ throwable.toString());
        view.onSendMessageFailed();
    }



//    public void getAdminPhone() {
//        Log.d(TAG, "getAdminPhone: ");
//        this.api = retrofit.create(Api.class);
//         api.getAdminPhone().
//                observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleAdminResponse, this::handleAdminError);
//    }

//    private void handleAdminResponse(String phone) {
//        Log.d(TAG, "handleAdminResponse: ");
//        this.view.onAdminPhoneFetchSuccess(phone);
//    }
//
//    private void handleAdminError(Throwable s) {
//        Log.d(TAG, "handleAdminError: ");
//        this.view.onAdminPhoneFetchFailed();
//    }
//
//    public void getWorkerPhone() {
//        this.api = (Api) this.retrofit.create(Api.class);
//         this.api.getWorkerPhone().observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleWorkerResponse, this::handleWorkerError );
//    }
//
//    private void handleWorkerResponse(String s) {
//        Log.d(TAG, "handleWorkerResponse: ");
//        this.view.onWorkerPhoneFetchSuccess(s);
//    }
//
//    private void handleWorkerError(Throwable s) {
//        Log.d(TAG, "handleWorkerError: ");
//        this.view.onWorkerPhoneFetchFailed();
//







    private void handleSendMessageError(Throwable throwable) {
        Log.d(TAG, "handleSendMessageError: "+ throwable.toString());
        this.view.onSendMessageFailed();
    }

    private void handleSendMessageResponse(String string) {
        Log.d(TAG, "handleSendMessageResponse: "+ string);

        this.view.onSendMessageSuccess(string);
    }
}
