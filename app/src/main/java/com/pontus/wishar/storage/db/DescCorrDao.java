package com.pontus.wishar.storage.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.pontus.wishar.data.DescCorr;

import java.util.List;

/**
 * Created by NorthBei on 2018/3/25.
 */
@Dao
public interface DescCorrDao {
    @Query("SELECT * FROM DescCorr WHERE ssid = :ssid")
    DescCorr getDescCorr(String ssid);

    @Query("SELECT * FROM DescCorr")
    List<DescCorr> getDescCorrList();
}
