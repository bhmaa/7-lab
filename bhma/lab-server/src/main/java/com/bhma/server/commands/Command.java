package com.bhma.server.commands;

import com.bhma.common.exceptions.IllegalKeyException;
import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandObjectRequirement;
import com.bhma.common.util.ServerResponse;

/**
 * parent of all commands
 */
public abstract class Command {
    private final String name;
    private final String description;
    private final CommandObjectRequirement objectRequirement;
    private final boolean commandNeedsStringArgument;

    public Command(String name, String description, CommandObjectRequirement objectRequirement,
                   boolean commandNeedsStringArgument) {
        this.name = name;
        this.description = description;
        this.objectRequirement = objectRequirement;
        this.commandNeedsStringArgument = commandNeedsStringArgument;
    }

    public abstract ServerResponse execute(String argument, Object object, String username) throws InvalidCommandArguments,
            IllegalKeyException;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CommandObjectRequirement getObjectRequirement() {
        return objectRequirement;
    }

    public boolean isCommandNeedsStringArgument() {
        return commandNeedsStringArgument;
    }
}
