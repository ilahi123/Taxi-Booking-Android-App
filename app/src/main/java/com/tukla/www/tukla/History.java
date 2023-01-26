package com.tukla.www.tukla;

public class History {

    private Session session;
    private double fare;
    private String updatedAt;
    private String feedback;

    public History() {

    }

    public History(Session session, double fare, String feedback, String updatedAt) {
        this.session = session;
        this.fare = fare;
        this.updatedAt = updatedAt;
        this.feedback = feedback;
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

    public String getFeedback() {
        return feedback;
    }
}
