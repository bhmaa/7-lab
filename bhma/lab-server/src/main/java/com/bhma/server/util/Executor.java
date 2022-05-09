package com.bhma.server.util;

import com.bhma.common.exceptions.IllegalKeyException;
import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;
import com.bhma.server.commands.Command;

public class Executor {
    private final CommandManager commandManager;

    public Executor(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public ServerResponse executeCommand(String commandName, String argument, Object objectArgument, String username) {
        ServerResponse response;
        if (commandManager.getCommands().containsKey(commandName)) {
            Command command = commandManager.getCommands().get(commandName);
            try {
                response = command.execute(argument, objectArgument, username);
            } catch (InvalidCommandArguments | IllegalKeyException e) {
                response = new ServerResponse(e.getMessage(), ExecuteCode.ERROR);
            }
        } else {
            response = new ServerResponse("Unknown command detected: " + commandName, ExecuteCode.ERROR);
        }
        return response;
    }
}
