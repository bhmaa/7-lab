package com.bhma.common.util;

public enum RegistrationCode {
    REGISTERED("the user has been successfully registered"),
    AUTHORIZED("you are logged into your account"),
    DENIED("invalid password or username");

    private final String message;

    RegistrationCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
