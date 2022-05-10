package com.bhma.server;

import com.bhma.common.exceptions.IllegalAddressException;
import com.bhma.common.util.Checker;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.bhma.server.collectionmanagers.CollectionManager;
import com.bhma.server.collectionmanagers.SQLCollectionManager;
import com.bhma.server.usersmanagers.SQLUserManager;
import com.bhma.server.usersmanagers.SQLUserTableCreator;
import com.bhma.server.util.CommandManager;
import com.bhma.server.util.Executor;
import com.bhma.server.util.Receiver;
import com.bhma.server.util.SQLManager;
import com.bhma.server.util.UsersHandler;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public final class Server {
    private static final int BUFFER_SIZE = 2048;
    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    private static final int NUMBER_OF_ARGUMENTS = 7;
    private static final int INDEX_HOST = 0;
    private static final int INDEX_PORT = 1;
    private static final int INDEX_DB_HOSTNAME = 2;
    private static final int INDEX_DB_PORT = 3;
    private static final int INDEX_DB_NAME = 4;
    private static final int INDEX_DB_USERNAME = 5;
    private static final int INDEX_DB_PASSWORD = 6;
    private static final String USER_TABLE_NAME = "spacemarinesusers";
    private static final String DATA_TABLE_NAME = "space_marines";

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        LOGGER.trace("the server is running");
        if (args.length == NUMBER_OF_ARGUMENTS) {
            try {
                final InetSocketAddress address = Checker.checkAddress(args[INDEX_HOST], args[INDEX_PORT]);
                LOGGER.info(() -> "set " + address + " address");
                final String dataBaseUrl = "jdbc:postgresql://" + args[INDEX_DB_HOSTNAME] + ":" + args[INDEX_DB_PORT]
                        + "/" + args[INDEX_DB_NAME];
                final String dataBaseUsername = args[INDEX_DB_USERNAME];
                final String dataBasePassword = args[INDEX_DB_PASSWORD];
                try (Connection connection = DriverManager.getConnection(dataBaseUrl, dataBaseUsername, dataBasePassword)) {
                    LOGGER.info("connected to the database");
                    DatagramSocket server = new DatagramSocket(address);
                    SQLManager sqlManager = new SQLManager(connection);
                    SQLUserTableCreator sqlUserTableCreator = new SQLUserTableCreator(connection, USER_TABLE_NAME, LOGGER);
                    SQLUserManager sqlUserManager = new SQLUserManager(sqlUserTableCreator.init(), connection, USER_TABLE_NAME, LOGGER);
                    sqlManager.createTable();
                    CollectionManager collectionManager = new SQLCollectionManager(sqlManager.initCollection(), sqlManager);
                    CommandManager commandManager = new CommandManager(collectionManager);
                    Executor executor = new Executor(commandManager);
                    UsersHandler usersHandler = new UsersHandler(sqlUserManager, commandManager.getRequirements(), LOGGER);
                    Receiver receiver = new Receiver(server, BUFFER_SIZE, LOGGER, executor, usersHandler);
                    while (true) {
                        receiver.receive();
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.error("wrong data from client");
                } catch (IOException | SQLException e) {
                    LOGGER.error(e);
                }
            } catch (IllegalAddressException e) {
                LOGGER.error(e.getMessage());
            }
        } else {
            LOGGER.error("command line arguments must indicate host name, port, database host name, port and name, username and password");
        }
        LOGGER.trace("the server is shutting down");
    }
}
