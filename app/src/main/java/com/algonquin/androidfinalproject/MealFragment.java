package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
    private boolean isWide;

    public MealFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        db = new NutritionDatabaseHelper(getContext());
        isWide = extras.getBoolean("isWide");
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
            if (!isWide) {
                getActivity().setResult(10);
                getActivity().finish();
            } else {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(this);
                ft.commit();
                ((AddMeal) getActivity()).populateMeals();

            }

        });
        totalCalTV = view.findViewById(R.id.totalCal);
        totalCalTV.setText(getString(R.string.total) + String.valueOf(totalCal) + " kCal");
        avgCalTV = view.findViewById(R.id.avgCal);
        avgCalTV.setText(getString(R.string.total) + String.valueOf(avgCal) + " kCal");
        maxCalTV = view.findViewById(R.id.maxCal);
        maxCalTV.setText(getString(R.string.total) + String.valueOf(maxCal) + " kCal");
        minCalTV = view.findViewById(R.id.minCal);
        minCalTV.setText(getString(R.string.total) + String.valueOf(minCal) + " kCal");
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
