package com.airbyte.admin;

import com.airbyte.BaseView;

import java.util.List;

public interface SendMessageView extends BaseView {

    void onGroupFetchSuccess(List<Group> list);
    void onGroupFetchError();
    void onSendMessageSuccess();
    void onSendMessageError();

    void onCustomersPhoneFetchSuccesss(List<String> phone);
    void onCustomersPhoneFetchError( );
}
