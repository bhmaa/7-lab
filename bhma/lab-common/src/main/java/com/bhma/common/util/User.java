package com.bhma.common.util;

import java.io.Serializable;

public class User implements Serializable {
    private final String username;
    private final String hashPassword;

    public User(String username, String hashPassword) {
        this.username = username;
        this.hashPassword = hashPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashPassword() {
        return hashPassword;
    }
}
