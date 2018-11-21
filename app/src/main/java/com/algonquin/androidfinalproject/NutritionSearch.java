package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class NutritionSearch extends Activity {

    private NutritionListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_search);

        adapter = new NutritionListAdapter(this);
        ListView list = findViewById(R.id.searchList);
        list.setAdapter(adapter);

        if (getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            String searched = extras.getString("search");
            adapter.foodName.add(searched);
        } else {
            adapter.foodName.add("test");
            adapter.foodName.add("test2");
            adapter.notifyDataSetChanged();
            adapter.foodName.add("test3");
            adapter.notifyDataSetChanged();
        }



    }
}
