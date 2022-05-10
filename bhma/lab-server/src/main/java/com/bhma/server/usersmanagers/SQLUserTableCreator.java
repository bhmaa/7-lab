package com.bhma.server.usersmanagers;

import com.bhma.common.util.User;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLUserTableCreator {
    private final Connection connection;
    private final String usersTableName;
    private final Logger logger;

    public SQLUserTableCreator(Connection connection, String usersTableName, Logger logger) {
        this.connection = connection;
        this.usersTableName = usersTableName;
        this.logger = logger;
    }

    private void createUserTable() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + usersTableName + " (username VARCHAR(100) NOT NULL"
                    + " PRIMARY KEY, password VARCHAR(100) NOT NULL)");
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public List<User> init() {
        createUserTable();
        List<User> sqlUsers = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM " + usersTableName);
            while (result.next()) {
                sqlUsers.add(new User(result.getString("username"), result.getString("password")));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info(() -> "added data of " + sqlUsers.size() + " users from the database");
        return sqlUsers;
    }
}
