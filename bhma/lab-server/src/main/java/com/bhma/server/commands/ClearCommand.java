package com.bhma.server.commands;

import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandObjectRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;
import com.bhma.server.collectionmanagers.CollectionManager;

/**
 * clear command
 */
public class ClearCommand extends Command {
    private final CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию", CommandObjectRequirement.NONE, false);
        this.collectionManager = collectionManager;
    }

    /**
     * removes all elements from collection
     * @param argument must be empty
     * @throws InvalidCommandArguments if argument isn't empty
     */
    public ServerResponse execute(String argument, Object object, String username) throws InvalidCommandArguments {
        if (!argument.isEmpty() || object != null) {
            throw new InvalidCommandArguments();
        }
        if (!collectionManager.clear(username)) {
            return new ServerResponse("Cannot delete objects", ExecuteCode.SERVER_ERROR);
        }
        return new ServerResponse(ExecuteCode.SUCCESS);
    }
}
