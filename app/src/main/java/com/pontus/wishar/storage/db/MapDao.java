package com.pontus.wishar.storage.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MapDao {
    @Delete
    public void deleteUsers(Wifi... wifi);

    @Query("SELECT * FROM WifiMap")
    public Wifi[] loadAllwifis();

    @Query("SELECT * FROM WifiMap WHERE HashId LIKE :geohash")
    public Wifi[] loadAreawifis(String geohash);

    @Update
    public void updateUsers(Wifi... wifi);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUsers(Wifi... wifi);

    @Insert
    public void insertBothUsers(Wifi user1, Wifi wifi2);

    @Insert
    public void insertUsersAndFriends(Wifi user, List<Wifi> wifi);
}
