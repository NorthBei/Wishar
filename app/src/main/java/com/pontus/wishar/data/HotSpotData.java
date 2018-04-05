package com.pontus.wishar.data;

/**
 * Created by NorthBei on 2018/3/30.
 */

public class HotSpotData {
    private DescCorr descCorr;
    private boolean hasRecord;

    public HotSpotData(DescCorr descCorr, boolean hasRecord) {
        this.descCorr = descCorr;
        this.hasRecord = hasRecord;
    }

    public String getSSID(){
        return descCorr.getSsid();
    }

    public String getWifiDescName() {
        return descCorr.getWifiDescFileName();
    }

    public boolean hasRecord() {
        return hasRecord;
    }
}
