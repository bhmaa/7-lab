package com.bhma.server.usersmanagers;

import com.bhma.server.util.User;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "users")
public class XMLUserManager extends UserManager {
    @XmlElement(name = "user")
    private List<User> users;
    private String filename;

    public XMLUserManager(List<User> users, String filename) {
        super(users);
        this.users = Collections.synchronizedList(users);
        this.filename = filename;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public void registerUser(User user) {
        users.add(user);
    }
}
