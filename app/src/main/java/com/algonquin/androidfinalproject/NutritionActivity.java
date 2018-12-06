package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
<div>Icons made by <a href="https://www.freepik.com/" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/"
    title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/"
    title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
 */
public class NutritionActivity extends AppCompatActivity {

    protected String searchItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);
        Toolbar toolbar = findViewById(R.id.nutritionToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button searchButton = findViewById(R.id.searchButton);
        Button favsButton = findViewById(R.id.favsButton);
        Button mealBtn = findViewById(R.id.addMeal);
        final EditText searchBox = findViewById(R.id.searchBox);


        favsButton.setOnClickListener(v->{

            Intent intent = new Intent(NutritionActivity.this, FavouritesPage.class);
            startActivity(intent);
        });
        searchButton.setOnClickListener(v -> {


            searchItem = searchBox.getText().toString();
            if (searchItem.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.enterSearch, Toast.LENGTH_LONG).show();
            } else {

                Intent intent = new Intent(NutritionActivity.this, NutritionSearch.class);
                intent.putExtra("search", searchItem);
                startActivity(intent);
            }
        });
        mealBtn.setOnClickListener(c->{
            startActivity(new Intent(NutritionActivity.this, AddMeal.class));

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
                startActivity(new Intent(NutritionActivity.this, NutritionSearch.class));
                break;
            case R.id.meals:
                startActivity(new Intent(NutritionActivity.this, AddMeal.class));
                break;
            case R.id.home:
                startActivity(new Intent(NutritionActivity.this, MainActivity.class));
                break;
            case R.id.favs:
                startActivity(new Intent(NutritionActivity.this, FavouritesPage.class));
                break;
            case R.id.about:
                View dialogView = getLayoutInflater().inflate(R.layout.about_dialog, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.about);
                alert.setView(dialogView);
                alert.setPositiveButton("OK", (c,b)->{

                });
                alert.create().show();
                Snackbar.make(findViewById(R.id.searchButton), R.string.thanks, Snackbar.LENGTH_LONG).show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}