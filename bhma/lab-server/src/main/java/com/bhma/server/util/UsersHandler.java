package com.bhma.server.util;

import com.bhma.common.util.CommandRequirement;
import com.bhma.common.util.PullingRequest;
import com.bhma.common.util.PullingResponse;
import com.bhma.common.util.RegistrationCode;
import com.bhma.common.util.User;

import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class UsersHandler {
    private final SQLManager sqlManager;
    private final HashMap<String, CommandRequirement> commands;
    private final Logger logger;

    public UsersHandler(SQLManager sqlManager, HashMap<String, CommandRequirement> commands, Logger logger) {
        this.sqlManager = sqlManager;
        this.commands = commands;
        this.logger = logger;
    }

    public PullingResponse handle(PullingRequest request) {
        User newUser = request.getUser();
        if (sqlManager.isUsernameExist(newUser.getUsername())) {
            if (sqlManager.checkPassword(newUser)) {
                logger.info(() -> "user " + newUser.getUsername() + " authorized");
                return new PullingResponse(commands, RegistrationCode.AUTHORIZED);
            } else {
                logger.info("failed login attempt");
                return new PullingResponse(RegistrationCode.DENIED);
            }
        } else {
            sqlManager.registerUser(newUser);
            logger.info(() -> "user " + newUser.getUsername() + " registered");
            return new PullingResponse(commands, RegistrationCode.REGISTERED);
        }
    }

    public boolean checkUser(User user) {
        return sqlManager.isUsernameExist(user.getUsername()) && sqlManager.checkPassword(user);
    }
}
