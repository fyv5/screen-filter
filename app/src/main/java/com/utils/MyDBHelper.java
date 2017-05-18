package com.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fy on 2017/5/17.
 */

public class MyDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "vision";
//    String sql = "create table if not exists " + TABLE_NAME +
//            " (Id integer primary key, time, leftVision, rightVision)";

    String sql =
            "create table vision(_id integer primary key autoincrement, " +
                    "date, " +
                    "leftVision, " +
                    "rightVision)";


    public MyDBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

}
