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
    public double bat;
    public double lat;
    public double lon;
    public double curbat;
    public boolean connected;


    public Slave(String name, String id, double bat, double curbat, double lat, double lon, int[][] m1, int[][] m2, int[][] result, boolean connected) {
        this.name = name;
        this.id = id;
        this.bat = bat;
        this.curbat = curbat;
        this.lat = lat;
        this.lon = lon;
        this.m1 = m1;
        this.m2 = m2;
        this.result = result;
        this.connected = connected;

    }
    public String getSlaveResult(){
        String local = "Computed: "+Arrays.deepToString(result)+"\n";
        return  local;
    }
    public String getAllVariables() {return "Server Name: " + name + "\n" + "EndpointID: " + id + "\n" + "Battery: " + bat + "%\n" + "Latitude: " + lat + "\n" + "Longitude: " + lon+"\n"+ "Computed Result: "+Arrays.deepToString(result)+"\n";}
}