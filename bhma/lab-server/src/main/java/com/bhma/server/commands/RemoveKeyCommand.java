package com.bhma.server.commands;

import com.bhma.common.exceptions.IllegalKeyException;
import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;
import com.bhma.server.collectionmanagers.CollectionManager;

/**
 * remove_key command
 */
public class RemoveKeyCommand extends Command {
    private final CollectionManager collectionManager;

    public RemoveKeyCommand(CollectionManager collectionManager) {
        super("remove_key", "удалить элемент из коллекции по его ключу", CommandRequirement.NONE);
        this.collectionManager = collectionManager;
    }

    /**
     * removes element from collection by key
     * @param argument must be a number
     * @throws InvalidCommandArguments if argument is empty
     * @throws NumberFormatException if argument is not a number
     * @throws IllegalKeyException if there's no element with entered key
     */
    public ServerResponse execute(String argument, Object object, String username) throws InvalidCommandArguments,
            NumberFormatException, IllegalKeyException {
        if (argument.isEmpty() || object != null) {
            throw new InvalidCommandArguments();
        }
        if (!collectionManager.containsKey(Long.valueOf(argument))) {
            throw new IllegalKeyException("There's no value with that key.");
        }
        if (!collectionManager.getByKey(Long.valueOf(argument)).getOwnerUsername().equals(username)) {
            throw new IllegalKeyException("Object with that key belong to the another user");
        }
        collectionManager.remove(Long.valueOf(argument));
        return new ServerResponse(ExecuteCode.SUCCESS);
    }
}
