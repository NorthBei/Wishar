package com.pontus.wishar.storage.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.pontus.wishar.data.DescCorresp;

/**
 * Created by NorthBei on 2018/3/25.
 */
@Dao
public interface DescCorrespDao {
    @Query("SELECT * FROM DescCorresp WHERE ssid = :ssid")
    DescCorresp getDescCorresp(String ssid);
}
