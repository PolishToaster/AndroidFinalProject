package com.algonquin.androidfinalproject;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
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
import java.util.HashMap;
import java.util.Iterator;

import static java.net.URLEncoder.encode;

public class Food implements Parcelable {

    private String foodId;
    private String label;
    private String brand;
    private String nickname;
    private int size;
    private HashMap<String, String> nutrients;
    private NutritionQuery nQ;


    public Food(String foodId, String label, String brand, HashMap<String, String> nutrients){
        this(foodId, label, nutrients);
        this.brand = brand;
    }

    public Food(String foodId, String label, HashMap<String, String> nutrients){
        this.nutrients = nutrients;
        renameKeys(this.nutrients);
        this.size = nutrients.size();
        this.foodId = foodId;
        this.label = label;
        this.brand = "Generic Food";
    }

    public Food(String foodId, String nickname){
        this.foodId = foodId;
        setNickname(nickname);
        this.brand = "Generic Food";
        nQ = new NutritionQuery();
        nQ.execute();

    }

    protected Food(Parcel in) {
        foodId = in.readString();
        label = in.readString();
        brand = in.readString();
        size = in.readInt();
        nutrients = new HashMap<>();
        for (int i =0; i<size; i++){
            String key = in.readString();
            String nutrient = in.readString();
            nutrients.put(key, nutrient);
        }


    }
/**
 * <p>auto-generated Creator</p>
 */
    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public String getBrand() {
        return brand;
    }

    public String getLabel() {
        return label;
    }

    public String getFoodId() {
        return foodId;
    }

    public HashMap<String, String> getNutrients(){
        return this.nutrients;
    }

    public String getNickname(){
        return this.nickname;
    }

    private void renameKeys(HashMap<String, String> nutrients){
        HashMap<String, String> raw = new HashMap<>();
        raw.putAll(nutrients);
        nutrients.clear();
        for (String key : raw.keySet()){

            switch (key){
                case "CA":
                    nutrients.put("Calcium", raw.get(key) + " mg");
                    break;
                case "CHOCDF":
                    nutrients.put("Carbs", raw.get(key) + " g");
                    break;
                case "CHOLE":
                    nutrients.put("Cholesterol", raw.get(key) + " mg");
                    break;
                case "FAMS":
                    nutrients.put("Monounsaturated Fat", raw.get(key) + " g");
                    break;
                case "FAPU":
                    nutrients.put("Polyunsaturated Fat", raw.get(key) + " g");
                    break;
                case "FASAT":
                    nutrients.put("Saturated Fat", raw.get(key) + " g");
                    break;
                case "FAT":
                    nutrients.put("Fat", raw.get(key) + " g");
                    break;
                case "FATRN":
                    nutrients.put("Trans Fat", raw.get(key) + " g");
                    break;
                case "FE":
                    nutrients.put("Iron", raw.get(key) + " mg");
                    break;
                case "FIBTG":
                    nutrients.put("Fiber", raw.get(key) + " g");
                    break;
                case "FOLDFE":
                    nutrients.put("Folate", raw.get(key) + " g");
                    break;
                case "K":
                    nutrients.put("Potassium", raw.get(key) + " mg");
                    break;
                case "MG":
                    nutrients.put("Magnesium", raw.get(key) + " mg");
                    break;
                case "NA":
                    nutrients.put("Sodium", raw.get(key) + " mg");
                    break;
                case "ENERC_KCAL":
                    nutrients.put("Energy", raw.get(key) + " kcal");
                    break;
                case "NIA":
                    nutrients.put("B3", raw.get(key) + " mg");
                    break;
                case "P":
                    nutrients.put("Phosphorous", raw.get(key) + " mg");
                    break;
                case "PROCNT":
                    nutrients.put("Protein", raw.get(key) + " g");
                    break;
                case "RIBF":
                    nutrients.put("B2", raw.get(key) + " mg");
                    break;
                case "SUGAR":
                    nutrients.put("Sugars", raw.get(key) + " g");
                    break;
                case "THIA":
                    nutrients.put("B1", raw.get(key) + " mg");
                    break;
                case "TOCPHA":
                    nutrients.put("E", raw.get(key) + " mg");
                    break;
                case "VITA_RAE":
                    nutrients.put("A", raw.get(key) + " g");
                    break;
                case "VITB12":
                    nutrients.put("B12", raw.get(key) + " g");
                    break;
                case "VITB6A":
                    nutrients.put("B6", raw.get(key) + " mg");
                    break;
                case "VITC":
                    nutrients.put("C", raw.get(key) + " mg");
                    break;
                case "VITD":
                    nutrients.put("D", raw.get(key) + " g");
                    break;
                case "VITK1":
                    nutrients.put("K", raw.get(key) + " g");
                    break;
            }
        }

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.foodId);
        dest.writeString(this.label);
        dest.writeString(this.brand);
        dest.writeInt(this.size);
        for (String key : nutrients.keySet()){
            dest.writeString(key);
            dest.writeString(nutrients.get(key));
        }

    }

    private URL getUrl(String searchValue){
        URL url;
        String ingr;
        try {
            ingr = encode(searchValue, "UTF-8");
            url = new URL("https://api.edamam.com/api/food-database/parser?app_id=29c18d4f&app_key=03b359a7a4e853d951fa29dc6482776b&ingr=" + ingr);
            return url;
        } catch (UnsupportedEncodingException e){
            Log.i("Food", "Unsupported Encoding Exception");
            e.printStackTrace();
        } catch (MalformedURLException e){
            Log.i("Food", "Malformed URL Exception");
            e.printStackTrace();
        }
        return null;

    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    private void setLabel(String label){
        this.label = label;
    }
    private void setBrand(String brand){
        this.brand = brand;
    }

    protected class NutritionQuery extends AsyncTask<String, Integer, String> {

        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection conn;
            try{
                conn = (HttpURLConnection) getUrl(foodId).openConnection();
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
                    setLabel(food.getString("label"));
                    JSONObject nutrientList = food.getJSONObject("nutrients");
                    nutrients = new HashMap<>();
                    Iterator<String> keys = nutrientList.keys();
                    while (keys.hasNext()){
                        String key = keys.next();
                        nutrients.put(key, nutrientList.getString(key));
                    }

                    if (food.has("brand")){
                        setBrand(food.getString("brand"));
                    }

                }
            } catch (ProtocolException e) {
                Log.i("Food", "Protocol Exception");
                //Toast.makeText(getApplicationContext(), "Protocol Exception", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e){
                Log.i("Food", "IO Exception");
                //Toast.makeText(getApplicationContext(), "IO Exception", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (JSONException e) {
                Log.i("Food", "JSON Exception");
                //Toast.makeText(getApplicationContext(), "JSON Exception", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            renameKeys(nutrients);
            size = nutrients.size();
        }
    }
}
