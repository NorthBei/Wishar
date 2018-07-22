package com.pontus.wishar.storage.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.huma.room_for_asset.RoomAsset;
import com.pontus.wishar.data.DescCorr;

/**
 * Created by NorthBei on 2018/3/25.
 */

@Database(entities = {DescCorr.class,Wifi.class}, version = 2)
public abstract  class WisharDB extends RoomDatabase {
    public abstract DescCorrDao descCorrespDao();
    public abstract MapDao mapDao();

    private static WisharDB db;


    public synchronized static WisharDB getDB(Context context) {
        if(db== null){
            db= RoomAsset.databaseBuilder(context.getApplicationContext(), WisharDB.class, "wishar.db").allowMainThreadQueries().build();
        }
        return db;
    }





}

