package com.example.tiphaine.myapplication2;

/**
 * Created by olivier on 07/02/17.
 */

public class Station {

    private int number;
    private String contract_name;
    private String name;
    private String address;
    private float lat;
    private float lng;
    private String banking;
    private String bonus;
    private String status;
    private int totalplace;
    private int nbplace;
    private int nbvelo;
    private String lastupdate;



    Station(int number,
            String name,
            String address,
            float lat,
            float lng,
            String banking,
            String bonus,
            int totalplace,
            int nbplace,
            int nbvelo,
            String lastupdate)
    {
        this.number = number;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.banking = banking;
        this.bonus = bonus;
        this.totalplace = totalplace;
        this.nbplace = nbplace;
        this.nbvelo = nbvelo;
        this.lastupdate = lastupdate;
    }



    public float getLat()
    {
        return lat;
    }

    public float getLng()
    {
        return lng;
    }


    public String toString()
    {
        return number + " # " + name + " # " + address + " # " + lat + " | " + lng ;
    }


}
