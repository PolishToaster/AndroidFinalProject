package com.algonquin.androidfinalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NutritionDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NUTRITION_DB";
    private static final int VERSION_NUM = 1;


    public NutritionDatabaseHelper (Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    protected final String FAVS_TABLE = "FAVOURITES";
    protected final String HIST_TABLE = "HISTORY";
    protected final String KEY_ID = "ID";
    protected final String FOOD_ID = "FOOD_ID";

    protected final String CREATE_HIST = "CREATE TABLE " +
            HIST_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FOOD_ID + " TEXT NOT NULL);";
    protected final String CREATE_FAVS = "CREATE TABLE " +
            FAVS_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FOOD_ID + " TEXT NOT NULL);";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVS);
        db.execSQL(CREATE_HIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + HIST_TABLE);
        onCreate(db);
    }

    public void insert(SQLiteDatabase db, String table, String foodId){
        ContentValues itemToAdd = new ContentValues();
        itemToAdd.put(FOOD_ID, foodId);
        db.insert(table, "NullColumn", itemToAdd);
    }

    public void delete(SQLiteDatabase db, String table, long id){
        db.delete(table, KEY_ID + " =  " + id, null);
    }
}
