package com.algonquin.androidfinalproject.Movies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* Said Zaripov*/
public class MovieDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Movies";
    private static final int VERSION_NUM = 1;
    private static final String ACTIVITY_NAME = "MovieDatabaseHelper";
    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_RELEASED = "RELEASE_DATE";
    public static final String KEY_RATING = "RATING";
    public static final String KEY_RUNTIME = "RUNTIME";
    public static final String KEY_PLOT = "PLOT";
    public static final String KEY_STARRING = "STARRING";

    /*
    Public Default Constructor
     */
    public MovieDatabaseHelper(Context ctx){
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(ACTIVITY_NAME, "Calling onCreate()");

        db.execSQL("CREATE TABLE " +DATABASE_NAME +"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_TITLE+" TEXT, "+KEY_RELEASED+" TEXT, "+KEY_RATING+" TEXT, "+KEY_RUNTIME+" TEXT, "+KEY_PLOT+" TEXT, "+KEY_STARRING+" TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(ACTIVITY_NAME, "Calling onUpgrade(), oldVersion="+oldVersion+" newVersion="+newVersion);

        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);
        onCreate(db);
    }
}