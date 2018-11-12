package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.Button;

public class OCTranspo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_octranspo);

        Button button = (Button)findViewById(R.id.button);

        //Snackbar.make(button, "String to show", Snackbar.LENGTH_LONG).show();


    }
}
