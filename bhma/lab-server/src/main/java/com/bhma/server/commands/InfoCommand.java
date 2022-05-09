package com.bhma.server.commands;

import com.bhma.common.exceptions.InvalidCommandArguments;
import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.ServerResponse;
import com.bhma.server.collectionmanagers.CollectionManager;

/**
 * info command
 */
public class InfoCommand extends Command {
    private final CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации,"
                + " количество элементов и т.д.)", CommandRequirement.NONE);
        this.collectionManager = collectionManager;
    }

    /**
     * print class of collection, it's creation date and size
     * @param argument must be empty
     * @throws InvalidCommandArguments if argument isn't empty
     */
    public ServerResponse execute(String argument, Object object, String username) throws InvalidCommandArguments {
        if (!argument.isEmpty() || object != null) {
            throw new InvalidCommandArguments();
        }
        return new ServerResponse(collectionManager.collectionInfo(), ExecuteCode.VALUE);
    }
}
