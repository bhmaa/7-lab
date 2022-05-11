package com.bhma.server;

import com.bhma.common.exceptions.IllegalAddressException;
import com.bhma.common.util.Checker;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bhma.server.collectionmanagers.CollectionManager;
import com.bhma.server.collectionmanagers.SQLCollectionManager;
import com.bhma.server.usersmanagers.SQLUserManager;
import com.bhma.server.usersmanagers.tablecreators.SQLUserTableCreator;
import com.bhma.server.util.CommandManager;
import com.bhma.server.util.Executor;
import com.bhma.server.util.Receiver;
import com.bhma.server.collectionmanagers.datamanagers.SQLDataManager;
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
    private static final String DATA_TABLE_NAME = "spacemarines";
    private static final ExecutorService REQUEST_READING_POOL = Executors.newFixedThreadPool(1);
    private static final ExecutorService REQUEST_PROCESSING_POOL = Executors.newCachedThreadPool();
    private static final ExecutorService RESPONSE_SENDING_POOL = Executors.newCachedThreadPool();

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
                try (Connection connection = DriverManager.getConnection(dataBaseUrl, dataBaseUsername, dataBasePassword);
                     DatagramSocket server = new DatagramSocket(address)) {
                    LOGGER.info(() -> "connected to the database " + dataBaseUrl);
                    LOGGER.info("opened datagram socket");
                    SQLDataManager sqlDataManager = new SQLDataManager(connection, DATA_TABLE_NAME, USER_TABLE_NAME, LOGGER);
                    SQLUserTableCreator sqlUserTableCreator = new SQLUserTableCreator(connection, USER_TABLE_NAME, LOGGER);
                    SQLUserManager sqlUserManager = new SQLUserManager(sqlUserTableCreator.init(), connection, USER_TABLE_NAME, LOGGER);
                    CollectionManager collectionManager = new SQLCollectionManager(sqlDataManager.initCollection(), sqlDataManager);
                    CommandManager commandManager = new CommandManager(collectionManager, LOGGER);
                    Executor executor = new Executor(commandManager);
                    UsersHandler usersHandler = new UsersHandler(sqlUserManager, commandManager.getRequirements(), LOGGER);
                    Receiver receiver = new Receiver(server, BUFFER_SIZE, LOGGER, executor, usersHandler);
                    receiver.start(REQUEST_READING_POOL, REQUEST_PROCESSING_POOL, RESPONSE_SENDING_POOL);
                } catch (IOException | SQLException | ExecutionException | InterruptedException e) {
                    LOGGER.error(e);
                }
            } catch (IllegalAddressException e) {
                LOGGER.error(e.getMessage());
            } finally {
                REQUEST_READING_POOL.shutdown();
                REQUEST_PROCESSING_POOL.shutdown();
                RESPONSE_SENDING_POOL.shutdown();
            }
        } else {
            LOGGER.error("command line arguments must indicate host name, port, database host name, port and name, username and password");
        }
        LOGGER.trace("the server is shutting down");
    }
}
