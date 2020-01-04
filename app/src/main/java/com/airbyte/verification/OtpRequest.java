package com.airbyte.verification;

public class OtpRequest {
    String authkey;
    String message;
    String mobile;

    public OtpRequest(String authkey, String mobile, String message) {
        this.authkey = authkey;
        this.mobile = mobile;
        this.message = message;
    }
}
