package com.ssdiscusskiny.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "data.db";

    public static String COMMENT_STATUS_TABLE ="comments_not_sent";

    public static String ROW_ID = "_id";
    public static String MSG_ID = "message_id";
    public static String MSG_CONTENT = "content";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DB_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String q1 = "CREATE TABLE "+ COMMENT_STATUS_TABLE +" ("+ROW_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+MSG_ID+" TEXT UNIQUE, "+MSG_CONTENT+" TEXT);";
        db.execSQL(q1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
