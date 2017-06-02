package com.wb.connect.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by sam on 2017/6/2.
 */

public class DbHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "conn.db";
    private static final int DATABASE_VERSION = 1;

    String create_table_upload_sql = "create table if not exists upload "+
            "(id integer primary key AUTOINCREMENT,name varchar,path text)";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        db.execSQL(create_table_upload_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);


    }

    public Cursor getUploads() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"0 _id", "name", "path"};
        String sqlTables = "upload";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null,
                null, null, null);

        c.moveToFirst();
        return c;

    }

    public boolean checkUploadIsExist(String path) {

        boolean isExist=false;
        SQLiteDatabase db = getReadableDatabase();

        ContentValues row = new ContentValues();
        Cursor cur = db.rawQuery("  select id,name,path from upload where path =  ?",
                new String[] { path });

        if (cur.moveToNext()) {
            row.put("id", cur.getString(0));
            row.put("name", cur.getInt(1));
            row.put("path", cur.getInt(2));

            isExist=true;
        }
        cur.close();

        return isExist;

    }

    public void InsertUpload(String fileName,String filePath){

        SQLiteDatabase db = getWritableDatabase();

        String sql = "insert into upload (id,name,path) values (2,'"+fileName+"','"+filePath+"')";
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            Log.i("err", "insert failed");
        }
    }
}
