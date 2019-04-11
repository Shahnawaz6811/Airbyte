package com.airbyte.verification;

public class OtpResponse {
    private String message;
    private String type;

    public OtpResponse(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    public String getType() {
        return this.type;
    }
}
