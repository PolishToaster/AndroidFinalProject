package com.algonquin.androidfinalproject;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.algonquin.androidfinalproject.OCTranspoPackage.Trip;

import java.util.ArrayList;

/**
 * <h1>OCTranspo Details Fragment</h1>
 * This class is the details from when the user requests trip information for a route.
 * This class handles the fragment creation and all the items within
 */
public class OCTranspoDetailsFragment extends Fragment {

    Resources res;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.octranspo_details_fragment, container, false);
        container.removeAllViews();

        res = getResources();

        ListView listView = (ListView) view.findViewById(R.id.octranspo_fragment_listview);
        Bundle args = getArguments();
        ArrayList<Trip> list = args.getParcelableArrayList("list");
        TripListAdapter listAdapter = new TripListAdapter(getActivity().getBaseContext(), list);
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        return view;
    }

    /**
     * <h1>TripListAdapter</h1>
     * This class is a custom ArrayAdapter that creates the list of routes acquired from the trip query
     */
    private class TripListAdapter extends ArrayAdapter<Trip> {
        ArrayList<Trip> list;

        private TripListAdapter(Context context, ArrayList<Trip> list) {
            super(context, 0);
            this.list = list;
        }

        public int getCount() {
            return list.size();
        }

        @Nullable
        @Override
        public Trip getItem(int position) {
            return list.get(position);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.octranspo_details_fragment_item, null);

            String destinationStr = list.get(position).getDestination();
            String longitudeString = list.get(position).getLongitude();
            String latitudeStr = list.get(position).getLatitude();
            String gpsSpeedStr = list.get(position).getGpsSpeed();
            String tripStartStr = list.get(position).getTripStartTime();
            String adjustedStr = list.get(position).getAdjustedScheduleTime();


            TextView destination = (TextView) view.findViewById(R.id.octranspo_listview_tripDestination);
            TextView longitude = (TextView) view.findViewById(R.id.octranspo_listview_longitude);
            TextView latitude = (TextView) view.findViewById(R.id.octranspo_listview_latitude);
            TextView speed = (TextView) view.findViewById(R.id.octranspo_listview_gpsSpeed);
            TextView start = (TextView) view.findViewById(R.id.octranspo_listview_tripStartTime);
            TextView adjusted = (TextView) view.findViewById(R.id.octranspo_listview_adjustedScheduleTime);

            if (!destinationStr.isEmpty())
                destination.setText(res.getString(R.string.octranspo_trip_destination, list.get(position).getDestination()));
            else
                destination.setVisibility(View.GONE);

            if (!longitudeString.isEmpty())
                longitude.setText(res.getString(R.string.octranspo_longitude, list.get(position).getLongitude()));
            else
                longitude.setVisibility(View.GONE);

            if (!latitudeStr.isEmpty())
                latitude.setText(res.getString(R.string.octranspo_latitude, list.get(position).getLatitude()));
            else
                latitude.setVisibility(View.GONE);

            if (!gpsSpeedStr.isEmpty())
                speed.setText(res.getString(R.string.octranspo_gps_speed, list.get(position).getGpsSpeed()));
            else
                speed.setVisibility(View.GONE);

            if (!tripStartStr.isEmpty())
                start.setText(res.getString(R.string.octranspo_trip_start_time, list.get(position).getTripStartTime()));
            else
                start.setVisibility(View.GONE);

            if (!adjustedStr.isEmpty())
                adjusted.setText(res.getString(R.string.octranspo_adjusted_schedule_time, list.get(position).getAdjustedScheduleTime()));
            else
                adjusted.setVisibility(View.GONE);

            return view;
        }
    }
}
