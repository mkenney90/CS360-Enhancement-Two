package com.snhu.weight_tracker;

import java.io.Serializable;

public class AuthenticatedUser implements Serializable {

    private int _id;
    private String username;
    private String password;

    public AuthenticatedUser() {

    }
    public AuthenticatedUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
