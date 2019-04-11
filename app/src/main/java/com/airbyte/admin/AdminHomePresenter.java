package com.airbyte.admin;

import android.util.Log;

import com.airbyte.customer.complaints.Complaint;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

class AdminHomePresenter {

    Disposable disposable;
    private static final String TAG = "AdminHomePresenter";
    private AdminApiService adminApiService;
    private ComplaintFragmentListView complaintFragmentListView;
    private PaymentFragmentListView paymentFragmentListView;
    private CustomerFragmentListView userFragmentListView;
    private CustomerUpdateView customerUpdateView;
    SmsTemplateFetchListener smsTemplateFetchListener;

     AdminHomePresenter(Retrofit retrofit, ComplaintFragmentListView complaintFragmentListView) {
         this.complaintFragmentListView = complaintFragmentListView;
         adminApiService = retrofit.create(AdminApiService.class);
    }


    void fetchComplaints(){
         disposable = adminApiService.getComplaints()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleComplaintFetchedSuccess, this::handleComplaintFetchedError);

     }

    AdminHomePresenter(Retrofit retrofit, CustomerUpdateView customerUpdateView){
         adminApiService = retrofit.create(AdminApiService.class);
         this.customerUpdateView = customerUpdateView;

    }


    private void handleComplaintFetchedSuccess(List<Complaint> complaints) {
        Log.d(TAG, "handleComplaintFetchedSuccess: " + complaints);
        complaintFragmentListView.onComplaintListFetchSuccess(complaints);

    }

    private void handleComplaintFetchedError(Throwable throwable) {
        disposable.dispose();
        Log.d(TAG, "handleComplaintFetchedError: "+ throwable.toString());
        complaintFragmentListView.onComplaintListFetchFailed();
     }



    AdminHomePresenter(Retrofit retrofit, CustomerFragmentListView userFragmentListView){
        Log.d(TAG, "AdminHomePresenter: "+userFragmentListView);
        this.userFragmentListView = userFragmentListView;
        this.adminApiService = retrofit.create(AdminApiService.class);
    }


    void fetchCustomers(){
        Disposable subscribe = adminApiService.getCustomers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleCustomerFetchedSuccess, this::handleCustomerFetchedError);
    }

    private void handleCustomerFetchedSuccess(List<Customer> customerList) {
        Log.d(TAG, "handleCustomerFetchedSuccess: " + customerList);
        userFragmentListView.onUserListFetchSuccess(customerList);

    }

    private void handleCustomerFetchedError(Throwable throwable) {
        Log.d(TAG, "handleCustomerFetchedError: "+ throwable.toString());
        userFragmentListView.onUserListFetchFailed();
    }



    AdminHomePresenter(Retrofit retrofit, PaymentFragmentListView paymentFragmentListView) {
        this.paymentFragmentListView = paymentFragmentListView;
        adminApiService = retrofit.create(AdminApiService.class);

    }
    void fetchPayments(){
        Disposable subscribe = adminApiService.getPayments()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handlePaymentFetchedSuccess, this::handlePaymentFetchedError);
    }

    private void handlePaymentFetchedSuccess(List<Payment> payments) {
        Log.d(TAG, "handlePaymentFetchedSuccess: " + payments);
        paymentFragmentListView.onPaymentListFetchSuccess(payments);

    }

    private void handlePaymentFetchedError(Throwable throwable) {
        Log.d(TAG, "handlePaymentFetchedError: "+ throwable.toString());
        paymentFragmentListView.onPaymentListFetchFailed();
    }


    public void updateUserDetail(String id, String name, String address, String phone1, String phone2) {

        Disposable subscribe = adminApiService.updateCustomerDetail(id, name, address, phone1, phone2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleCustomerUpdateSuccess, this::handleCustomerUpdateFailed);
    }

    private void handleCustomerUpdateSuccess(String message) {
        Log.d(TAG, "handleCustomerUpdateSuccess: " + message);
        customerUpdateView.onCustomerUpdateSuccess(message);

    }

    private void handleCustomerUpdateFailed(Throwable throwable) {
        Log.d(TAG, "handleCustomerUpdateFailed: "+ throwable.toString());
        customerUpdateView.onCustomerUpdateFailed();
    }

      void fetchSmsTemplates(SmsTemplateFetchListener listener) {
          Log.d(TAG, "fetchSmsTemplates: ");
        this.smsTemplateFetchListener = listener;
        Disposable subscribe = adminApiService.getSmsTemplates()
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
}
