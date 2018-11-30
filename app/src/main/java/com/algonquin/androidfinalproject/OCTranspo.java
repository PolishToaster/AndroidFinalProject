package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquin.androidfinalproject.OCTranspoPackage.Stop;
import com.algonquin.androidfinalproject.OCTranspoPackage.Trip;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OCTranspo extends Activity {

    String generatedURL = "";
    final String ACTIVITY_NAME = "OCTranspo";
    ProgressBar progressBar;
    LinearLayout progressLayout;
    TextView progressText;
    ListView listView;
    FrameLayout frameLayout;

    SQLiteDatabase dbWrite;
    OCTranspoDatabaseHelper databaseHelper;

    static String baseURL;

    ArrayList<Stop> stopList;
    OCTranspoListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_octranspo);

        Button button = (Button) findViewById(R.id.octranspo_button);
        final EditText editText = (EditText) findViewById(R.id.octranspo_edittext);

        progressLayout = (LinearLayout) findViewById(R.id.octranspo_progress_layout);
        progressBar = (ProgressBar) findViewById(R.id.octranspo_progressbar);
        progressText = (TextView) findViewById(R.id.octranspo_progress_text);

        frameLayout = (FrameLayout) findViewById(R.id.octranspo_framelayout);

        listView = (ListView) findViewById(R.id.octranspo_listview);

        baseURL = getString(R.string.octranspo_baseurl) + "?appID=" + getString(R.string.OCTRANSPO_APP_ID) + "&apiKey=" + getString(R.string.OCTRANSPO_API_KEY);

        databaseHelper = new OCTranspoDatabaseHelper(getApplicationContext());

        dbWrite = databaseHelper.getWritableDatabase();

        stopList = new ArrayList<Stop>();

        listAdapter = new OCTranspoListAdapter(this, stopList);

        listView.setAdapter(listAdapter);

        readFromDatabase(databaseHelper, dbWrite);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString();
                if (input.contentEquals("") || input == null) {
                    popSnack(editText, "Stop number must not be empty or null", Snackbar.LENGTH_LONG);
                    Log.e(ACTIVITY_NAME, "Input was empty or null. Cannot search empty or null values");
                } else {
                    //Search through stop list to make sure stop doesn't exist
                    boolean stopExists = false;
                    for (Stop stop : stopList) {
                        if (input.contentEquals(stop.getStopNumber())) {
                            stopExists = true;
                        }
                    }
                    if (stopExists) {
                        popSnack(editText, "Stop number exists already. Cannot add duplicate stops", Snackbar.LENGTH_LONG);
                        Log.e(ACTIVITY_NAME, "Stop exists already. Cannot add duplicate stops.");
                    } else {
                        generatedURL = urlBuilder(baseURL, input);
                        progressLayout.setVisibility(View.VISIBLE);
                        frameLayout.setVisibility(View.GONE);
                        new OCTranspoQuery(getApplicationContext()).execute(input);
                    }
                }
            }
        });

        /*button.setOnClickListener(new View.OnClickListener() {
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
        });*/
    }

    public void readFromDatabase(OCTranspoDatabaseHelper databaseHelper, SQLiteDatabase dbWrite) {
        Cursor stopCursor = dbWrite.rawQuery("SELECT * FROM " + databaseHelper.getStopTableName(), null);
        if (stopCursor.getCount() != 0) {
            Stop stop;

            stopCursor.moveToFirst();
            do {
                ArrayList<String> routeList = new ArrayList<String>();
                String stopNumber = stopCursor.getString(stopCursor.getColumnIndex(databaseHelper.getKeyStopId()));
                String stopName = stopCursor.getString(stopCursor.getColumnIndex(databaseHelper.getKeyStopName()));
                Log.i(ACTIVITY_NAME, "Stop number: " + stopNumber + ", Stop name: " + stopName);
                Cursor routeCursor = dbWrite.query(databaseHelper.getRouteTableName(), new String[]{databaseHelper.getKeyRouteNumber()},
                        databaseHelper.getKeyStopId() + " = ?", new String[]{stopNumber}, null, null, null);
                routeCursor.moveToFirst();
                do {
                    String route = routeCursor.getString(routeCursor.getColumnIndex(databaseHelper.getKeyRouteNumber()));
                    routeList.add(route);
                } while (routeCursor.moveToNext());
                Collections.sort(routeList, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        int io1 = Integer.parseInt(o1);
                        int io2 = Integer.parseInt(o2);
                        return Integer.compare(io1, io2);
                    }
                });
                stop = new Stop(stopNumber, stopName, routeList);
                stopList.add(stop);
            } while (stopCursor.moveToNext());

            listAdapter.notifyDataSetChanged();
        }
    }

    private class OCTranspoListAdapter extends ArrayAdapter<Stop> {
        ArrayList<Stop> list;

        private OCTranspoListAdapter(Context context, ArrayList<Stop> list) {
            super(context, 0);
            this.list = list;
        }

        public int getCount() {
            return list.size();
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View result = inflater.inflate(R.layout.octranspo_listview_item, null);
            TextView title = (TextView) result.findViewById(R.id.octranspo_listview_item_title);
            final ExpandableRelativeLayout expandable = (ExpandableRelativeLayout) result.findViewById(R.id.octranspo_listview_expandable);
            GridLayout gridLayout = (GridLayout) result.findViewById(R.id.octranspo_listview_expandable_gridlayout);
            for (final String route : list.get(position).getRouteList()) {
                final Button text = new Button(getApplicationContext());
                text.setText(route);
                text.setTextColor(Color.BLACK);
                text.setPadding(5, 5, 5, 5);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        generatedURL = urlBuilder(baseURL, list.get(position).getStopNumber(), route);
                        new OCTranspoQuery(getApplicationContext()).execute(text.getText().toString());
                    }
                });
                gridLayout.addView(text);
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int gridSize = 0;
            for (int i = 0; i < width/240; i++) {
                gridSize++;
            }
            gridLayout.setColumnCount(gridSize);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandable.toggle();
                }
            });
            title.setText(list.get(position).toString());
            expandable.collapse();
            return result;
        }

    }

    private class OCTranspoQuery extends AsyncTask<String, String, String> {

        Context context;
        String message = "";
        ArrayList<Trip> tripList = new ArrayList<Trip>();;

        public OCTranspoQuery(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.i(ACTIVITY_NAME, "Starting OCTranspoQuery");
            XmlPullParser pullParser = Xml.newPullParser();
            InputStream stream = null;

            String tripDestination = "";
            String longitude = "";
            String latitude = "";
            String gpsSpeed = "";
            String tripStartTime = "";
            String adjustedScheduleTime = "";

            publishProgress("0", "Downloading XML");

            try {
                stream = downloadUrl(generatedURL);
                publishProgress("25", "Loading XML");
                pullParser.setInput(stream, null);
                publishProgress("50", "Parsing XML for data");
                if (generatedURL.contains("GetNextTripsForStop")) {
                    message = "No other trips for route " + strings[0] + " for the next 4 hours";
                    int eventType = pullParser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String pullParserName = pullParser.getName();
                        if (pullParser.getName() != null) {
                            if (pullParser.getEventType() != XmlPullParser.END_TAG) {
                                switch (pullParser.getName()) {
                                    case "TripDestination":
                                        tripDestination = pullParser.nextText();
                                        break;
                                    case "Longitude":
                                        longitude = pullParser.nextText();
                                        break;
                                    case "Latitude":
                                        latitude = pullParser.nextText();
                                        break;
                                    case "GPSSpeed":
                                        gpsSpeed = pullParser.nextText();
                                        break;
                                    case "TripStartTime":
                                        tripStartTime = pullParser.nextText();
                                        break;
                                    case "AdjustedScheduleTime":
                                        adjustedScheduleTime = pullParser.nextText();
                                        break;
                                    case "Error":
                                        Log.i(ACTIVITY_NAME, "Found an error tag");
                                        if (pullParser.getAttributeCount() == 1) {
                                            Log.e(ACTIVITY_NAME, "Error in the API call (Error code: " + pullParser.nextText() + ")");
                                            eventType = XmlPullParser.END_DOCUMENT;
                                        }
                                        break;
                                }

                            } else if (pullParser.getName().contentEquals("Trip") && pullParser.getEventType() == XmlPullParser.END_TAG) {
                                tripList.add(new Trip(tripDestination, longitude, latitude, gpsSpeed, tripStartTime, adjustedScheduleTime));
                                message = "Successfully grabbed trip data for route " + strings[0];
                                Log.i(ACTIVITY_NAME, new Trip(tripDestination, longitude, latitude, gpsSpeed, tripStartTime, adjustedScheduleTime).toString());
                            }
                        }
                        eventType = pullParser.next();
                    }

                } else if (generatedURL.contains("GetRouteSummaryForStop")) {
                    Log.i(ACTIVITY_NAME, "In GetRouteSummary");
                    String stopDescription = "";
                    ArrayList<String> routeList = new ArrayList<>();

                    int eventType = pullParser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (pullParser.getName() != null) {
                            if (pullParser.getEventType() != XmlPullParser.END_TAG) {
                                switch (pullParser.getName()) {
                                    case "StopDescription":
                                        stopDescription = pullParser.nextText();
                                        break;
                                    case "RouteNo":
                                        String routeNumber = pullParser.nextText();
                                        if (!routeList.contains(routeNumber))
                                            routeList.add(routeNumber);
                                        break;
                                }
                            }
                        }
                        eventType = pullParser.next();
                    }
                    publishProgress("75", "Saving data to application");
                    if (stopDescription.isEmpty() || routeList.isEmpty()) {
                        message = "Stop does not exist. Not adding to the list.";
                    } else {
                        stopList.add(new Stop(strings[0], stopDescription, routeList));
                        //add stop to database
                        for (String route : routeList) {
                            ContentValues insertRoute = new ContentValues();
                            insertRoute.put(databaseHelper.getKeyRouteNumber(), route);
                            insertRoute.put(databaseHelper.getKeyStopId(), strings[0]);

                            dbWrite.insert(databaseHelper.getRouteTableName(), null, insertRoute);
                        }
                        ContentValues insertStop = new ContentValues();
                        insertStop.put(databaseHelper.getKeyStopId(), strings[0]);
                        insertStop.put(databaseHelper.getKeyStopName(), stopDescription);

                        dbWrite.insert(databaseHelper.getStopTableName(), null, insertStop);

                        message = "Stop " + strings[0] + " successfully added to the list!";
                    }
                }

            } catch (IOException e) {
                message = "Failed to retrieve data from the server.";
            } catch (XmlPullParserException e) {
                message = "XML reader encountered an error.";
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(Integer.parseInt(values[0]));
            progressText.setText(values[1]);
            progressLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressLayout.setVisibility(View.GONE);
            listAdapter.notifyDataSetChanged();

            popToast(context, message, Toast.LENGTH_LONG);

            if (tripList != null || (generatedURL.contains("GetNextTripsForStop") && !tripList.isEmpty())) {
                frameLayout.setVisibility(View.VISIBLE);
                Bundle args = new Bundle();
                args.putParcelableArrayList("list", tripList);
                Fragment fragment = new OCTranspoDetailsFragment();
                fragment.setArguments(args);
                frameLayout.removeAllViews();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(frameLayout.getId(), fragment);
                fragmentTransaction.commit();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.weight = 1.0f;
                frameLayout.setLayoutParams(params);
                listView.setLayoutParams(params);
            }
        }

        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream();
        }
    }

    private String urlBuilder(String baseURL, String... args) {
        String url = baseURL;

        if (args.length == 1) {
            String busStop = args[0];
            url = url.replaceAll("@", "GetRouteSummaryForStop");
            url += "&stopNo=" + busStop;
        } else if (args.length == 2) {
            String busStop = args[0];
            String busNumber = args[1];
            url = url.replaceAll("@", "GetNextTripsForStop");
            url += "&routeNo=" + busNumber;
            url += "&stopNo=" + busStop;
        }

        Log.i(ACTIVITY_NAME, url);

        return url;
    }

    private String getRouteSummaryForStop(String baseURL, String stopNo) {
        String url = baseURL;

        url = url.replaceAll("!", "GetRouteSummaryForStop");

        url += "&stopNo=" + stopNo;

        return url;
    }

    private String getNextTripsForStop(String baseURL, String stopNo, String routeNo) {
        String url = baseURL;

        url = url.replaceAll("!", "GetNextTripsForStop");

        url += "&stopNo=" + stopNo + "&routeNo=" + routeNo;

        return url;
    }

    private void popToast(Context context, String message, int toastLength) {
        Toast.makeText(context, message, toastLength).show();
    }

    private void popSnack(View view, String message, int snackLength) {
        Snackbar.make(view, message, snackLength).show();
    }
}
