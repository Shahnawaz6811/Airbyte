package com.airbyte.customer.complaints;

import com.airbyte.BaseView;

import java.util.List;

interface ComplaintFragmentView extends BaseView {
    void onAdminPhoneFetchFailed();

    void onAdminPhoneFetchSuccess(String str);

    void handleComplaintTemplatesError();

    void onComplaintTemplatesSuccess(List<String> list);

    void onComplaintRegisterFailed();

    void onComplaintRegisterSuccess(String str);

    void onSendMessageFailed();

    void onSendMessageSuccess(String str);

    void onWorkerPhoneFetchFailed();

    void onWorkerPhoneFetchSuccess(String str);
}
