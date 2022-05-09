package com.bhma.common.util;

import java.io.Serializable;

public class PullingRequest implements Serializable {
    private final String commandName = "pull commands";
    private final User user;

    public PullingRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "PullingRequest{"
                + " commandName='" + commandName + '\''
                + '}';
    }
}
