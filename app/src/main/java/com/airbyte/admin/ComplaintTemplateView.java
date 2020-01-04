package com.airbyte.admin;

import java.util.List;

public interface ComplaintTemplateView {

    void onComlaintTemplateAddSuccess();
    void onComplaintTemplateAddFailed();
    void onComplaintTemplateFetchSuccess(List<Template> templates);
    void onComplaintTemplateFetchFailed();
    void onComplaintTemplateDeleteSuccess();
    void onComplaintTemplateDeleteFailed();


}
