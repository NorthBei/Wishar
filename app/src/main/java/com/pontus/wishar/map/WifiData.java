package com.pontus.wishar.map;

public class WifiData {

    protected String name,lng,lat,addr;

    public void setName(String name) {
        this.name = name;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }


    public String getName() {
        return name;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    public String getAddr() {
        return addr;
    }

}
