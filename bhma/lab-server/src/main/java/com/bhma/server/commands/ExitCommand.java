package com.bhma.server.commands;

import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandObjectRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;
import org.apache.logging.log4j.Logger;

/**
 * exit command
 */
public class ExitCommand extends Command {
    private final Logger logger;

    public ExitCommand(Logger logger) {
        super("exit", "завершить программу (без сохранения в файл)", CommandObjectRequirement.NONE,
                false);
        this.logger = logger;
    }

    /**
     * sets execute flag to false
     *
     * @param argument must be empty
     * @throws InvalidCommandArguments if argument isn't empty
     */
    public ServerResponse execute(String argument, Object object, String username) throws InvalidCommandArguments {
        if (!argument.isEmpty() || object != null) {
            throw new InvalidCommandArguments();
        }
        logger.info(() -> "user " + username + " logged out");
        return new ServerResponse(ExecuteCode.EXIT);
    }
}
