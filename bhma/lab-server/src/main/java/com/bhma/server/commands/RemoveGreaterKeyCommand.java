package com.bhma.server.commands;

import com.bhma.common.data.SpaceMarine;
import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;
import com.bhma.server.collectionmanagers.CollectionManager;

public class RemoveGreaterKeyCommand extends Command {
    private final CollectionManager collectionManager;

    public RemoveGreaterKeyCommand(CollectionManager collectionManager) {
        super("remove_greater_key", "удалить из коллекции все элементы, превышающие заданный",
                CommandRequirement.NONE);
        this.collectionManager = collectionManager;
    }

    /**
     * removes all elements that greater than entered one
     * @param argument must be empty
     * @throws InvalidCommandArguments if argument isn't empty
     */
    public ServerResponse execute(String argument, Object spaceMarine, String username) throws InvalidCommandArguments {
        if (!argument.isEmpty() || spaceMarine == null || spaceMarine.getClass() != SpaceMarine.class) {
            throw new InvalidCommandArguments();
        }
        long undeletedItems = collectionManager.removeGreater((SpaceMarine) spaceMarine, username);
        if (undeletedItems != 0) {
            return new ServerResponse(undeletedItems + " objects were not deleted", ExecuteCode.SERVER_ERROR);
        }
        return new ServerResponse(ExecuteCode.SUCCESS);
    }
}
