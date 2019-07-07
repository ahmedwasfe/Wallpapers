package com.ahmet.iphonewallpaper.Database.SqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.ahmet.iphonewallpaper.Model.WallpaperItem;

import java.util.ArrayList;


/**
 * Created by ahmet on 7/23/2017.
 */

public class FavoriteDatabaseSQLite extends SQLiteOpenHelper {

    private static final String DB_Name = "Wallpapers";
    private static final int DB_Version = 4;

    private static final String Table_NAME = "wallpaper";

    private static final String KEY_CATEGORYID = "ID";
    private static final String KEY_IMAGE = "imageUrl";


    public FavoriteDatabaseSQLite(Context context) {
        super(context, DB_Name, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String create_Table = "create table "+Table_NAME+" ("+KEY_CATEGORYID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +KEY_IMAGE+" varchar(150) )";
        sqLiteDatabase.execSQL(create_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        String delete_query = "DROP table "+Table_NAME+" IF EXITS";
        sqLiteDatabase.execSQL(delete_query);

        onCreate(sqLiteDatabase);
    }

    public void addData(WallpaperItem wallpaperItem){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_IMAGE, wallpaperItem.getImageUrl());

        db.insert(Table_NAME, null, values);
    }

    public ArrayList<WallpaperItem> getAllData(){

        ArrayList<WallpaperItem> favoriteArrayList = new ArrayList<>();

        String select_query = "select * from "+Table_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()){

            do{
                int imageID = cursor.getInt(cursor.getColumnIndex(KEY_CATEGORYID));
                String imageUrl = cursor.getString(cursor.getColumnIndex(KEY_IMAGE));

                WallpaperItem wallpaperItem = new WallpaperItem(imageID, imageUrl);
                favoriteArrayList.add(wallpaperItem);

            }while(cursor.moveToNext());
        }
        return favoriteArrayList;
    }

    // Mothed one get Contact by Id in Database


    //Methoed one Delete Data
    public int deleteData(WallpaperItem wallpaperItem){
        SQLiteDatabase db =this.getWritableDatabase();
        return db.delete(Table_NAME, "id=?", new String[]{String.valueOf(wallpaperItem.getImageID())});
    }


    public void deleteData(int id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from "+Table_NAME+" where id = "+id);
        sqLiteDatabase.close();
    }

    public void deleteAll(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from "+Table_NAME+"");
        sqLiteDatabase.close();
    }

    public int checkData(String Title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery("select * from "+Table_NAME+" Where "+KEY_IMAGE+" Like'" + Title + "'", null);
        int count = rs.getCount();
        return count;
    }
}
