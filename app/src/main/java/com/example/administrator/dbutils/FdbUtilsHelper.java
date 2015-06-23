package com.example.administrator.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jf on 2015/6/22.
 */
public class FdbUtilsHelper extends SQLiteOpenHelper {

    String[] fleidname={"tablename"};

    public static String ALLTABLE="Alltable";

    @Override
    public void onCreate(SQLiteDatabase db) {

        StringBuffer sb = new StringBuffer();
        sb.append("create table ");
        sb.append("Alltable");
        sb.append(" (_id integer primary key autoincrement");
        for (int i = 0; i < fleidname.length; i++) {
            sb.append("," + fleidname[i] + " varchar(30)");
        }
        sb.append(")");
       // System.out.println(sb.toString());
        db.execSQL(sb.toString());
    }

    public FdbUtilsHelper(Context context) {
        super(context, "mydb.db", null, 1);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
