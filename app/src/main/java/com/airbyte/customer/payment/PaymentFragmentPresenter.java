package com.airbyte.customer.payment;

import android.util.Log;

import com.airbyte.BaseView;
import com.airbyte.customer.CustomerApiService;
import com.airbyte.utils.MessageApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class PaymentFragmentPresenter {

    private static final String TAG = "PaymentFragmentPresente";

     PaymentFragmentView view;
    private MessageApiService messageApiService;
    CustomerApiService customerApiService;
    Retrofit messageApiRetrofit;
    private Retrofit customerApiRetrofit;
    PaymentInsertCallback callback;

    public void getOrderId() {

        Disposable subscribe = customerApiService.getOrderId()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleGetOrderIdSuccess, this::handleGetOrderError);
    }

    private void handleGetOrderError(Throwable throwable) {
        Log.d(TAG, "handleGetOrderError: ");
        callback.onOrderIdFetchError();
    }

    private void handleGetOrderIdSuccess(String s) {
        Log.d(TAG, "handleGetOrderIdSuccess: "+s);
        callback.onOrdeIdFetchSuccess(s);
    }

    public interface PaymentInsertCallback extends BaseView {
        void onPaymentInsertSuccess(Payment payment);
        void onPaymentInsertError();

        void onOrdeIdFetchSuccess(String orderId);
        void onOrderIdFetchError();
    }

     PaymentFragmentPresenter(Retrofit retrofit,PaymentFragmentView view) {
        this.view = view;
        customerApiService = retrofit.create(CustomerApiService.class);
        messageApiService = retrofit.newBuilder().baseUrl(MessageApiService.api91BaseUrl).build().create(MessageApiService.class);

     }

    PaymentFragmentPresenter(Retrofit retrofit,PaymentInsertCallback callback) {
        this.callback = callback;
        customerApiService = retrofit.create(CustomerApiService.class);

    }



     void sendPayRequest(String adminPhone, String smsBody){
        Log.d(TAG, "sendPayRequest: ");
        Disposable subscribe = messageApiService.sendMessage(MessageApiService.MSG91_AUTH_KEY, adminPhone, smsBody, "klwn", "airbyt")
                                               .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeOn(Schedulers.io())
                                                .subscribe(this::handleSendMessageResponse, this::handleSendMessageError);
    }


    private void handleSendMessageResponse(String s) {
        Log.d(TAG, "handleSendMessageResponse: ");
        view.onSendMessageSuccess("Payment Request Sent" + s);
    }

    private void handleSendMessageError(Throwable s) {
        Log.d(TAG, "handleSendMessageError: "+ s.toString() );
        view.onSendMessageFailed();

    }


     void insertPaymentInfo(String customerPhone,String customerId,
                            String amount,
                            String txnId,String txnDate,String orderId,String status) {
         Log.d(TAG, "insertPaymentInfo: "+status);
        Disposable subscribe = customerApiService.insertPaymentInfo(customerPhone, customerId, amount, txnId,txnDate,orderId,status)
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeOn(Schedulers.io())
                                                .subscribe(this::handleInsertPaymentResponse, this::handleInsertPaymentError);

    }

    private void handleInsertPaymentError(Throwable throwable) {
        Log.d(TAG, "handlePaymentError: "+ throwable.toString());
//        view.onPaymentFailed();
        callback.onPaymentInsertError();
    }

    private void handleInsertPaymentResponse(Payment payment) {
        Log.d(TAG, "handleInsertPaymentResponse: "+payment);
         Log.d(TAG, "handleInsertPaymentResponse: "+callback);
        callback.onPaymentInsertSuccess(payment);
    }

     void getLastPaymentDetail(String customerId) {

            Disposable subscribe = customerApiService.getLastPaymentInfo(customerId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleLastPaymentResponse, this::handleLastPaymentError);

        }

    private void handleLastPaymentError(Throwable throwable) {
        Log.d(TAG, "handleLastPaymentError: "+ throwable.toString());
        view.onLastPaymentInfoFetchFailed();
    }

    private void handleLastPaymentResponse(Payment payment) {
        Log.d(TAG, "handleLastPaymentResponse: ");
        view.onLastPaymentInfoFetchSuccess(payment);
    }

    public void makePayment(String amount) {
        Log.d(TAG, "makePayment: ");
//         Retrofit retrofit = new Retrofit.Builder().baseUrl(CustomerApiService.PAYMENT).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
//         customerApiService = retrofit.create(CustomerApiService.class);
//
//        Disposable subscribe = customerApiService.makePayment(amount).observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleMakePaymentResponse, this::handleMakePaymentError);
    }

    private void handleMakePaymentError(Throwable throwable) {
        Log.d(TAG, "handleMakePaymentError: "+ throwable.toString());
        view.onLastPaymentInfoFetchFailed();
    }

    private void handleMakePaymentResponse(String response) {
        Log.d(TAG, "handleMakePaymentResponse: "+response);
//        view.onLastPaymentInfoFetchSuccess(response);
    }
}



