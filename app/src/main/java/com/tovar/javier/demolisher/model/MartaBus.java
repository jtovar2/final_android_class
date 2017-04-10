package com.tovar.javier.demolisher.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by javier on 4/9/17.
 */

public class MartaBus implements Serializable
{

    @SerializedName("ROUTE")
    String route;
    @SerializedName("LATITUDE")
    String latitude;
    @SerializedName("LONGITUDE")
    String longitude;
    @SerializedName("TIMEPOINT")
    String timepoint;

    public String getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(String timepoint) {
        this.timepoint = timepoint;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    MartaBus(){}


    public Double getNumLongitude()
    {
        return Double.parseDouble(longitude);
    }

    public Double getNumLatitude()
    {
        return Double.parseDouble(latitude);
    }
}
