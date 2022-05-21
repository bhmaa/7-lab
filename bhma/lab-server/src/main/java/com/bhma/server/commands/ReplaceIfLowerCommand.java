package com.bhma.server.commands;

import com.bhma.common.data.SpaceMarine;
import com.bhma.common.exceptions.IllegalKeyException;
import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;
import com.bhma.server.collectionmanagers.CollectionManager;

/**
 * replace_if_lowe command
 */
public class ReplaceIfLowerCommand extends Command {
    private final CollectionManager collectionManager;

    public ReplaceIfLowerCommand(CollectionManager collectionManager) {
        super("replace_if_lower", "заменить значение по ключу, если новое значение меньше старого",
                CommandRequirement.SPACE_MARINE);
        this.collectionManager = collectionManager;
    }

    /**
     * update value by key if it's greater than entered one
     * @param argument must be a number
     * @throws InvalidCommandArguments if argument is empty
     * @throws NumberFormatException if argument isn't a number
     * @throws IllegalKeyException if there's no element with entered key in collection
     */
    public ServerResponse execute(String argument, Object spaceMarine, String username) throws InvalidCommandArguments,
            NumberFormatException, IllegalKeyException {
        if (argument.isEmpty() || spaceMarine == null || spaceMarine.getClass() != SpaceMarine.class) {
            throw new InvalidCommandArguments();
        }
        try {
            long key = Long.parseLong(argument);
            if (!collectionManager.containsKey(key)) {
                throw new IllegalKeyException("There's no element with that key");
            }
            if (!collectionManager.getByKey(key).getOwnerUsername().equals(username)) {
                throw new IllegalKeyException("Object with that key belong to the another user");
            }
            SpaceMarine oldSpaceMarine = collectionManager.getByKey(key);
            if (oldSpaceMarine.compareTo((SpaceMarine) spaceMarine) < 0) {
                collectionManager.addToCollection(key, (SpaceMarine) spaceMarine);
            }
            return new ServerResponse(ExecuteCode.SUCCESS);
        } catch (NumberFormatException e) {
            return new ServerResponse("the argument must be a long number", ExecuteCode.ERROR);
        }
    }
}
