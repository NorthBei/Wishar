package com.pontus.wishar.data;

import java.util.ArrayList;
import java.util.List;

public class HotSpot {

    private String ssid;
    private String type;
    private List<HotSpotOption> category = new ArrayList<>();

    public String getSsid() {
        return ssid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public List<HotSpotOption> getCategory() {
        return category;
    }

    public void setCategory(List<HotSpotOption> category) {
        this.category = category;
    }
}