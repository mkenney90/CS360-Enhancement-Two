package com.snhu.weight_tracker;

public class AuthenticatedUserManager {

    private AuthenticatedUser user;
    private static AuthenticatedUserManager instance;

    private AuthenticatedUserManager() {

    }

    public static AuthenticatedUserManager getInstance() {
        if (instance == null) {
            instance = new AuthenticatedUserManager();
        }
        return instance;
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }
}
