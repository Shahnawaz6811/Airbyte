package com.airbyte.admin;

import com.airbyte.BaseView;

import java.util.List;

public interface AddGroupFragmentView extends BaseView {

    void onGroupFetchSuccess(List<Group> groups);
    void onGroupFetchFailed();

    void onNewGroupAddSuccess();
    void onNewGroupAddFailed();

    void onGroupDeleteSuccess();
    void onGroupDeleteFailed();

    void onGroupCombinationFetchSuccess(List<GroupCombination> groupCombinations);

    void onGroupCombinationFetchError();

}
