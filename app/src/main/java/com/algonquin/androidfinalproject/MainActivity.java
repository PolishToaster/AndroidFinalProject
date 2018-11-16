package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startOCTranspo = (Button)findViewById(R.id.startOCTranspo);
        startOCTranspo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OCTranspo.class);
                MainActivity.this.startActivity(intent);

            }
        });
        Button startNutrition = findViewById(R.id.startNutrition);
        startNutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NutritionActivity.class);
                MainActivity.this.startActivity(intent);

            }
        });
    }
}
