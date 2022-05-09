package com.bhma.common.util;

import java.io.Serializable;

public class ClientRequest implements Serializable {
    private final String commandName;
    private final String commandArguments;
    private final Object objectArgument;
    private final User user;

    public ClientRequest(String commandName, String commandArguments, Object objectArgument, User user) {
        this.commandName = commandName;
        this.commandArguments = commandArguments;
        this.objectArgument = objectArgument;
        this.user = user;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCommandArguments() {
        return commandArguments;
    }

    public User getUser() {
        return user;
    }

    public Object getObjectArgument() {
        return objectArgument;
    }

    @Override
    public String toString() {
        return "ClientRequest{"
                + " commandName='" + commandName + '\''
                + ", commandArguments='" + commandArguments + '\''
                + ", objectArgument=" + objectArgument
                + '}';
    }
}
