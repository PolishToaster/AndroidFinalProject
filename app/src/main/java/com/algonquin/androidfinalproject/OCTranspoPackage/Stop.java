package com.algonquin.androidfinalproject.OCTranspoPackage;

import java.util.ArrayList;

public class Stop {
    String stopNumber;
    String stopName;
    ArrayList<String> routeList;

    public Stop(String stopNumber, String stopName, ArrayList<String> routeList) {
        this.stopNumber = stopNumber;
        this.stopName = stopName;
        this.routeList = routeList;
    }

    public String getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(String stopNumber) {
        this.stopNumber = stopNumber;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    @Override
    public String toString() {
        return stopName + " (" + stopNumber + ")";
    }

    public ArrayList<String> getRouteList() {
        return this.routeList;
    }
}
