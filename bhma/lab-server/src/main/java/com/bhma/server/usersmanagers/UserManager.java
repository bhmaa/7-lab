package com.bhma.server.usersmanagers;

import com.bhma.server.util.User;
import java.util.List;

public abstract class UserManager {
    private final List<User> users;

    public UserManager(List<User> users) {
        this.users = users;
    }

    public boolean isUsernameExists(String username) {
        return users.stream().anyMatch(e -> e.getUsername().equals(username));
    }

    public boolean checkPassword(User user) {
        return users.stream().anyMatch(e -> e.equals(user));
    }

    public abstract void registerUser(User user);
}
