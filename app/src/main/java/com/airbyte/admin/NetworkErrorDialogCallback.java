package com.airbyte.admin;

import androidx.appcompat.app.AlertDialog;

public interface NetworkErrorDialogCallback {

    void onRetry();
    void onCancel();
}
