package com.bhma.server.commands;

import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;
import com.bhma.server.collectionmanagers.CollectionManager;

/**
 * remove_lower_key command
 */
public class RemoveLowerKeyCommand extends Command {
    private final CollectionManager collectionManager;

    public RemoveLowerKeyCommand(CollectionManager collectionManager) {
        super("remove_lower_key", "удалить из коллекции все элементы, ключ которых меньше, чем заданный",
                CommandRequirement.NONE);
        this.collectionManager = collectionManager;
    }

    /**
     * removes all elements whose key is lower than entered one
     * @param argument must be a number
     * @throws InvalidCommandArguments if argument is empty
     * @throws NumberFormatException if argument isn't a number
     */
    public ServerResponse execute(String argument, Object object, String username) throws InvalidCommandArguments,
            NumberFormatException {
        if (argument.isEmpty() || object != null) {
            throw new InvalidCommandArguments();
        }
        long undeletedItems = collectionManager.removeLowerKey(Long.parseLong(argument), username);
        if (undeletedItems != 0) {
            return new ServerResponse(undeletedItems + " objects were not deleted", ExecuteCode.SERVER_ERROR);
        }
        return new ServerResponse(ExecuteCode.SUCCESS);
    }
}
