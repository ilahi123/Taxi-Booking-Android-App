package com.tukla.www.tukla;

/**
 * Created by Lenovo on 10/23/2017.
 */

public class Places {

    private String primaryText, addressDescription, distance;


    public Places(){

    }

    //CONSTRACTOR..........
    public Places(String primaryText, String addressDescription, String distance) {
        this.primaryText = primaryText;
        this.addressDescription = addressDescription;
        this.distance = distance;
    }


    public String getPrimaryText(){
        return primaryText;
    }

    public void setPrimaryText(String text1){
        this.primaryText=text1;
    }

    public  String getAddressDescription(){
        return addressDescription;
    }

    public void setAddressDescription(String text2){
        this.addressDescription=text2;
    }

    public String getDistance(){
        return distance;
    }

    public void setDistance(String text3){
        this.distance=text3;
    }




}
