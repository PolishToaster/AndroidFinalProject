package com.algonquin.androidfinalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquin.androidfinalproject.OCTranspoPackage.Stop;
import com.algonquin.androidfinalproject.OCTranspoPackage.Trip;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OCTranspo extends AppCompatActivity {

    /**
     *
     *<h1>OCTranspo Trip Lookup</h1>
     * This application loads OCTranspo stop data through the api and lets the user
     * search up trip information for the related stops
     *
     * @author Zachary Roberts
     * @credit Uses AAkira's ExpandableRelativeLayouts which can be found on
     * Github here: https://github.com/AAkira/ExpandableLayout
     * @version 1.0
     */
    String generatedURL = "";
    final String ACTIVITY_NAME = "OCTranspo";
    ProgressBar progressBar;
    LinearLayout progressLayout;
    TextView progressText;
    ListView listView;
    FrameLayout frameLayout;

    Resources res;

    SQLiteDatabase dbWrite;
    OCTranspoDatabaseHelper databaseHelper;

    static String baseURL;

    Activity activity;

    ArrayList<Stop> stopList;
    OCTranspoListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_octranspo);
        activity = this;
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

        listAdapter.setNotifyOnChange(true);

        listView.setAdapter(listAdapter);

        readFromDatabase(databaseHelper, dbWrite);

        res = getResources();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString();
                if (input.contentEquals("") || input == null) {
                    popSnack(editText, res.getString(R.string.octranspo_empty_stop_error), Snackbar.LENGTH_LONG);
                } else {
                    //Search through stop list to make sure stop doesn't exist
                    boolean stopExists = false;
                    for (Stop stop : stopList) {
                        if (input.contentEquals(stop.getStopNumber())) {
                            stopExists = true;
                        }
                    }
                    if (stopExists) {
                        popSnack(editText, res.getString(R.string.octranspo_duplicate_stop_error), Snackbar.LENGTH_LONG);
                    } else {
                        generatedURL = urlBuilder(baseURL, input);
                        progressLayout.setVisibility(View.VISIBLE);
                        frameLayout.setVisibility(View.GONE);
                        new OCTranspoQuery(getApplicationContext()).execute(input);
                    }
                }
                hideKeyboard(activity);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.octranspo_toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     *
     * <h1>readFromDataase help</h1>
     * This function reads information from the database and loads it into the global arrraylist variables
     *
     * @param databaseHelper the database helper object used to access database names (tables, variables, etc)
     * @param dbWrite the writable database object that is used to grab information on stops and routes
     */
    public void readFromDatabase(OCTranspoDatabaseHelper databaseHelper, SQLiteDatabase dbWrite) {
        Cursor stopCursor = dbWrite.rawQuery("SELECT * FROM " + databaseHelper.getStopTableName(), null);
        if (stopCursor.getCount() != 0) {
            Stop stop;

            stopCursor.moveToFirst();
            do {
                ArrayList<String> routeList = new ArrayList<String>();
                String stopNumber = stopCursor.getString(stopCursor.getColumnIndex(databaseHelper.getKeyStopId()));
                String stopName = stopCursor.getString(stopCursor.getColumnIndex(databaseHelper.getKeyStopName()));
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
        }
        listAdapter.notifyDataSetChanged();
    }

    /**
     * <h1>OCTranspoListAdapter</h1>
     * This class is a custom ArrayAdapter that is used to load stops into the main listview
     * and builds the buttons used to search trip information
     */
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
            final String stopNo = list.get(position).getStopNumber();
            TextView title = (TextView) result.findViewById(R.id.octranspo_listview_item_title);
            final ExpandableRelativeLayout expandable = (ExpandableRelativeLayout) result.findViewById(R.id.octranspo_listview_expandable);
            GridLayout gridLayout = (GridLayout) result.findViewById(R.id.octranspo_listview_expandable_gridlayout);
            for (final String route : list.get(position).getRouteList()) {
                final Button button = new Button(getApplicationContext(), null, android.R.attr.borderlessButtonStyle);
                button.setTextColor(R.color.primary_text_default_material_dark);
                button.setText(route);
                button.setPadding(5, 5, 5, 5);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        generatedURL = urlBuilder(baseURL, list.get(position).getStopNumber(), route);
                        new OCTranspoQuery(getApplicationContext()).execute(button.getText().toString());
                    }
                });
                gridLayout.addView(button);
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int gridSize = 0;
            for (int i = 0; i < width / 270; i++) {
                gridSize++;
            }
            gridLayout.setColumnCount(gridSize);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            gridLayout.setLayoutParams(params);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandable.toggle();
                }
            });
            title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(ACTIVITY_NAME, list.get(position).getStopNumber() + " was long clicked");
                    AlertDialog alertDialog = new AlertDialog.Builder(OCTranspo.this)
                            .setTitle(res.getString(R.string.octranspo_delete_stop_title))
                            .setMessage(res.getString(R.string.octranspo_delete_stop_message, stopNo))
                            .setPositiveButton(res.getString(R.string.octranspo_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbWrite.delete(databaseHelper.getStopTableName(), databaseHelper.getKeyStopId() + " = ?", new String[]{list.get(position).getStopNumber()});
                                    dbWrite.delete(databaseHelper.getRouteTableName(), databaseHelper.getKeyStopId() + " = ?", new String[]{list.get(position).getStopNumber()});
                                    stopList.clear();
                                    readFromDatabase(databaseHelper, dbWrite);
                                    popToast(getContext(), getResources().getString(R.string.octranspo_delete_confirmation, stopNo), Toast.LENGTH_LONG);
                                }
                            })
                            .setNegativeButton(res.getString(R.string.octranspo_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing, user clicked no
                                }
                            })
                            .show();
                    return true;
                }
            });
            title.setText(list.get(position).toString());
            expandable.collapse();
            return result;
        }

    }

    /**
     * <h1>OCTranspoQuery</h1>
     * This class is an AsyncTask for pulling data from the web and parsing the downloaded information
     */
    private class OCTranspoQuery extends AsyncTask<String, String, String> {

        Context context;
        String message = "";
        ArrayList<Trip> tripList = new ArrayList<Trip>();
        ;

        public OCTranspoQuery(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            XmlPullParser pullParser = Xml.newPullParser();
            InputStream stream = null;

            String tripDestination = "N/A";
            String longitude = "N/A";
            String latitude = "N/A";
            String gpsSpeed = "N/A";
            String tripStartTime = "N/A";
            String adjustedScheduleTime = "N/A";

            publishProgress("0", res.getString(R.string.octranspo_downloading_xml));

            try {
                stream = downloadUrl(generatedURL);
                publishProgress("25", res.getString(R.string.octranspo_loading_xml));
                pullParser.setInput(stream, null);
                publishProgress("50", res.getString(R.string.octranspo_parsing_xml));
                //If the URL requested the next trips for a particular stop, get the trip information and send it to the details fragment
                if (generatedURL.contains("GetNextTripsForStop")) {
                    message = res.getString(R.string.octranspo_no_further_trips, strings[0]);
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
                                        if (pullParser.getAttributeCount() == 1) {
                                            Log.e(ACTIVITY_NAME, "Error in the API call (Error code: " + pullParser.nextText() + ")");
                                            eventType = XmlPullParser.END_DOCUMENT;
                                        }
                                        break;
                                }

                            } else if (pullParser.getName().contentEquals("Trip") && pullParser.getEventType() == XmlPullParser.END_TAG) {
                                tripList.add(new Trip(tripDestination, longitude, latitude, gpsSpeed, tripStartTime, adjustedScheduleTime));
                                message = res.getString(R.string.octranspo_trip_data_success, strings[0]);
                            }
                        }
                        eventType = pullParser.next();
                    }

                    //If the URL requested the Route Summary for a stop, get all routes and create a new list item int he stop list
                } else if (generatedURL.contains("GetRouteSummaryForStop")) {
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
                    publishProgress("75", res.getString(R.string.octranspo_saving_data));
                    if (stopDescription.isEmpty() || routeList.isEmpty()) {
                        message = res.getString(R.string.octranspo_stop_does_not_exist, strings[0]);
                    } else {
                        stopList.add(new Stop(strings[0], stopDescription, routeList));
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

                        message = res.getString(R.string.octranspo_stop_data_success, strings[0]);
                    }
                }

            } catch (IOException e) {
                message = res.getString(R.string.octranspo_ioexception);
            } catch (XmlPullParserException e) {
                message = res.getString(R.string.octranspo_xmlexception);
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

            if (tripList != null && (generatedURL.contains("GetNextTripsForStop") && !tripList.isEmpty())) {
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

    /**
     * This function is a helper function for building the url that is used to call the api
     * @param baseURL the base url for the api
     * @param args additional arguments to determine the requested function (GetRouteSummary VS GetNextTrips)
     * @return the completed URL to be queried
     */
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

        return url;
    }

    /**
     * Helper function to create toasts easily
     * @param context context for the toast
     * @param message message in the toast
     * @param toastLength length to display the toast for
     */
    private void popToast(Context context, String message, int toastLength) {
        Toast.makeText(context, message, toastLength).show();
    }

    /**
     * Helper function to create SnackBars easily
     * @param view view for the snackbar
     * @param message message in the snackbar
     * @param snackLength length to display the snackbar
     */
    private void popSnack(View view, String message, int snackLength) {
        Snackbar.make(view, message, snackLength).show();
    }

    /**
     * Helper function to hide the keyboard after the "Add Stop" button has been pressed
     * @param activity activity to grab the focus from
     */
    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * function that inflates the menu items within the toolbar upon load
     * @param m the menu who's children will be inflated
     * @return true if the optionsMenu was created
     */
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.octranspo_toolbar_menu, m);
        return true;
    }

    /**
     * function to handle clicks made on the objects inside the toolbar
     * @param mi menu item selected
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem mi) {
        int id = mi.getItemId();
        switch (id) {
            case R.id.octranspo_action_help:
                View view = View.inflate(this, R.layout.octranspo_about_menu_content, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(res.getString(R.string.octranspo_about_title));
                builder.setView(view);
                TextView credit = (TextView) view.findViewById(R.id.octranspo_credit);
                credit.setMovementMethod(LinkMovementMethod.getInstance());
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
        return true;
    }
}
