package com.algonquin.androidfinalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OCTranspoDatabaseHelper extends SQLiteOpenHelper {

    private static String ACTIVITY_NAME = "OCTranspoDatabaseHelper";
    private static String DATABASE_NAME = "OCTranspoDatabase";
    private static String STOP_TABLE_NAME = "StopTable";
    private static String ROUTE_TABLE_NAME = "RouteTable";
    private static String KEY_STOP_ID = "StopID";
    private static String KEY_STOP_NAME = "StopName";
    private static String KEY_ROUTE_NUMBER = "RouteNumber";
    private static int VERSION_NUM = 1;

    public OCTranspoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(ACTIVITY_NAME, "Calling onCreate");
        db.execSQL( "CREATE TABLE " + STOP_TABLE_NAME + " (" +
                    KEY_STOP_ID + " TEXT PRIMARY KEY, " +
                    KEY_STOP_NAME + " TEXT)");
        db.execSQL( "CREATE TABLE " + ROUTE_TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY, " +
                    KEY_STOP_ID + " TEXT, " +
                    KEY_ROUTE_NUMBER + " TEXT, " +
                "UNIQUE(" + KEY_STOP_ID + ", " + KEY_ROUTE_NUMBER + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + STOP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ROUTE_TABLE_NAME);
        onCreate(db);
    }

    public static String getActivityName() {
        return ACTIVITY_NAME;
    }

    public static String getStopTableName() {
        return STOP_TABLE_NAME;
    }

    public static String getRouteTableName() {
        return ROUTE_TABLE_NAME;
    }

    public static String getKeyStopId() {
        return KEY_STOP_ID;
    }

    public static String getKeyStopName() {
        return KEY_STOP_NAME;
    }

    public static String getKeyRouteNumber() {
        return KEY_ROUTE_NUMBER;
    }

    public static int getVersionNum() {
        return VERSION_NUM;
    }
}
