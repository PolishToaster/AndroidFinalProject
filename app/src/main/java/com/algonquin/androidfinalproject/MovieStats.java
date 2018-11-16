package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;

public class MovieStats extends Activity {
    MovieDataHelper movieDataHelper;
    ArrayList<String> movieList = new ArrayList<>();
    SQLiteDatabase movieDB;
    Cursor cursor;
    Button movie_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_stats);

        EditText maxValue = (EditText)findViewById(R.id.movie_longest_value);
        EditText minValue = (EditText)findViewById(R.id.movie_shortest_value);
        EditText avgValue = (EditText)findViewById(R.id.movie_avgRuntime_value);
        Button cancelButton = (Button)findViewById(R.id.movie_detail_cancel);
        movieDataHelper = new MovieDataHelper(MovieStats.this);
        movieDB = movieDataHelper.getReadableDatabase();

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        double sum = 0;
        cursor = movieDB.query(MovieDataHelper.TABLE_NAME,new String[]{ MovieDataHelper.KEY_ID, MovieDataHelper.KEY_RUNTIME},null,null,null,null,null);
        cursor.moveToFirst() ;
        while (!cursor.isAfterLast()) {
            double runtime = cursor.getDouble(cursor.getColumnIndex(MovieDataHelper.KEY_RATING));
            max = runtime > max ? runtime:max;
            min = runtime < min ? runtime:min;
            sum += runtime;
            cursor.moveToNext();
        }
        double avg = sum/cursor.getCount();

        maxValue.setText(Double.toString(max));
        minValue.setText(Double.toString(min));
        avgValue.setText(Double.toString(avg));

        movie_return = (Button)findViewById(R.id.movie_return);
        movie_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieStats.this,Movie.class);
                startActivity(intent);
            }
        });
    }





}
