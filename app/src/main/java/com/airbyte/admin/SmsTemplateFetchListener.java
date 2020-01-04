package com.airbyte.admin;

import java.util.List;

public interface SmsTemplateFetchListener {
    void onSmsTemplateFetchSuccess(List<String> smsTemplates);
    void onSmsTemplateFetchError();
}