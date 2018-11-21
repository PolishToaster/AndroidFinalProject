package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FavouritesPage extends Activity {

    private NutritionListAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_page);
        adapter = new NutritionListAdapter(this);
        ListView list = findViewById(R.id.favsList);
        list.setAdapter(adapter);
        adapter.foodName.add("test");
        adapter.notifyDataSetChanged();
    }




}
