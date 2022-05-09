package com.bhma.server.commands;

import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;

/**
 * execute_script command
 */
public class ExecuteScriptCommand extends Command {

    public ExecuteScriptCommand() {
        super("execute_script", "считать и исполнить скрипт из указанного файла", CommandRequirement.NONE);
    }

    /**
     * switches input manager to a script mode
     * @param argument mustn't be empty
     * @throws InvalidCommandArguments if argument is empty
     */
    public ServerResponse execute(String argument, Object object, String username) throws InvalidCommandArguments {
        if (argument.isEmpty() || object != null) {
            throw new InvalidCommandArguments();
        }
        return new ServerResponse(argument, ExecuteCode.READ_SCRIPT);
    }
}
