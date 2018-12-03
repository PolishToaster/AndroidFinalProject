package com.algonquin.androidfinalproject.OCTranspoPackage;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents a Trip. Stores information on each trip for a route query
 */
public class Trip implements Parcelable {
    private String destination;
    private String longitude;
    private String latitude;
    private String gpsSpeed;
    private String tripStartTime;
    private String adjustedScheduleTime;

    public Trip(String destination,
                 String longitude,
                 String latitude,
                 String gpsSpeed,
                 String tripStartTime,
                 String adjustedScheduleTime) {
        this.destination = destination;
        this.longitude = longitude;
        this.latitude = latitude;
        this.gpsSpeed = gpsSpeed;
        this.tripStartTime = tripStartTime;
        this.adjustedScheduleTime = adjustedScheduleTime;
    }

    public Trip(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        this.destination = data[0];
        this.longitude = data[1];
        this.latitude = data[2];
        this.gpsSpeed = data[3];
        this.tripStartTime = data[4];
        this.adjustedScheduleTime = data[5];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.destination,
                this.longitude,
                this.latitude,
                this.gpsSpeed,
                this.tripStartTime,
                this.adjustedScheduleTime});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    @Override
    public String toString() {
        return "Trip{" +
                "destination='" + destination + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", gpsSpeed='" + gpsSpeed + '\'' +
                ", tripStartTime='" + tripStartTime + '\'' +
                ", adjustedScheduleTime='" + adjustedScheduleTime + '\'' +
                '}';
    }

    public String getDestination() {
        return destination;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getGpsSpeed() {
        return gpsSpeed;
    }

    public String getTripStartTime() {
        return tripStartTime;
    }

    public String getAdjustedScheduleTime() {
        return adjustedScheduleTime;
    }
}