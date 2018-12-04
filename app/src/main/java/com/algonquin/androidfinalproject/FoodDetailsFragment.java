package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FoodDetailsFragment extends Fragment {


    private boolean isSearch;
    private ArrayList<String> nutrientsList;
    private ArrayList<String> meals;
    private NutrientsListAdapter adapter;
    private MealListAdapter mealListAdapter;
    private Food foodItem;
    private String nickname;
    private NutritionDatabaseHelper db;
    private boolean isWide;
    private Button otherButton, detailsButton;



    public FoodDetailsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        foodItem = bundle.getParcelable("food");
        isSearch = bundle.getBoolean("isSearch");
        nutrientsList = new ArrayList<>();
        meals = new ArrayList<>();
        db = new NutritionDatabaseHelper(getContext());
        isWide = bundle.getBoolean("isWide");

    }

    private void populateMeals() {
        Cursor cursor = db.queryAll(db.MEAL_LIST_TABLE, new String[]{db.KEY_ID, db.MEAL});
        int mealIndex = cursor.getColumnIndex(db.MEAL);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String meal = cursor.getString(mealIndex);
            meals.add(meal);
            cursor.moveToNext();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        detailsButton = view.findViewById(R.id.detailsButton);
        otherButton = getActivity().findViewById(R.id.extraButton);
        final Activity parent = getActivity();
        TextView tvName = view.findViewById(R.id.foodNameDetail);
        TextView tvBrand = view.findViewById(R.id.foodBrandDetail);
        ListView listViewDetails = view.findViewById(R.id.detailsWindow);
        tvName.setText(foodItem.getLabel());
        tvBrand.setText(foodItem.getBrand());
        addNutrients();
        adapter = new NutrientsListAdapter(getActivity(), nutrientsList);
        listViewDetails.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (isSearch) {



            detailsButton.setText(R.string.favourite);
            otherButton.setVisibility(View.GONE);
            detailsButton.setOnClickListener(v -> {
                View dialogView = getLayoutInflater().inflate(R.layout.favourite_dialog, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);
                builder.setTitle(R.string.favourite);

                builder.setPositiveButton("OK", (d, id) -> {
                    nickname = ((EditText) dialogView.findViewById(R.id.editText)).getText().toString();
                    if(!isWide) {
                        Intent intent = new Intent();
                        intent.putExtra("id", foodItem.getFoodId());
                        intent.putExtra("nickname", nickname);
                        parent.setResult(Activity.RESULT_OK, intent);
                        parent.finish();
                    } else {
                        Activity activity = getActivity();
                        ((NutritionSearch) activity).addFavourite(nickname, foodItem);
                        ft.remove(this);
                        ft.commit();

                    }

                });
                builder.setNegativeButton(R.string.cancel, (d, id1) -> {

                });
                AlertDialog dialog = builder.create();
                dialog.show();


            });


        } else {

            detailsButton.setText(R.string.unfavourite);
            detailsButton.setOnClickListener(v -> {

                View dialogView = getLayoutInflater().inflate(R.layout.favourite_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);
                ((TextView) dialogView.findViewById(R.id.textView)).setText(R.string.deletePrompt);
                builder.setTitle(R.string.unfavourite);

                builder.setPositiveButton(R.string.delete, (d, id) -> {

                                String confirm = ((EditText) dialogView.findViewById(R.id.editText)).getText().toString();
                    if (!isWide) {
                                Intent intent = new Intent();
                                if (confirm.equals("DELETE")) {
                                    intent.putExtra("id", foodItem.getFoodId());
                                    parent.setResult(13, intent);
                                    parent.finish();
                                } else {
                                    parent.setResult(Activity.RESULT_CANCELED);
                                    parent.finish();
                                }
                    } else {
                        if (confirm.equals("DELETE")) {
                            ((FavouritesPage) getActivity()).removeFavourite(foodItem.getFoodId());

                            ft.remove(this);
                            ft.commit();
                        }
                    }
                    });
                builder.setNegativeButton(R.string.cancel, (d, id1) -> {

                });
                AlertDialog dialog = builder.create();
                dialog.show();

            });
            otherButton.setVisibility(View.VISIBLE);
            otherButton.setText(R.string.addMeal);
            otherButton.setOnClickListener(c -> {
                populateMeals();
                View dialogView = getLayoutInflater().inflate(R.layout.meal_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                ListView list = dialogView.findViewById(R.id.mealList);
                mealListAdapter = new MealListAdapter(getContext(), meals);
                list.setAdapter(mealListAdapter);
                mealListAdapter.notifyDataSetChanged();
                builder.setTitle(R.string.addMeal);
                builder.setView(dialogView);
                ((TextView) dialogView.findViewById(R.id.addMealText)).setText("Choose a meal");
                AlertDialog dialog = builder.create();
                dialog.show();
                ((ListView) dialogView.findViewById(R.id.mealList)).setOnItemClickListener((p, v, position, id) -> {
                    if (!isWide){
                        Intent intent = new Intent();
                        intent.putExtra("meal", ((ListView) dialogView.findViewById(R.id.mealList)).getItemAtPosition(position).toString());
                        intent.putExtra("food", foodItem.getFoodId());
                        String[] energy = foodItem.getNutrients().get("Energy").split(" ");
                        intent.putExtra("kCal", Double.parseDouble(energy[0]));
                        parent.setResult(Activity.RESULT_OK, intent);
                        dialog.dismiss();
                        parent.finish();
                    } else {
                        String[] energy = foodItem.getNutrients().get("Energy").split(" ");
                        ((FavouritesPage)getActivity()).addToMeal(((ListView) dialogView.findViewById(R.id.mealList)).getItemAtPosition(position).toString(), foodItem.getFoodId(), Double.parseDouble(energy[0]));
                        dialog.dismiss();
                    }

                });
            });
        }
    }


    private void addNutrients() {
        nutrientsList.clear();
        for (String key : foodItem.getNutrients().keySet()) {
            String toAdd = key + ": " + foodItem.getNutrients().get(key);
            nutrientsList.add(toAdd);
        }

    }

    protected class NutrientsListAdapter extends ArrayAdapter<String> {

        int i = 0;

        public NutrientsListAdapter(Context context, ArrayList<String> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            view = LayoutInflater.from(getContext()).inflate(R.layout.details_layout, null);
            TextView tv = view.findViewById(R.id.nutrientDetails);
            if (i < nutrientsList.size()) {
                tv.setText(nutrientsList.get(i));
                i++;
            }
            return view;
        }

    }

    protected class MealListAdapter extends ArrayAdapter<String> {



        public MealListAdapter(Context ctx, ArrayList<String> list) {
            super(ctx, 0, list);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.meal_list, null);
            TextView tv = view.findViewById(R.id.mealNameText);
            tv.setText(getItem(position));
            return view;
            }
        }
    }

