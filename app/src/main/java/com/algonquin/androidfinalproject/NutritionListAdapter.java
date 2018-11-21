package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NutritionListAdapter extends ArrayAdapter<String> {

    protected ArrayList<String> foodName;
    private Activity context;




        public NutritionListAdapter(Activity ctx){
            super(ctx, 0);
            this.context = ctx;
            foodName = new ArrayList<>();
        }

        public int getCount(){return foodName.size();}
        public String getItem(int itemNum) { return foodName.get(itemNum);}
        @Override
        public View getView(int position, View view, ViewGroup parent){
            LayoutInflater inflater = context.getLayoutInflater();
            View result = inflater.inflate(R.layout.list_layout,null);
            TextView txtFoodName = result.findViewById(R.id.foodName);
            txtFoodName.setText(getItem(position));
            return result;
        }


}
