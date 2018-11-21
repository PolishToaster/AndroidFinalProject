package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
<div>Icons made by <a href="https://www.freepik.com/" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/"
    title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/"
    title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
 */
public class NutritionActivity extends Activity {

    protected String searchItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        Button searchButton = findViewById(R.id.searchButton);
        Button favsButton = findViewById(R.id.favsButton);
        final EditText searchBox = findViewById(R.id.searchBox);


        favsButton.setOnClickListener(v->{

            Intent intent = new Intent(NutritionActivity.this, FavouritesPage.class);
            startActivity(intent);
        });
        searchButton.setOnClickListener(v -> {


            searchItem = searchBox.getText().toString();
            if (searchItem.isEmpty()) {
                startActivity(new Intent(NutritionActivity.this, NutritionSearch.class));
                Toast.makeText(getApplicationContext(), "Please enter a search term.", Toast.LENGTH_LONG).show();
            } else {

                Intent intent = new Intent(NutritionActivity.this, NutritionSearch.class);
                intent.putExtra("search", searchItem);
                startActivity(intent);
            }
        });
    }
}