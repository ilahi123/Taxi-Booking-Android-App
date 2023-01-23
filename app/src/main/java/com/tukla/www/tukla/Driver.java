package com.tukla.www.tukla;

public class Driver {

    private String toda;
    private String plateNumber;
    private User user;
    private String updatedAt;

    public Driver(
            User user,
            String toda,
            String plateNumber,
            String updatedAt) {

        this.user = user;
        this.toda = toda;
        this.plateNumber = plateNumber;
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public String getToda() {
        return this.toda;
    }

    public String getPlateNumber() {
        return this.plateNumber;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
