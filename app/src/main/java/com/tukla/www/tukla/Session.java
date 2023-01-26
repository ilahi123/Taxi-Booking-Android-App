package com.tukla.www.tukla;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Session {

    private User driver;
    private Booking booking;
    private String startedAt;
    private LatLngDefined driverLocation;
    private Boolean isDriverArrived;
    private Boolean isDone;

    public Session() {

    }

    public Session(User driver, Booking booking, String startedAt, LatLngDefined driverLocation, Boolean isDriverArrived, Boolean isDone) {
        this.driver = driver;
        this.booking = booking;
        this.startedAt = startedAt;
        this.driverLocation = driverLocation;
        this.isDriverArrived = isDriverArrived;
        this.isDone = isDone;
    }

    public User getDriver() {
        return driver;
    }

    public Booking getBooking() {
        return booking;
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

    public Boolean getIsDone() {
        return isDone;
    }
}
