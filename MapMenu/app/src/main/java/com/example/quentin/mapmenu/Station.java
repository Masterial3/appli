package com.example.quentin.mapmenu;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
    private MarkerOptions marker;



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
        this.marker = new MarkerOptions().position(new LatLng(this.lat, this.lng));
    }



    public float getLat()
    {
        return lat;
    }

    public float getLng()
    {
        return lng;
    }

    public MarkerOptions getMarker(){ return marker; }

    public String getName(){ return name;}
    public String getAddress(){return address;}
    public int getNbvelo(){return nbvelo;}
    public int getNbplace(){return nbplace;}

    public String toString()
    {
        return number + " # " + name + " # " + address + " # " + lat + " | " + lng ;
    }


}
