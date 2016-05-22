package com.example.pawel.cardviewsswipes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pawel on 2016-05-16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Castle.db";
    public static final String TABLE_NAME = "castle_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "CASTLE_NAME";
    public static final String COL_3 = "CASTLE_DESC";
    public static final String COL_4 = "IMAGE";
    public static final String COL_5 = "LAT";
    public static final String COL_6 = "LONG";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 5);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, CASTLE_NAME TEXT, CASTLE_DESC TEXT, IMAGE TEXT, LAT TEXT, LONG TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insert(String castleName, String castleDesc, String imageSrc, String lat, String longi){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, castleName);
        contentValues.put(COL_3, castleDesc);
        contentValues.put(COL_4, imageSrc);
        contentValues.put(COL_5, lat);
        contentValues.put(COL_6, longi);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1) return false;
        else return true;
    }

    public Map<String, String> getElement(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Map<String, String> map = new HashMap<>();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME + " where ID = ?", new String[] {id});
        if(result.getCount() > 0){
            result.moveToFirst();
            map.put("name", result.getString(1));
            map.put("desc", result.getString(2));
            map.put("image", result.getString(3));
            map.put("lat", result.getString(4));
            map.put("long", result.getString(5));
        }
        result.close();
        return map;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME, null);
        return result;
    }

    public Integer delete(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }

    public boolean update(String id, String castleName, String castleDesc, String imageSrc, String lat, String longi){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, castleName);
        contentValues.put(COL_3, castleDesc);
        contentValues.put(COL_4, imageSrc);
        contentValues.put(COL_5, lat);
        contentValues.put(COL_6, longi);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return true;
    }
}
