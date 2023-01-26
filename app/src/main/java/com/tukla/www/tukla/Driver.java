package com.tukla.www.tukla;

public class Driver {

    private String toda;
    private String plateNumber;

    public Driver() {

    }

    public Driver(
            String toda,
            String plateNumber) {

        this.toda = toda;
        this.plateNumber = plateNumber;
    }

    public String getToda() {
        return this.toda;
    }

    public String getPlateNumber() {
        return this.plateNumber;
    }

}
