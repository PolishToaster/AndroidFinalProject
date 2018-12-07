package com.algonquin.androidfinalproject.Movies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquin.androidfinalproject.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/* Said Zaripov*/
public class MovieFragmentDisplay extends Fragment {

    private ProgressBar progressBar;
    private Button doneBtn;
    private ImageButton addFavsBtn;

    private String movieTitle, movieRelease, movieRating, movieRuntime, moviePlot, movieStarring, posterIcon;
    private ImageView posterDisplay;
    private TextView titleDisplay, releaseDisplay, ratingDisplay, runtimeDisplay, plotDisplay, starringDisplay;

    private static String movieURLBase = "http://www.omdbapi.com/?apikey=6c9862c2&r=xml&t=";
    private String movieURL, fetchURL;
    private URL movieWebsite, posterURL;
    private HttpURLConnection movieConnection, moviePosterConnnection;

    private ContentValues cValues = new ContentValues();
    private Cursor c;
    private SQLiteDatabase db;

    public MovieFragmentDisplay(){}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View screen = inflater.inflate(R.layout.activity_movie_fragment_display, container, false);

        progressBar = screen.findViewById(R.id.movieDisplayBar);
        doneBtn = screen.findViewById(R.id.displayReturnBtn);
        addFavsBtn = screen.findViewById(R.id.addToFavBtn);

        posterDisplay = screen.findViewById(R.id.movieDisplayPoster);
        titleDisplay = screen.findViewById(R.id.movieDisplayTitle);
        releaseDisplay = screen.findViewById(R.id.movieDisplayReleaseDate);
        ratingDisplay = screen.findViewById(R.id.movieDisplayRating);
        runtimeDisplay = screen.findViewById(R.id.movieDisplayRuntime);
        plotDisplay = screen.findViewById(R.id.movieDisplayPlot);
        starringDisplay = screen.findViewById(R.id.movieDisplayStarring);

        progressBar.setVisibility(View.VISIBLE);

        doneBtn.setOnClickListener((v -> {
            ((MovieActivity)getActivity()).toMovieMain();
        }));

        addFavsBtn.setOnClickListener((v -> {

            db = ((MovieActivity)getActivity()).getDb();

            c = db.rawQuery("SELECT "+MovieDatabaseHelper.KEY_TITLE+" FROM "+ MovieDatabaseHelper.DATABASE_NAME+" WHERE "+
                    MovieDatabaseHelper.KEY_TITLE+"=?", new String[]{movieTitle});

            c.moveToFirst();
            String movieTitleToTest = "";
            if(c!=null && c.getCount()>0) {
                movieTitleToTest = c.getString(c.getColumnIndex(MovieDatabaseHelper.KEY_TITLE));
            }

            if (movieTitleToTest.equals(movieTitle)){
                Toast toast = Toast.makeText(getContext(), movieTitle + getString(R.string.added_movieto_DB), Toast.LENGTH_SHORT);
                toast.show(); //display your message box
            }else {
                cValues.put(MovieDatabaseHelper.KEY_TITLE, movieTitle);
                cValues.put(MovieDatabaseHelper.KEY_RELEASED, movieRelease);
                cValues.put(MovieDatabaseHelper.KEY_RATING, movieRating);
                cValues.put(MovieDatabaseHelper.KEY_RUNTIME, movieRuntime);
                cValues.put(MovieDatabaseHelper.KEY_PLOT, moviePlot);
                cValues.put(MovieDatabaseHelper.KEY_STARRING, movieStarring);

                db.insert(MovieDatabaseHelper.DATABASE_NAME, null, cValues);

                Toast toast = Toast.makeText(getContext(), movieTitle + getString(R.string.added_movie_toDB), Toast.LENGTH_SHORT);
                toast.show();
                /*
                display your message box
                 */
            }
        }));


        if (bundle != null) {
            movieURL = bundle.getString("Title", "UTF-8");
        }

        try {
            movieWebsite = new URL(movieURLBase + URLEncoder.encode(movieURL));
        } catch (Exception e) {
            e.printStackTrace();
        }


        MovieQuery query = new MovieQuery();
        query.execute();

        return screen;
    }

    public class MovieQuery extends AsyncTask<String, Integer, String> {

        private Bitmap moviePoster;
        private int responseCode;

        @Override
        protected String doInBackground(String... args) {
            try {
                /*
                FIRST CONNECT TO SERVER.
                 */
                //movieURL = URLEncoder.encode(bundle.getString("Title"), "UTF-8");
                movieWebsite = new URL(movieURLBase + URLEncoder.encode(movieURL));
                movieConnection = (HttpURLConnection) movieWebsite.openConnection();
                InputStream response = movieConnection.getInputStream();

                /*
                READ THE XML
                 */
                XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
                pullParserFactory.setNamespaceAware(false);
                XmlPullParser xpp = pullParserFactory.newPullParser();
                xpp.setInput(response, "UTF-8");

                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    switch (xpp.getEventType()) {
                        case XmlPullParser.START_TAG:
                            String name = xpp.getName();
                            if (name.equals("movie")) {
                                movieTitle = xpp.getAttributeValue(null, "title");
                                onProgressUpdate(10);
                                movieRelease = xpp.getAttributeValue(null, "released");
                                onProgressUpdate(20);
                                movieRating = xpp.getAttributeValue(null, "rated");
                                onProgressUpdate(30);
                                movieRuntime = xpp.getAttributeValue(null, "runtime");
                                onProgressUpdate(40);
                                moviePlot = xpp.getAttributeValue(null, "plot");
                                onProgressUpdate(50);
                                movieStarring = xpp.getAttributeValue(null, "actors");
                                onProgressUpdate(60);

                                posterIcon = xpp.getAttributeValue(null, "poster");
                                onProgressUpdate(70);
                            }

                            Log.i("read XML tag:", name);
                            break;

                        case XmlPullParser.TEXT:
                            break;
                    }
                    xpp.next();
                }
                //Build URL
                URLEncoder.encode(posterIcon, "UTF-8");
                posterURL = new URL(posterIcon);
                moviePosterConnnection = (HttpURLConnection) posterURL.openConnection();

                //FETCH PICTURE
                responseCode = moviePosterConnnection.getResponseCode();
                if (responseCode == 200) {
                    moviePoster = BitmapFactory.decodeStream(moviePosterConnnection.getInputStream());
                    onProgressUpdate(90);
                }
                String imageFile = movieTitle + ".jpg";
                if (fileExistence(imageFile))
                {
                    FileInputStream fis = null;
                    try {
                        fis = getContext().openFileInput(imageFile);
                    } catch (FileNotFoundException e) {e.printStackTrace();}
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                } else {
                    FileOutputStream outputStream = getContext().openFileOutput(imageFile, Context.MODE_PRIVATE);
                    moviePoster.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
                onProgressUpdate(100);

            } catch (Exception e){
                Log.i("Exception", e.getMessage());
            }
            return "";
        }

        private boolean fileExistence(String fileName){
            File file = getContext().getFileStreamPath(fileName);
            return file.exists();
        }

        @Override
        protected void onProgressUpdate(Integer... args) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result!=null) {
                String concat;
                concat = getString(R.string.movie_display_title) + movieTitle;
                titleDisplay.setText(concat);
                concat = getString(R.string.movie_display_release) + movieRelease;
                releaseDisplay.setText(concat);
                concat = getString(R.string.movie_display_rating) + movieRating;
                ratingDisplay.setText(concat);
                concat = getString(R.string.movie_display_runtime) + movieRuntime;
                runtimeDisplay.setText(concat);
                concat = getString(R.string.movie_display_plot) + moviePlot;
                plotDisplay.setText(concat);
                concat = getString(R.string.movie_display_star) + movieStarring;
                starringDisplay.setText(concat);

                posterDisplay.setImageBitmap(moviePoster);

                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}
