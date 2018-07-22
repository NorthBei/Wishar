package com.pontus.wishar.storage.db;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(tableName = "WifiMap",primaryKeys={"HashId"})
public class Wifi {
    @NonNull
    public String HashId;

    public String WifiData;
}
