package com.ahmet.iphonewallpaper.Database.DataSource.Interface;


import com.ahmet.iphonewallpaper.Database.DataSource.Model.Recents;

import java.util.List;

import io.reactivex.Flowable;

public interface IRecentsDataSource {

    Flowable<List<Recents>> getAllRecents();

    void insertRecents(Recents... recents);

    void updateRecents(Recents... recents);

    void deleteRecents(Recents... recents);

    void deleteAllRecents();

}
