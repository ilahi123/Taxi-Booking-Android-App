package com.tukla.www.tukla;

public class Admin {

    private User user;
    private String updatedAt;

    public Admin() {

    }

    public Admin(User user, String updatedAt) {
        this.user = user;
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
