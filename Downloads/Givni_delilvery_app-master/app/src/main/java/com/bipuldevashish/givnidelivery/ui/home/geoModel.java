package com.bipuldevashish.givnidelivery.ui.home;

import com.google.firebase.firestore.GeoPoint;

public class geoModel {

    private GeoPoint geoPoint;
    private String name,mapAddress,houseName,landMark;




    public geoModel(GeoPoint geoPoint, String name, String mapAddress, String houseName, String landMark) {
        this.geoPoint = geoPoint;
        this.name = name;
        this.mapAddress = mapAddress;
        this.houseName = houseName;
        this.landMark = landMark;
    }

    public geoModel() {
    }


    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapAddress() {
        return mapAddress;
    }

    public void setMapAddress(String mapAddress) {
        this.mapAddress = mapAddress;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getLandMark() {
        return landMark;
    }

    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }
}
