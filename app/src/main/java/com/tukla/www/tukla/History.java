package com.tukla.www.tukla;

public class History {

    private Session session;
    private double fare;
    private String updatedAt;

    public History() {

    }

    public History(Session session, double fare, String updatedAt) {
        this.session = session;
        this.fare = fare;
        this.updatedAt = updatedAt;
    }

    public Session getSession() {
        return session;
    }

    public double getFare() {
        return fare;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

}
