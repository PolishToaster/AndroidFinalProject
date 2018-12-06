package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class FavouritesPage extends AppCompatActivity {

    private FavouritesListAdapter adapter;
    private ArrayList<Food> favs;
    private NutritionDatabaseHelper dHelp;
    private final String ACTIVITY_NAME = "FavouritesPage";
    private ListView list;
    private Cursor cursor;
    private boolean isWide;
    private ProgressBar pg;
    private static Context context;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_page);
        Toolbar toolbar = findViewById(R.id.nutritionToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        FavouritesPage.context = getApplicationContext();
        isWide = (findViewById(R.id.isWide) != null);
        favs = new ArrayList<>();
        dHelp = new NutritionDatabaseHelper(this);
        pg = findViewById(R.id.progressBar2);
        pullDataFromDatabase(dHelp.FAVS_TABLE, new String[]{dHelp.FOOD_ID, dHelp.FOOD_NICK});
        list = findViewById(R.id.favsList);
        list.setOnItemClickListener((parent, view, position, id) -> {

            if (!isWide) {
                Intent selected = new Intent(FavouritesPage.this, FoodDetails.class);
                selected.putExtra("food", ((Food) list.getItemAtPosition(position)));
                selected.putExtra("isSearch", false);
                selected.putExtra("isWide", false);
                startActivityForResult(selected, 13);
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelable("food", ((Food) list.getItemAtPosition(position)));
                bundle.putBoolean("isSearch", false);
                bundle.putBoolean("isWide", true);
                FoodDetailsFragment newFrag = new FoodDetailsFragment();
                newFrag.setArguments(bundle);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.foodDetails, newFrag);
                ft.addToBackStack("does't matter");
                ft.commit();
            }

        });

    }

    private void pullDataFromDatabase(String table, String[] columns){

        cursor = dHelp.queryAll(table, columns);
        if (cursor.getCount() > 0) {
            PullFromDatabase aS = new PullFromDatabase(table, columns);
            aS.execute();
        } else {
            pg.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.addFav,Toast.LENGTH_LONG).show();
        }


    }




    public void removeFavourite(String id){

        dHelp.deleteThis(id);
        favs.clear();
        pullDataFromDatabase(dHelp.FAVS_TABLE, new String[]{dHelp.FOOD_ID, dHelp.FOOD_NICK});
        Toast.makeText(this, R.string.removed, Toast.LENGTH_LONG).show();
    }

    public void addToMeal(String food, String meal, double kCal){
        Cursor foodCursor = dHelp.query(dHelp.FAVS_TABLE, dHelp.FOOD_ID, food);
        foodCursor.moveToFirst();
        int fKey = foodCursor.getInt(foodCursor.getColumnIndex(dHelp.KEY_ID));
        Cursor mealCursor = dHelp.query(dHelp.MEAL_LIST_TABLE, dHelp.MEAL, meal);
        mealCursor.moveToFirst();
        int mealKey = mealCursor.getInt(mealCursor.getColumnIndex(dHelp.KEY_ID));
        double energy = kCal;
        dHelp.insert(mealKey, fKey, energy);
        Toast.makeText(this, getString(R.string.added) + meal, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras;
        if (data != null) {
            extras = data.getExtras();

            if (resultCode == 13 && requestCode == 13) {
                String toDelete = data.getExtras().getString("id");
                removeFavourite(toDelete);
                Toast.makeText(this, R.string.removed, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_OK) {
                addToMeal(extras.getString("food"), extras.getString("meal"), extras.getDouble("kCal"));
            }
        }
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
                startActivity(new Intent(FavouritesPage.this, NutritionSearch.class));
                break;
            case R.id.meals:
                startActivity(new Intent(FavouritesPage.this, AddMeal.class));
                break;
            case R.id.home:
                startActivity(new Intent(FavouritesPage.this, MainActivity.class));
                break;
            case R.id.favs:
                Snackbar.make(findViewById(R.id.favsList), R.string.here, Snackbar.LENGTH_LONG).show();
                break;
            case R.id.about:
                View dialogView = getLayoutInflater().inflate(R.layout.about_dialog, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.about);
                alert.setView(dialogView);
                alert.setPositiveButton("OK", (c,b)->{

                });
                alert.create().show();
                Snackbar.make(findViewById(R.id.favsList), R.string.thanks, Snackbar.LENGTH_LONG).show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    protected class FavouritesListAdapter extends ArrayAdapter<Food>{

        public FavouritesListAdapter(Context context, ArrayList<Food> foods) {
            super(context, 0, foods);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Food food = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_layout, null);
            TextView tvNick = view.findViewById(R.id.foodName);
            TextView tvName = view.findViewById(R.id.otherDetails);
            tvNick.setText(food.getNickname());
            tvName.setText(food.getLabel());

            return view;
        }
    }

    public class PullFromDatabase extends AsyncTask<String, Integer, String> {

        private String table;
        private String[] columns;
        //Activity activity;

        private PullFromDatabase(String table, String[] columns) {
            super();
            this.table = table;
            this.columns = columns;
            //this.activity = FavouritesPage.this;
        }

        @Override
        protected void onPreExecute() {
            pg.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            cursor = dHelp.queryAll(table, columns);
            int foodNickIndex = cursor.getColumnIndex(dHelp.FOOD_NICK);
            int foodIdIndex = cursor.getColumnIndex(dHelp.FOOD_ID);
            cursor.moveToFirst();
            int progress = 100/cursor.getCount();
            for (int i = 0; i < cursor.getCount(); i++) {
                String foodId = cursor.getString(foodIdIndex);
                String nickname = cursor.getString(foodNickIndex);
                Food addFood = new Food(foodId, nickname);
                favs.add(addFood);
                cursor.moveToNext();
                onProgressUpdate(progress);
                progress += progress;
                try {

                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            pg.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(String s) {
            adapter = new FavouritesListAdapter(FavouritesPage.this, favs);
            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            pg.setVisibility(View.INVISIBLE);
        }
    }


}
