package com.ahmet.iphonewallpaper.Database.DataSource.LocalDatabase;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.ahmet.iphonewallpaper.Database.DataSource.Interface.RecentsDAO;
import com.ahmet.iphonewallpaper.Database.DataSource.Model.Recents;

import static com.ahmet.iphonewallpaper.Database.DataSource.LocalDatabase.LocalDataBase.DATABASE_VERSION;

@Database(entities = Recents.class, version = DATABASE_VERSION)
public abstract class LocalDataBase extends RoomDatabase {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "AHMETWallpaper";

    public abstract RecentsDAO recentsDAO();


    public static RoomDatabase instance;

    public static RoomDatabase getInstance(Context context){

        if (instance == null){
            instance = Room.databaseBuilder(context, LocalDataBase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
