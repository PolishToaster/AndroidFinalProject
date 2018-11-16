package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NutritionActivity extends Activity {

    protected String searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        Button searchButton = findViewById(R.id.searchButton);
        final EditText searchBox = findViewById(R.id.search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchItem = searchBox.getText().toString();
//regexp from https://stackoverflow.com/questions/3247067/how-do-i-check-that-a-java-string-is-not-all-whitespaces
                if (searchItem.equals("Name") || searchItem.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a search term.", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(NutritionActivity.this)
                            .setTitle("Search Confirmation")
                            .setMessage("You searched for: " + searchItem + "\n Is this correct?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    searchBox.setText("");
                                    Toast.makeText(getApplicationContext(), "Please enter a search term.", Toast.LENGTH_LONG).show();


                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Snackbar.make(findViewById(R.id.searchButton), "Don't Forget to add to favourites!", Snackbar.LENGTH_LONG).show();
                                }
                            })
                            .show();

                }
            }
        });
    }
}