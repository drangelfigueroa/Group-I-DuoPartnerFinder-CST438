package com.example.duofinder.DB.POJO;

import java.util.Objects;

public class User {
    public String username;
    public String email;
    public boolean isAdmin;

    public User(){}

    public User(String username, String email, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isAdmin == user.isAdmin &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, isAdmin);
    }
}
