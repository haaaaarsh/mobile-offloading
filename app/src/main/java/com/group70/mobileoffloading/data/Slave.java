package com.group70.mobileoffloading.data;

import java.io.Serializable;
import java.util.Arrays;

public class Slave implements Serializable {
    public String name;
    public String id;
    public int[][] m1;
    public int[][] m2;
    public int[][] result;
    public boolean comp;
    public double battery;
    public double latitude;
    public double longitude;
    public double curbat;
    public boolean connected;


    public Slave(String name, String id, double battery, double curbat, double latitude, double longitude, int[][] m1, int[][] m2, int[][] result, boolean connected) {
        this.name = name;
        this.id = id;
        this.battery = battery;
        this.curbat = curbat;
        this.latitude = latitude;
        this.longitude = longitude;
        this.m1 = m1;
        this.m2 = m2;
        this.result = result;
        this.connected = connected;

    }
    public String getSlaveResult(){
        String local = "Computed: "+Arrays.deepToString(result)+"\n";
        return  local;
    }
    public String getAllVariables() {return "Server Name: " + name + "\n" + "EndpointID: " + id + "\n" + "Battery: " + battery + "%\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude +"\n"+ "Computed Result: "+Arrays.deepToString(result)+"\n";}
}