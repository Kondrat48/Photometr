package com.example.artem.photometr;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by artem on 18.07.2017.прпе
 */


public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    public static String DATABASE_NAME;

    DBHelper(final Context context, String databaseName)
    {
        super(new DatabaseContext(context), databaseName, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table photometer_analyse (id integer primary key, element text, measurement text, hundred_percent text, percent text,kg_on_ha text, db text)");
        db.execSQL("create table farm_info (id integer primary key, farm_name text, field_number text, crop text, area_of_field text, predecessor text, note text)");
        db.execSQL("create table analise (id integer primary key, ph text, humus text, n text, p text, k text, s text, ca text, mg text, b text, cu text, zn text, mn text, fe text, mo text, co text, j text)");
        db.execSQL("create table fertilization (id integer primary key, n text, p2o5 text, k2o text, s text, calx text, gypsum text, manure text)");
        db.execSQL("create table top_dressing (id integer primary key, n text, p text, k text, s text, ca text, mg text, b text, cu text, zn text, mn text, fe text, mo text, co text, j text)");
        for (int i = 0; i <= 18;i++) insertPhotometerAnalyse(i);
        insertFarmInfo();
        insertAnalise();
        for (int i = 0; i <= 2;i++) insertFertilization();
        for (int i = 0; i <= 3;i++) insertTopDressing();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Cursor getData(int id, String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + table + " where id = "+id+"", null );
        return res;
    }

    private boolean insertPhotometerAnalyse(int i){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("k1", (String) null);
        cv.put("n", (String) null);
        cv.put("p", (String) null);
        cv.put("k", (String) null);
        cv.put("s", (String) null);
        cv.put("ca", (String) null);
        cv.put("mg", (String) null);
        cv.put("b", (String) null);
        cv.put("cu", (String) null);
        cv.put("zn", (String) null);
        cv.put("mn", (String) null);
        cv.put("fe", (String) null);
        cv.put("mo", (String) null);
        cv.put("co", (String) null);
        cv.put("j", (String) null);
        db.insert("photometer_analyse", null, cv);
        return true;
    }

    private boolean insertFarmInfo(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("farm_name", (String) null);
        cv.put("field_number", (String) null);
        cv.put("crop", (String) null);
        cv.put("area_of_field", (String) null);
        cv.put("predecessor", (String) null);
        cv.put("note", (String) null);
        db.insert("farm_info", null, cv);
        return true;
    }

    private boolean insertAnalise(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ph", (String) null);
        cv.put("humus", (String) null);
        cv.put("n", (String) null);
        cv.put("p", (String) null);
        cv.put("k", (String) null);
        cv.put("s", (String) null);
        cv.put("ca", (String) null);
        cv.put("mg", (String) null);
        cv.put("b", (String) null);
        cv.put("cu", (String) null);
        cv.put("zn", (String) null);
        cv.put("mn", (String) null);
        cv.put("fe", (String) null);
        cv.put("mo", (String) null);
        cv.put("co", (String) null);
        cv.put("j", (String) null);
        db.insert("analise", null, cv);
        return true;
    }

    private boolean insertFertilization(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("n", (String) null);
        cv.put("p2o5", (String) null);
        cv.put("k2o", (String) null);
        cv.put("s", (String) null);
        cv.put("calx", (String) null);
        cv.put("gypsum", (String) null);
        cv.put("manure", (String) null);
        db.insert("fertilization", null, cv);
        return true;
    }

    private boolean insertTopDressing(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("n", (String) null);
        cv.put("p", (String) null);
        cv.put("k", (String) null);
        cv.put("s", (String) null);
        cv.put("ca", (String) null);
        cv.put("mg", (String) null);
        cv.put("b", (String) null);
        cv.put("cu", (String) null);
        cv.put("zn", (String) null);
        cv.put("mn", (String) null);
        cv.put("fe", (String) null);
        cv.put("mo", (String) null);
        cv.put("co", (String) null);
        cv.put("j", (String) null);
        db.insert("top_dressing", null, cv);
        return true;
    }

    public boolean updateFarmInfo (String farm_name, String field_number, String crop, String area_of_field, String predecessor, String note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("farm_name", farm_name);
        cv.put("field_number", field_number);
        cv.put("crop", crop);
        cv.put("area_of_field", area_of_field);
        cv.put("predecessor", predecessor);
        cv.put("note", note);
        db.update("farm_info", cv, "id = ? ", new String[] { Integer.toString(1) } );
        return true;
    }
}

