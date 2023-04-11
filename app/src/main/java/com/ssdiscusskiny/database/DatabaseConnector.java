package com.ssdiscusskiny.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseConnector {

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private Context context;
    public static  int DB_VERSION=1;

    public DatabaseConnector(Context context){
        this.context = context;
        databaseHelper = new DatabaseHelper(this.context, DatabaseHelper.DB_NAME, null,
                DB_VERSION);

    }

    public void open() {
        db = databaseHelper.getWritableDatabase();
    }

    public void close(){
        if (db!=null) db.close();
    }

    public boolean isCommentNotSent(String msgId){
        open();
        String q = "SELECT "+DatabaseHelper.MSG_ID+" FROM "+DatabaseHelper.COMMENT_STATUS_TABLE +" WHERE "+DatabaseHelper.MSG_ID+" =?";
        Cursor mCursor = db.rawQuery(q, new String[]{msgId});
        if (mCursor.getCount()>0) return true;
        else return false;

        //db not closed you will close in call
    }

    public void clearNotSent(String msgId){
        open();
        db.delete(DatabaseHelper.COMMENT_STATUS_TABLE, DatabaseHelper.MSG_ID+" =?", new String[]{msgId});
        close();
    }

    public void insertComment(String msgId, String msgContent){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.MSG_ID, msgId);
        contentValues.put(DatabaseHelper.MSG_CONTENT, msgContent);

        open();
        db.insert(DatabaseHelper.COMMENT_STATUS_TABLE, null, contentValues);
        close();
    }



}
