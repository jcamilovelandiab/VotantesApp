package com.example.votoapp.model.entities;

public class User {

    String user;
    String password;
    Role role;

    public User(String user, String password, Role role) {
        this.user = user;
        this.password = password;
        this.role = role;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
