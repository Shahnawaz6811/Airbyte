package com.airbyte.admin;

import com.airbyte.BaseView;
import com.airbyte.customer.complaints.Complaint;

import java.util.List;

interface CustomerFragmentListView extends BaseView {

    void onUserListFetchSuccess(List<Customer> paymentList);

    void onUserListFetchFailed();


 }
