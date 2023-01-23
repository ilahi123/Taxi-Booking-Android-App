package com.tukla.www.tukla;

import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDateTime;

public class Booking {

    private String UuID;
    private String requestAt;
    private LatLngDefined origin;
    private LatLngDefined destination;
    private Boolean isAccepted;
    private Boolean isArrived;
    private double fare;
    private double distance;
    private String originText;
    private String destinationText;
    private User user;
    private User driver;
    private LatLngDefined driverLocation;

    public Booking() {

    }

    public Booking(
            //String UuID,
            User user,
            User driver,
            String requestAt,
            LatLngDefined origin,
            LatLngDefined destination,
            Boolean isAccepted,
            Boolean isArrived,
            double fare,
            double distance,
            String originText,
            String destinationText,
            LatLngDefined driverLocation) {

        //this.UuID = UuID;
        this.user = user;
        this.driver = driver;
        this.requestAt = requestAt;
        this.origin = origin;
        this.destination = destination;
        this.isAccepted = isAccepted;
        this.isArrived = isArrived;
        this.distance = distance;
        this.fare = fare;
        this.destinationText = destinationText;
        this.originText = originText;
        this.driverLocation = driverLocation;
    }

//    public String getUuID() {
//        return UuID;
//    }

    public User getUser() {
        return user;
    }

    public User getDriver() {
        return driver;
    }

    public String getRequestAt() {
        return requestAt;
    }

    public LatLngDefined getOrigin() {
        return origin;
    }

    public LatLngDefined getDestination() {
        return destination;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public Boolean getIsArrived() {
        return isArrived;
    }

    public double getFare() {
        return fare;
    }

    public double getDistance() {
        return distance;
    }

    public String getDestinationText() {
        return destinationText;
    }

    public String getOriginText() {
        return originText;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public void setIsAccepted(Boolean status) {
        this.isAccepted = status;
    }

    public void setIsArrived(Boolean status) {
        this.isArrived = status;
    }

}
