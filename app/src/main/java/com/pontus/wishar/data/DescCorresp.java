package com.pontus.wishar.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by NorthBei on 2018/3/25.
 */
@Entity(tableName = "DescCorresp")
public class DescCorresp {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "ssid")
    private final String ssid;

    @NonNull
    @ColumnInfo(name = "wifi_desc")
    private final String wifiDescFileName;

    public DescCorresp(@NonNull String ssid,@NonNull String wifiDescFileName) {
        this.ssid = ssid;
        this.wifiDescFileName = wifiDescFileName;
    }

    @NonNull
    public String getSsid() {
        return ssid;
    }

    @NonNull
    public String getWifiDescFileName() {
        return wifiDescFileName;
    }

    @Override
    public String toString(){
        return String.format("{ssid:%s,wifi_desc:%s}",ssid,wifiDescFileName);
    }
}
