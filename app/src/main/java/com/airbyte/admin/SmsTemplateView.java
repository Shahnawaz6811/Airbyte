package com.airbyte.admin;

import java.util.List;

public interface SmsTemplateView {

    void onSmsTemplateAddSuccess();
    void onSmsTemplateAddFailed();
    void onSmsTemplateFetchSuccess(List<Template> templates);
    void onSmsTemplateFetchFailed();
    void onSmsTemplateDeleteSuccess();
    void onSmsTemplateDeleteFailed();


}
