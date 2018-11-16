package com.algonquin.androidfinalproject;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDataHelper extends SQLiteOpenHelper{
    private static String DATABASE_NAME = "Movie.db";
    private static int VERSION_NUM = 1;
    public static final String KEY_ID = "ID";
    public static final String KEY_TITLE = "Title";
    public static final String KEY_ACTOR= "Actors";
    public static final String KEY_YEAR = "Year";
    public static final String KEY_RUNTIME = "Runtime";
    public static final String KEY_RATING = "Rating";
    public static final String KEY_PLOT = "Plot";
    public static final String KEY_URL = "URL";
    public static final String TABLE_NAME = "MovieTable";

    private static final String DATABASE_CREATE = "create table " + TABLE_NAME
            + "( " + KEY_ID + " integer primary key autoincrement, "
            + KEY_TITLE  + " text ,"
            + KEY_ACTOR + " text ,"
            + KEY_YEAR + " integer ,"
            + KEY_RUNTIME + " text ,"
            + KEY_RATING + " DOUBLE ,"
            + KEY_PLOT + " text ,"
            + KEY_URL + " text );";

    public MovieDataHelper(Context ctx){
        super(ctx,DATABASE_NAME,null,VERSION_NUM);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
