package com.airbyte.admin;

import com.airbyte.BaseView;
import com.airbyte.customer.complaints.Complaint;
import com.airbyte.customer.payment.Payment;

import java.util.List;

public interface ComplaintFragmentListView extends BaseView {

    void onComplaintListFetchSuccess(List<Complaint> paymentList);

    void onComplaintListFetchFailed();

}
