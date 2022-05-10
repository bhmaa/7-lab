package com.bhma.common.util;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement(name = "spaceMarine")
@XmlType(propOrder = {"username", "hashPassword"})
public class User implements Serializable {
    private final String username;
    private final String hashPassword;

    public User(String username, String hashPassword) {
        this.username = username;
        this.hashPassword = hashPassword;
    }

    @XmlElement
    public String getUsername() {
        return username;
    }

    @XmlElement
    public String getHashPassword() {
        return hashPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(hashPassword, user.hashPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, hashPassword);
    }
}
