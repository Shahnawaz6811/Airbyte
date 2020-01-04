package com.airbyte.admin;

import android.util.Log;

import com.airbyte.customer.complaints.Complaint;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AdminServicePresenter {

    private static final String TAG = "AdminServicePresenter";

    private AddUserFragmentView addUserFragmentView;
    private AdminApiService adminApiService;
    private AddWorkerFragmentView addWorkerFragmentView;
    private AddGroupFragmentView addGroupFragmentView;

    public AdminServicePresenter(AddUserFragmentView addUserFragmentView, Retrofit retrofit) {
        this.addUserFragmentView = addUserFragmentView;
        adminApiService = retrofit.create(AdminApiService.class);

    }

    void addNewCustomer(String customerId, String customerName, String customerPhone1, String customerPhone2, String customerAddress){

        Disposable subscribe = adminApiService.addNewCustomer(customerId, customerName, customerPhone1, customerPhone2, customerAddress)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleAddCustomerSuccess, this::handleAddCustomerError);
    }

    private void handleAddCustomerSuccess(String message) {
        Log.d(TAG, "handleComplaintFetchedSuccess: " + message);
        if (message.equals("2")) {
            addUserFragmentView.onCustomerAddFailed("Customer already exists");

        }
        else if(message.equals("0")) {
            addUserFragmentView.onCustomerAddFailed("Error  adding customer, Retry in a while");
        }
        else{
            addUserFragmentView.onCustomerAddSuccess();
        }

    }

    private void handleAddCustomerError(Throwable throwable) {
        Log.d(TAG, "handleComplaintFetchedError: "+ throwable.toString());
        addUserFragmentView.onCustomerAddFailed("Unexpected error occured");
    }


     AdminServicePresenter(AddWorkerFragmentView addWorkerFragmentView,Retrofit retrofit) {
        this.addWorkerFragmentView = addWorkerFragmentView;
        this.adminApiService = retrofit.create(AdminApiService.class);
    }

    void addNewWorker(String name, String phone1, String phone2) {

        Disposable subscribe = adminApiService.addNewWorker(name, phone1, phone2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleAddWorkerSuccess, this::handleAddWorkerError);
    }

    private void handleAddWorkerSuccess(String message) {
        Log.d(TAG, "handleComplaintFetchedSuccess: " + message);
        if (message.equals("2")) {
            addWorkerFragmentView.onWorkerAddFailed("Worker already exists");

        }
        else if(message.equals("0")) {
            addWorkerFragmentView.onWorkerAddFailed("Error  adding customer, Retry in a while");
        }
        else{
            addWorkerFragmentView.onWorkerAddSuccess();
        }

    }

    private void handleAddWorkerError(Throwable throwable) {
        Log.d(TAG, "handleComplaintFetchedError: "+ throwable.toString());
        addWorkerFragmentView.onWorkerAddFailed("Unexpected error occured");
    }

    public AdminServicePresenter(AddGroupFragmentView addGroupFragmentView,Retrofit retrofit) {
        this.addGroupFragmentView = addGroupFragmentView;
        this.adminApiService = retrofit.create(AdminApiService.class);

    }

    void getGroups() {

          Log.d(TAG, "getGroups: ");
          Disposable subscribe = adminApiService.getGroups()
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(this::handleGetGroupSuccess, this::handleGetGroupError);


     }


    private void handleGetGroupSuccess(List<Group> groups) {
        Log.d(TAG, "handleGetGroupSuccess: " + groups);
        addGroupFragmentView.onGroupFetchSuccess(groups);
    }

    private void handleGetGroupError(Throwable throwable) {
        Log.d(TAG, "handleGetGroupError: "+ throwable.toString());
        addGroupFragmentView.onGroupFetchFailed();
    }


      void addNewGroup(String groupName) {
        Disposable subscribe = adminApiService.addNewGroup(groupName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleGroupAddSuccess, this::handleGroupAddError);
    }

    private void handleGroupAddSuccess(String message) {
        Log.d(TAG, "handleGroupAddSuccess: " + message);
        addGroupFragmentView.onNewGroupAddSuccess();
    }

    private void handleGroupAddError(Throwable throwable) {
        Log.d(TAG, "handleGroupAddError: "+ throwable.toString());
        addGroupFragmentView.onNewGroupAddFailed();
    }

      void deleteGroupById(String id) {

        Disposable subscribe = adminApiService.deleteGroupById(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleGroupDeleteSuccess, this::handleGroupDeleteError);
     }

    void deleteWorkerById(String id) {

        Disposable subscribe = adminApiService.deleteWorkerById(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleWorkerDeleteSuccess, this::handleWorkerDeleteError);
    }


    private void handleWorkerDeleteSuccess(String message) {
        if (message.equals("success")) {
            Log.d(TAG, "handleWorkerDeleteSuccess: " + message);
            addWorkerFragmentView.onWorkerDeleteSuccess();
        }
        else{
            addWorkerFragmentView.onWorkerDeleteFailed();
        }

    }

    private void handleWorkerDeleteError(Throwable throwable) {
        Log.d(TAG, "handleWorkerDeleteError: "+ throwable.toString());
        addWorkerFragmentView.onWorkerDeleteFailed();
    }


    void deleteGroupCombinationById(String id) {

        Disposable subscribe = adminApiService.deleteGroupCombinationById(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleGroupDeleteSuccess, this::handleGroupDeleteError);
    }

    private void handleGroupDeleteSuccess(String message) {
        Log.d(TAG, "handleGroupDeleteSuccess: " + message);
        addGroupFragmentView.onGroupDeleteSuccess();
    }

    private void handleGroupDeleteError(Throwable throwable) {
        Log.d(TAG, "handleGroupDeleteError: "+ throwable.toString());
        addGroupFragmentView.onGroupDeleteFailed();
    }

      void getWorkers() {

        Disposable subscribe = adminApiService.getWorkers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleGetWorkersSuccess, this::handleGetWorkersError);
    }

    private void handleGetWorkersSuccess(List<Worker> workers) {
        Log.d(TAG, "handleGetGroupSuccess: " + workers);
        addWorkerFragmentView.onWorkerFetchSuccess(workers);
    }

    private void handleGetWorkersError(Throwable throwable) {
        Log.d(TAG, "handleGetGroupError: "+ throwable.toString());
        addWorkerFragmentView.onWorkerFetchFailed();
    }

    public void addNewGroupCombination(String groupId, String customerId) {

        Disposable subscribe = adminApiService.addNewGroupCombination(groupId,customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleGroupAddSuccess, this::handleGroupAddError);
    }


    public void getGroupCombination() {
        Log.d(TAG, "getGroupCombination: ");
        Disposable subscribe = adminApiService.getGroupCombination()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleGetGroupCombinationSuccess, this::handleGetGroupCombinationError);
    }


    private void handleGetGroupCombinationSuccess(List<GroupCombination> groupCombinations) {
        Log.d(TAG, "handleGetGroupSuccess: " + groupCombinations);
        addGroupFragmentView.onGroupCombinationFetchSuccess(groupCombinations);
    }

    private void handleGetGroupCombinationError(Throwable throwable) {
        Log.d(TAG, "handleGetGroupError: "+ throwable.toString());
        addGroupFragmentView.onGroupCombinationFetchError();
    }



}
