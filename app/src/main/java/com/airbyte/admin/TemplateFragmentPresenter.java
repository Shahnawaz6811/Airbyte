package com.airbyte.admin;

import android.util.Log;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TemplateFragmentPresenter {

    private static final String TAG = "TemplateFragmentPresent";

    private AdminApiService apiService;
    private SmsTemplateView smsTemplateView;
    private ComplaintTemplateView complaintTemplateView;

      TemplateFragmentPresenter(Retrofit retrofit, SmsTemplateView view) {
         this.smsTemplateView = view;
         apiService = retrofit.create(AdminApiService.class);
    }

    public TemplateFragmentPresenter(Retrofit retrofit, ComplaintTemplateView view) {
        this.complaintTemplateView = view;
        apiService = retrofit.create(AdminApiService.class);
    }



    void addSmsTemplate(String template){
        Disposable subscribe = apiService.insertSmsTemplate(template)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleAddSmsTemplateSuccess, this::handleAddSmsTemplateError);
    }

    private void handleAddSmsTemplateSuccess(String message) {
        Log.d(TAG, "handleAddTemplateSuccess: " + message);
        smsTemplateView.onSmsTemplateAddSuccess();

    }

    private void handleAddSmsTemplateError(Throwable throwable) {
        Log.d(TAG, "handleAddSmsTemplateError: "+ throwable.toString());
        smsTemplateView.onSmsTemplateAddFailed();
    }


    void addComplaintTemplate(String template){
        Disposable subscribe = apiService.insertComplaintTemplate(template)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleAddComplaintTemplateSuccess, this::handleAddComplaintTemplateError);
    }



    private void handleAddComplaintTemplateSuccess(String message) {
        Log.d(TAG, "handleAddComplaintTemplateSuccess: " + message);
             complaintTemplateView.onComlaintTemplateAddSuccess();

        }

    private void handleAddComplaintTemplateError(Throwable throwable) {
        Log.d(TAG, "handleAddComplaintTemplateError: "+ throwable.toString());
        complaintTemplateView.onComplaintTemplateAddFailed();
    }



    void getSmsTemplates(){
        Log.d(TAG, "getSmsTemplates: ");
        Disposable subscribe = apiService.getSmsTemplate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleFetchSmsTemplateSuccess, this::handleFetchSmsTemplateError);
    }

    private void handleFetchSmsTemplateSuccess(List<Template> templates) {
        Log.d(TAG, "handleFetchSmsTemplateSuccess: " + templates);
        smsTemplateView.onSmsTemplateFetchSuccess(templates);

    }

    private void handleFetchSmsTemplateError(Throwable throwable) {
        Log.d(TAG, "handleFetchSmsTemplateError: "+ throwable.toString());
        smsTemplateView.onSmsTemplateFetchFailed();
    }



    void getComplaintTemplate(){
        Disposable subscribe = apiService.getComplaintTemplates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleFetchComplaintTemplateSuccess, this::handleFetchCompalintTemplateError);
    }

    private void handleFetchComplaintTemplateSuccess(List<Template> templates) {
        Log.d(TAG, "handleFetchComplaintTemplateSuccess: " + templates);
        complaintTemplateView.onComplaintTemplateFetchSuccess(templates);

    }

    private void handleFetchCompalintTemplateError(Throwable throwable) {
        Log.d(TAG, "handleFetchCompalintTemplateError: "+ throwable.toString());
        complaintTemplateView.onComplaintTemplateFetchFailed();
    }




    void deleteComplaintTemplate(String id){
        Disposable subscribe = apiService.deleteComplaintTemplate(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleDeleteComplaintTemplateSuccess, this::handleDeleteComplaintTemplateError);
    }

    private void handleDeleteComplaintTemplateSuccess(String templates) {
        Log.d(TAG, "handleDeleteComplaintTemplateSuccess: " + templates);
        complaintTemplateView.onComplaintTemplateDeleteSuccess();

    }

    private void handleDeleteComplaintTemplateError(Throwable throwable) {
        Log.d(TAG, "handleDeleteComplaintTemplateError: "+ throwable.toString());
        complaintTemplateView.onComplaintTemplateDeleteFailed();
    }



    void deleteSmsTemplate(String id){
        Log.d(TAG, "deleteSmsTemplate: "+id);
        Disposable subscribe = apiService.deleteSmsTemplate(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleDeleteSmsTemplateSuccess, this::handleDeleteSmsTemplateError);
    }


    private void handleDeleteSmsTemplateSuccess(String templates) {
        Log.d(TAG, "handleDeleteSmsTemplateSuccess: " + templates);
        smsTemplateView.onSmsTemplateDeleteSuccess();

    }

    private void handleDeleteSmsTemplateError(Throwable throwable) {
        Log.d(TAG, "handleDeleteSmsTemplateError: "+ throwable.toString());
        smsTemplateView.onSmsTemplateDeleteFailed();
    }









}
