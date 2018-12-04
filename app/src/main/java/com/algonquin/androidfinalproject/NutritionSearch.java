package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import static java.net.URLEncoder.encode;

public class NutritionSearch extends AppCompatActivity {

    private static final String ACTIVITY_NAME = "NutritionSearch";

    private ArrayList<Food> foods;
    private ListView listView;
    private String searched;
    private NutritionListAdapter adapter;
    private NutritionQuery nQ;
    private ProgressBar pG;
    private NutritionDatabaseHelper db;
    private boolean isWide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_search);
        Toolbar toolbar = findViewById(R.id.nutritionToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        isWide = (findViewById(R.id.isWide)!=null);
        db = new NutritionDatabaseHelper(this);
        Button search = findViewById(R.id.searchButtonMain);
        TextView searchBox = findViewById(R.id.searchBoxMain);
        Bundle extras;
        pG = findViewById(R.id.progressBar);
        listView = findViewById(R.id.searchList);
        foods = new ArrayList<>();

        if (getIntent().getExtras() != null){
            extras = getIntent().getExtras();
            searched = extras.getString("search");
            search();
        }
            search.setOnClickListener(v -> {


                searched = searchBox.getText().toString();
                search();
            });

        listView.setOnItemClickListener((parent, view, position, id) -> {

            if (!isWide){
            Intent selected = new Intent(NutritionSearch.this, FoodDetails.class);
            selected.putExtra("food", ((Food)listView.getItemAtPosition(position)));
            selected.putExtra("isSearch", true);
            startActivityForResult(selected, 67);
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelable("food", ((Food) listView.getItemAtPosition(position)));
                bundle.putBoolean("isSearch", true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            String foodId = bundle.getString("id");
            String nickname = bundle.getString("nickname");
            db.insert(foodId, nickname);
        }
    }

    private void search(){
        nQ = new NutritionQuery();
        nQ.execute();
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
                Snackbar.make(findViewById(R.id.searchBoxMain), R.string.here, Snackbar.LENGTH_LONG).show();
                break;
            case R.id.meals:
                startActivity(new Intent(NutritionSearch.this, AddMeal.class));
                break;
            case R.id.home:
                startActivity(new Intent(NutritionSearch.this, MainActivity.class));
                break;
            case R.id.favs:
                startActivity(new Intent(NutritionSearch.this, FavouritesPage.class));
                break;
            case R.id.about:
                View dialogView = getLayoutInflater().inflate(R.layout.about_dialog, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.about);
                alert.setView(dialogView);
                alert.setPositiveButton("OK", (c,b)->{

                });
                alert.create().show();
                Snackbar.make(findViewById(R.id.searchBoxMain), R.string.thanks, Snackbar.LENGTH_LONG).show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private URL getUrl(String searchValue){
        URL url;
        String ingr;
        try {
            ingr = encode(searchValue, "UTF-8");
            url = new URL("https://api.edamam.com/api/food-database/parser?app_id=29c18d4f&app_key=03b359a7a4e853d951fa29dc6482776b&ingr=" + ingr);
            return url;
        } catch (UnsupportedEncodingException e){
            Log.i(ACTIVITY_NAME, "Unsupported Encoding Exception");
            Toast.makeText(getApplicationContext(), "Unsupported Encoding", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (MalformedURLException e){
            Log.i(ACTIVITY_NAME, "Malformed URL Exception");
            Toast.makeText(getApplicationContext(), "Malformed URL", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;

    }

    public void addFavourite(String foodNickName, Food food){
        db.insert(food.getFoodId(), foodNickName);

    }
    protected class NutritionQuery extends AsyncTask<String, Integer, String>{

        String label;
        String foodId;
        String result;
        String brand;
        HashMap<String, String> nutrient;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pG.setVisibility(View.VISIBLE);
            foods.clear();


        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection conn;
            try{
                conn = (HttpURLConnection) getUrl(searched).openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                InputStream in = new BufferedInputStream(conn.getInputStream());

                BufferedReader read = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = read.readLine()) != null){
                    sb.append(line + "\n");
                }
                result = sb.toString();

                JSONObject j = new JSONObject(result);
                JSONArray jArray = j.getJSONArray("hints");
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject jObj = jArray.getJSONObject(i);
                    JSONObject food = jObj.getJSONObject("food");
                    foodId = food.getString("foodId");
                    label = food.getString("label");
                    JSONObject nutrients = food.getJSONObject("nutrients");
                    nutrient = new HashMap<>();
                    Iterator<String> keys = nutrients.keys();
                    while (keys.hasNext()){
                        String key = keys.next();
                        nutrient.put(key, nutrients.getString(key));
                    }

                    if (food.has("brand")){
                        brand = food.getString("brand");
                        Food foodItem = new Food(foodId, label, brand, nutrient);
                        foods.add(foodItem);
                    } else {
                        Food foodItem = new Food(foodId, label, nutrient);
                        foods.add(foodItem);
                    }

                    onProgressUpdate(100/jArray.length());

                }
            } catch (ProtocolException e) {
                Log.i(ACTIVITY_NAME, "Protocol Exception");
                //Toast.makeText(getApplicationContext(), "Protocol Exception", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e){
                Log.i(ACTIVITY_NAME, "IO Exception");
                //Toast.makeText(getApplicationContext(), "IO Exception", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (JSONException e) {
                Log.i(ACTIVITY_NAME, "JSON Exception");
                //Toast.makeText(getApplicationContext(), "JSON Exception", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pG.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
                adapter = new NutritionListAdapter(NutritionSearch.this, foods);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                pG.setVisibility(View.INVISIBLE);

        }
    }

    public class NutritionListAdapter extends ArrayAdapter<Food> {


        public NutritionListAdapter(Context ctx, ArrayList<Food> foods){
            super(ctx, 0, foods);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){

            Food food = getItem(position);
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_layout, null);
            TextView foodName = view.findViewById(R.id.foodName);
            TextView brandName = view.findViewById((R.id.otherDetails));
            foodName.setText(food.getLabel());
            brandName.setText(food.getBrand());
            return view;
        }




    }
}
