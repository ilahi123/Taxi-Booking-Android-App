package com.tukla.www.tukla;

public class Session {

    private String driverID;
    private String userID;
    private String bookingID;
    private String startedAt;
    private LatLngDefined driverLocation;
    private Boolean isDriverArrived;

    public Session() {

    }

    public Session(String driverID, String userID, String bookingID, String startedAt, LatLngDefined driverLocation, Boolean isDriverArrived) {
        this.driverID = driverID;
        this.userID = userID;
        this.bookingID = bookingID;
        this.startedAt = startedAt;
        this.driverLocation = driverLocation;
        this.isDriverArrived = isDriverArrived;
    }

    public String getDriverID() {
        return this.driverID;
    }

    public String getBookingID() {
        return bookingID;
    }

    public Boolean getIsDriverArrived() {
        return isDriverArrived;
    }

    public LatLngDefined getDriverLocation() {
        return driverLocation;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public String getUserID() {
        return userID;
    }
}
