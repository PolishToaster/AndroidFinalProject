package com.algonquin.androidfinalproject;

import java.net.HttpURLConnection;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import java.io.InputStream;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.app.Activity;

public class MovieImport extends Activity {

    private EditText titleT,actorT,yearT,ratingT,runtimeT,plotT,urlT;
    private Button cancelT,downloadT;
    private ProgressBar progressBarT;
    private Bundle bundle;
    public static final int DOWNLOAD_MOVIE_RESULT = 8;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_import);
        titleT=(EditText)findViewById(R.id.movie_detail_title_value);
        actorT=(EditText)findViewById(R.id.movie_detail_actor_value);
        yearT=(EditText)findViewById(R.id.movie_detail_year_value);
        ratingT=(EditText)findViewById(R.id.movie_detail_rating_value);
        runtimeT=(EditText)findViewById(R.id.movie_detail_runtime_value);
        plotT=(EditText)findViewById(R.id.movie_detail_plot_value);
        urlT=(EditText)findViewById(R.id.movie_detail_url_value);
        progressBarT=(ProgressBar)findViewById(R.id.movie_progressBar);
        progressBarT.setVisibility(View.VISIBLE);
        cancelT= (Button)findViewById(R.id.movie_import_cancel);
        downloadT=(Button)findViewById(R.id.movie_import_download);

        final MovieQuery movieQuery = new MovieQuery();
        movieQuery.execute();

        downloadT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                bundle = movieQuery.getBundle();
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(DOWNLOAD_MOVIE_RESULT,intent);
                finish();
            }
        });
        cancelT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

    }

    public class MovieQuery extends AsyncTask<String,Integer,String> {
        private ArrayList<String> title = new ArrayList<>();
        private ArrayList<String> actor= new ArrayList<>();
        private ArrayList<String> year= new ArrayList<>();
        private ArrayList<String> runtime= new ArrayList<>();
        private ArrayList<String> rating= new ArrayList<>();
        private ArrayList<String> plot= new ArrayList<>();
        private ArrayList<String> url= new ArrayList<>();
        boolean isTitle,isActor,isYear,isRuntime,isRating,isPlot=false;
        URL link;

        @Override
        protected String doInBackground(String... strings) {
            try{
                link= new URL("http://www.omdbapi.com/?apikey=6c9862c2&r=xml ");
                HttpURLConnection connection = (HttpURLConnection)link.openConnection();
                connection.setReadTimeout(8000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);

                while(parser.next()!=XmlPullParser.END_DOCUMENT){
                    switch(parser.getEventType()){
                        case XmlPullParser.START_TAG:
                            String tagName = parser.getName();
                            if(tagName.equals("Title")) isTitle=true;
                            if (tagName.equals("Actors")) isActor=true;
                            if(tagName.equals("Year")) isYear=true;
                            if(tagName.equals("Runtime")) isRuntime=true;
                            if(tagName.equals("Rating")) isRating=true;
                            if(tagName.equals("Plot")) isPlot=true;
                            if(tagName.equals("URL")) url.add(parser.getAttributeValue(null,"value"));
                            break;
                        case XmlPullParser.TEXT:
                            String text = parser.getText();
                            if(isTitle) title.add(text);
                            if(isActor)  actor.add(text);
                            if(isYear)   year.add(text);
                            if(isRuntime) runtime.add(text);
                            if(isRating)  rating.add(text);
                            if(isPlot)   plot.add(text);
                            break;
                        case XmlPullParser.END_TAG:
                            String tagName2 = parser.getName();
                            if(tagName2.equals("Title")) isTitle=false;
                            if(tagName2.equals("Actors")) isActor=false;
                            if(tagName2.equals("Year")) isYear=false;
                            if(tagName2.equals("Runtime")) isRuntime=false;
                            if(tagName2.equals("Rating")) isRating=false;
                            if(tagName2.equals("Plot")) isPlot=false;
                    }
                }
                connection.disconnect();}
            catch (XmlPullParserException e) {e.printStackTrace();}
            catch (MalformedURLException e) { e.printStackTrace(); }
            catch (Exception e) {e.printStackTrace(); }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer...value){
            progressBarT.setVisibility(View.VISIBLE);
            progressBarT.setProgress(value[0]);
        }

        @Override
        protected void onPostExecute(String result){
            progressBarT.setVisibility(View.INVISIBLE);
            titleT.setText(title.get(0));
            actorT.setText(actor.get(0));
            yearT.setText(year.get(0));
            publishProgress(30);
            android.os.SystemClock.sleep(400);

            runtimeT.setText(runtime.get(0));
            ratingT.setText(rating.get(0));
            plotT.setText(plot.get(0));
            urlT.setText(url.get(0));
            publishProgress(80);
            android.os.SystemClock.sleep(400);
        }
        public Bundle getBundle(){
            bundle = new Bundle();
            bundle.putStringArrayList(MovieDataHelper.KEY_TITLE,title);
            bundle.putStringArrayList(MovieDataHelper.KEY_ACTOR,actor);
            bundle.putStringArrayList(MovieDataHelper.KEY_YEAR,year);
            bundle.putStringArrayList(MovieDataHelper.KEY_RUNTIME,runtime);
            bundle.putStringArrayList(MovieDataHelper.KEY_RATING,rating);
            bundle.putStringArrayList(MovieDataHelper.KEY_PLOT,plot);
            bundle.putStringArrayList(MovieDataHelper.KEY_URL,url);
            return bundle;
        }
    }
    
    
}
