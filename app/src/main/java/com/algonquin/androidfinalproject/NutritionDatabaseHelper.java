package com.algonquin.androidfinalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class NutritionDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NUTRITION_DB";
    private static final int VERSION_NUM = 8;
    private SQLiteDatabase db;
    private HashMap<String, String> favsList;


    public NutritionDatabaseHelper (Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
        db = this.getWritableDatabase();
    }

    protected static final String FAVS_TABLE = "FAVOURITES";
    protected static final String HIST_TABLE = "HISTORY";
    protected static final String MEAL_TABLE = "MEALS";
    protected static final String MEAL_LIST_TABLE = "MEALS_LIST";
    protected static final String KEY_ID = "ID";
    protected static final String FOOD_NICK = "FOOD_NICKNAME";
    protected static final String FOOD_ID = "FOOD_ID";
    protected static final String MEAL = "MEAL_NAME";
    protected static final String FKEY_ID = "FOOD_ID_KEY";
    protected static final String CALORIES = "CALORIES";
    protected static final String SEARCHED = "SEARCHED";


    protected final String CREATE_FAVS = "CREATE TABLE " +
            FAVS_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FOOD_NICK + " TEXT NOT NULL, " + FOOD_ID + " TEXT NOT NULL);";
    protected final String CREATE_HIST = "CREATE TABLE " +
            HIST_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SEARCHED + " TEXT NOT NULL);";
    protected final String CREATE_MEAL = "CREATE TABLE " +
            MEAL_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FKEY_ID + " INTEGER NOT NULL, " +
            MEAL + " INTEGER NOT NULL, " + CALORIES + " REAL);";
    protected final String CREATE_MEAL_LIST = "CREATE TABLE " +
            MEAL_LIST_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MEAL + " TEXT NOT NULL);";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVS);
        db.execSQL(CREATE_HIST);
        db.execSQL(CREATE_MEAL);
        db.execSQL(CREATE_MEAL_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + HIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MEAL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MEAL_LIST_TABLE);
        onCreate(db);
    }

    /**
     * <p>Insert item into Favourites</p>
     *
     * @param foodId
     * @param nickname
     */
    public void insert(String foodId, String nickname){
        ContentValues itemToAdd = new ContentValues();
        itemToAdd.put(FOOD_ID, foodId);
        itemToAdd.put(FOOD_NICK, nickname);
        db.insert(FAVS_TABLE, null, itemToAdd);
    }

    public void insert(int mealId, int foodId, double kCal){
        ContentValues itemToAdd = new ContentValues();
        itemToAdd.put(FKEY_ID, foodId);
        itemToAdd.put(MEAL, mealId);
        itemToAdd.put(CALORIES, kCal);
        db.insert(MEAL_TABLE, null, itemToAdd);
    }

    public void insert(String meal){
        ContentValues itemToAdd = new ContentValues();
        itemToAdd.put(MEAL, meal);
        db.insert(MEAL_LIST_TABLE, null, itemToAdd);
    }

    public void delete(long id){
        db.delete(HIST_TABLE, KEY_ID + " =  " + id, null);
    }

    public void deleteThis(String foodID){
        db.delete(FAVS_TABLE, FOOD_ID + " = '" + foodID + "';", null);
    }
    public void deleteMeal(String meal){
        Cursor cursor = db.query(MEAL_LIST_TABLE, null, MEAL + " = '" + meal + "'",null,null,null,null);
        cursor.moveToFirst();
        int i = cursor.getInt(cursor.getColumnIndex(KEY_ID));
        db.delete(MEAL_LIST_TABLE, MEAL + " = '" + meal + "'", null);
        db.delete(MEAL_TABLE, MEAL + " = " + i, null);
    }

    public void clearTable(String table){
        db.delete(table, null, null);
    }

    public Cursor queryAll(String table, String[] columns){
        return db.query(table, columns, null, null, null, null, null, null);
    }
    public Cursor query(String table, String column, String where){
        return db.query(table, null, column + " = " + "'" + where + "'", null, null, null, null);
    }
    public Cursor calculate(String calculation, String meal){
        Cursor cursor = db.query(MEAL_LIST_TABLE, null, MEAL + " = '" + meal + "'",null,null,null,null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(KEY_ID);
        int fKey = cursor.getInt(index);
        return db.rawQuery("SELECT " + calculation + "(" + CALORIES + ") AS 'CALC' FROM " + MEAL_TABLE + " WHERE " + MEAL + " = " + fKey, null);
    }
}
