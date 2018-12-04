package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.algonquin.androidfinalproject.Movies.MovieActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startOCTranspo = (Button) findViewById(R.id.startOCTranspo);
        startOCTranspo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OCTranspo.class);
                MainActivity.this.startActivity(intent);

            }
        });

        Button movie_button = (Button) findViewById(R.id.startMovie);
        movie_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}