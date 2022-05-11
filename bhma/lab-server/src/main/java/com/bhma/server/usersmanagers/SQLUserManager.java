package com.bhma.server.usersmanagers;

import com.bhma.server.util.PasswordEncoder;
import com.bhma.server.util.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class SQLUserManager extends UserManager {
    private final List<User> users;
    private final Connection connection;
    private final String usersTableName;
    private final Logger logger;

    public SQLUserManager(List<User> users, Connection connection, String usersTableName, Logger logger) {
        super(users);
        this.users = users;
        this.connection = connection;
        this.usersTableName = usersTableName;
        this.logger = logger;
    }

    @Override
    public void registerUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + usersTableName
                    + "(username,password) VALUES (?,?)");
            statement.setString(1, user.getUsername());
            statement.setString(2, PasswordEncoder.encode(user.getPassword()));
            statement.execute();
            users.add(user);
        } catch (SQLException e) {
            logger.error(e);
        }
    }
}
