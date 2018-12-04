package com.algonquin.androidfinalproject;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MealFragment extends Fragment {

    private double totalCal, avgCal, maxCal, minCal;
    private String mealName;
    private NutritionDatabaseHelper db;
    private TextView totalCalTV, avgCalTV, maxCalTV, minCalTV, mealNameTV;

    public MealFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        db = new NutritionDatabaseHelper(getContext());
        mealName = extras.getString("meal");
        totalCal = getCalculatedValue("SUM");
        avgCal = getCalculatedValue("AVG");
        maxCal = getCalculatedValue("MAX");
        minCal = getCalculatedValue("MIN");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meal_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button remove = view.findViewById(R.id.removeMeal);
        remove.setOnClickListener(c->{
            db.deleteMeal(mealName);

        });
        totalCalTV = view.findViewById(R.id.totalCal);
        totalCalTV.setText(R.string.total + String.valueOf(totalCal));
        avgCalTV = view.findViewById(R.id.avgCal);
        avgCalTV.setText(R.string.average + String.valueOf(avgCal));
        maxCalTV = view.findViewById(R.id.maxCal);
        maxCalTV.setText(R.string.max + String.valueOf(maxCal));
        minCalTV = view.findViewById(R.id.minCal);
        minCalTV.setText(R.string.min + String.valueOf(minCal));
        mealNameTV = view.findViewById(R.id.mealName);
        mealNameTV.setText(mealName);
    }

    private double getCalculatedValue(String calculation){
        Cursor cursor = db.calculate(calculation, mealName);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex("CALC");
        return cursor.getDouble(index);
    }
}
