package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OCTranspo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_octranspo);

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(OCTranspo.this)
                        .setTitle("Alert Dialog")
                        .setMessage("This is an alert dialog!")
                        .setNeutralButton("Show a Toast", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "This is a toast!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("Show a snackbar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(findViewById(R.id.button), "This is a snackbar!", Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .show();

            }
        });
    }
}
