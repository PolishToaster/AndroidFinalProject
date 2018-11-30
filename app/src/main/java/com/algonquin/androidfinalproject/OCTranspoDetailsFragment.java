package com.algonquin.androidfinalproject;

import android.app.Fragment;
import android.content.Context;
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

public class OCTranspoDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.octranspo_details_fragment, container, false);
        container.removeAllViews();


        ListView listView = (ListView) view.findViewById(R.id.octranspo_fragment_listview);
        Bundle args = getArguments();
        ArrayList<Trip> list = args.getParcelableArrayList("list");
        for (int i = 0; i < list.size(); i++) {
            Log.i("FRAGMENT", list.get(i).toString());
        }
        TripListAdapter listAdapter = new TripListAdapter(getActivity().getBaseContext(), list);
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        return view;
    }

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

            TextView destination = (TextView) view.findViewById(R.id.octranspo_listview_tripDestination);
            destination.setText("Destination: " + list.get(position).getDestination());

            TextView longitude = (TextView) view.findViewById(R.id.octranspo_listview_longitude);
            longitude.setText("Longitude: " + list.get(position).getLongitude());

            TextView latitude = (TextView) view.findViewById(R.id.octranspo_listview_latitude);
            latitude.setText("Latitude: " + list.get(position).getLatitude());

            TextView speed = (TextView) view.findViewById(R.id.octranspo_listview_gpsSpeed);
            speed.setText("Speed: " + list.get(position).getGpsSpeed() + " k/h");

            TextView start = (TextView) view.findViewById(R.id.octranspo_listview_tripStartTime);
            start.setText("Trip Start Time: " + list.get(position).getTripStartTime());

            TextView adjusted = (TextView) view.findViewById(R.id.octranspo_listview_adjustedScheduleTime);
            adjusted.setText("Estimated Time of Arrival: " + list.get(position).getAdjustedScheduleTime() + " minutes");

            return view;
        }
    }
}
