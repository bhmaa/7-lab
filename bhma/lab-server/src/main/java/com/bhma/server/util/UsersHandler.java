package com.bhma.server.util;

import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.PullingRequest;
import com.bhma.common.util.PullingResponse;
import com.bhma.common.util.RegistrationCode;

import com.bhma.server.usersmanagers.SQLUserManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class UsersHandler {
    private final SQLUserManager sqlUserManager;
    private final HashMap<String, CommandRequirement> commands;
    private final Logger logger;

    public UsersHandler(SQLUserManager sqlUserManager, HashMap<String, CommandRequirement> commands, Logger logger) {
        this.sqlUserManager = sqlUserManager;
        this.commands = commands;
        this.logger = logger;
    }

    public PullingResponse handle(PullingRequest request) {
        User newUser = new User(request.getUsername(), PasswordEncoder.encode(request.getPassword()));
        if (sqlUserManager.isUsernameExists(newUser.getUsername())) {
            if (sqlUserManager.checkPassword(newUser)) {
                logger.info(() -> "user " + newUser.getUsername() + " authorized");
                return new PullingResponse(commands, RegistrationCode.AUTHORIZED);
            } else {
                logger.info("failed login attempt");
                return new PullingResponse(RegistrationCode.DENIED);
            }
        } else {
            sqlUserManager.registerUser(newUser);
            logger.info(() -> "user " + newUser.getUsername() + " registered");
            return new PullingResponse(commands, RegistrationCode.REGISTERED);
        }
    }

    public boolean checkUser(User user) {
        if (!sqlUserManager.isUsernameExists(user.getUsername())) {
            return false;
        }
        return sqlUserManager.checkPassword(user);
    }
}
