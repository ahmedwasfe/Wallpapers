package com.ahmet.iphonewallpaper.Database.DataSource.Interface;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ahmet.iphonewallpaper.Database.DataSource.Model.Recents;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface RecentsDAO {

    @Query("SELECT * FROM recents ORDER BY saveTime DESC LIMIT 500")
    Flowable<List<Recents>> getAllRecents();

    @Insert
    void insertRecents(Recents... recents);

    @Update
    void updateRecents(Recents... recents);

    @Delete
    void deleteRecents(Recents... recents);

    @Query("DELETE FROM recents")
    void deleteAllRecents();
}
