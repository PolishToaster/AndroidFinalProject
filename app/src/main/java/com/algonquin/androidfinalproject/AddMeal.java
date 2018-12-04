package com.algonquin.androidfinalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AddMeal extends AppCompatActivity {

    private ArrayList<String> meals;
    private EditText mealName;
    private Button addBtn;
    private ListView mealsList;
    private NutritionDatabaseHelper db;
    private MealListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
        Toolbar toolbar = findViewById(R.id.nutritionToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        meals = new ArrayList<>();
        mealName = findViewById(R.id.mealName);
        mealsList = findViewById(R.id.mealsList);
        addBtn = findViewById(R.id.addMealBtn);
        db = new NutritionDatabaseHelper(this);
        adapter = new MealListAdapter(this, meals);
        mealsList.setAdapter(adapter);

        addBtn.setOnClickListener(c->{
            if(!mealName.getText().toString().isEmpty()){
                String addMe = mealName.getText().toString();
                db.insert(addMe);
                meals.add(addMe);
                mealName.setText("");
            }
            adapter.notifyDataSetChanged();
        });
        populateMeals();
        mealsList.setOnItemClickListener((a, b, c, d) -> {
            Intent intent = new Intent(AddMeal.this, MealDetail.class);
            intent.putExtra("meal", (String) mealsList.getItemAtPosition(c));
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nutrition_tool_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.search:
                startActivity(new Intent(AddMeal.this, NutritionSearch.class));
                break;
            case R.id.meals:
                Snackbar.make(findViewById(R.id.mealName), R.string.here, Snackbar.LENGTH_LONG).show();
                break;
            case R.id.home:
                startActivity(new Intent(AddMeal.this, MainActivity.class));
                break;
            case R.id.favs:
                startActivity(new Intent(AddMeal.this, FavouritesPage.class));
                break;
            case R.id.about:
                View dialogView = getLayoutInflater().inflate(R.layout.about_dialog, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.about);
                alert.setView(dialogView);
                alert.setPositiveButton("OK", (c,b)->{

                });
                alert.create().show();
                Snackbar.make(findViewById(R.id.mealName), R.string.thanks, Snackbar.LENGTH_LONG).show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void populateMeals(){
        Cursor cursor = db.queryAll(db.MEAL_LIST_TABLE, new String[]{db.KEY_ID, db.MEAL});
        int mealIndex = cursor.getColumnIndex(db.MEAL);
        cursor.moveToFirst();
        for (int i = 0; i<cursor.getCount(); i++ ){
            String meal = cursor.getString(mealIndex);
            meals.add(meal);
            cursor.moveToNext();
        }

    }

    protected class MealListAdapter extends ArrayAdapter<String> {


        public MealListAdapter(Context ctx, ArrayList<String> list){
            super(ctx, 0, list);
        }

        @NonNull
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            String item = getItem(position);
            view = LayoutInflater.from(getContext()).inflate(R.layout.meal_list, null);
            TextView tv = view.findViewById(R.id.mealNameText);
            tv.setText(item);
            return view;
        }
    }
}
