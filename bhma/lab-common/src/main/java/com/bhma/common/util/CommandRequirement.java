package com.bhma.common.util;

import java.io.Serializable;

public class CommandRequirement implements Serializable {
    private final CommandObjectRequirement commandObjectRequirement;
    private final boolean commandNeedsStringArgument;

    public CommandRequirement(CommandObjectRequirement commandObjectRequirement, boolean commandNeedsStringArgument) {
        this.commandObjectRequirement = commandObjectRequirement;
        this.commandNeedsStringArgument = commandNeedsStringArgument;
    }

    public CommandObjectRequirement getCommandObjectRequirement() {
        return commandObjectRequirement;
    }

    public boolean isCommandNeedsStringArgument() {
        return commandNeedsStringArgument;
    }
}
