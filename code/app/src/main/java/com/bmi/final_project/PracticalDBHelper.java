package com.bmi.final_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PracticalDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Practical";
    private static final int DATABASE_VERSION = 1;
    public PracticalDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.getReadableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Practical (_id integer  primary key autoincrement,event_name text,category text,start_time text,end_time text,note text) ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Practical  (_id integer  primary key autoincrement,event_name text,category text,start_time text,end_time text,note text)  ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Practical");
        onCreate(db);
    }
}
