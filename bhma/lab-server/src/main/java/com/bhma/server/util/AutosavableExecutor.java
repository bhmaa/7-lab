package com.bhma.server.util;

import com.bhma.common.util.ServerResponse;
import com.bhma.server.collectionmanagers.SavableCollectionManager;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import org.apache.logging.log4j.Logger;

public class AutosavableExecutor extends Executor {
    private final SavableCollectionManager savableCollectionManager;
    private final Logger logger;

    public AutosavableExecutor(CommandManager commandManager, SavableCollectionManager savableCollectionManager,
                               Logger logger) {
        super(commandManager);
        this.savableCollectionManager = savableCollectionManager;
        this.logger = logger;
    }

    @Override
    public ServerResponse executeCommand(String commandName, String argument, Object objectArgument, String username) {
        ServerResponse response = super.executeCommand(commandName, argument, objectArgument, username);
        try {
            savableCollectionManager.save();
        } catch (IOException | JAXBException e) {
            logger.error(e);
        }
        return response;
    }
}
