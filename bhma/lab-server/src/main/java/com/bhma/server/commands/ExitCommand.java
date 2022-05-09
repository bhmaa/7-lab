package com.bhma.server.commands;

import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;

/**
 * exit command
 */
public class ExitCommand extends Command {
    public ExitCommand() {
        super("exit", "завершить программу (без сохранения в файл)", CommandRequirement.NONE);
    }

    /**
     * sets execute flag to false
     * @param argument must be empty
     * @throws InvalidCommandArguments if argument isn't empty
     */
    public ServerResponse execute(String argument, Object object, String username) throws InvalidCommandArguments {
        if (!argument.isEmpty() || object != null) {
            throw new InvalidCommandArguments();
        }
        return new ServerResponse(ExecuteCode.EXIT);
    }
}
