package com.asdar.geofence;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.Geofence;

public class SimpleGeofence /*implements Parcelable */ {
    // Instance variables
    private final int mId;
    private String mName;
    private String mAddress;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;
    private int mTransitionType;
    private int mResponsiveness;
	private int mLoiteringDelay;


	/**
     * @param geofenceId The Geofence's request ID
     * @param latitude   Latitude of the Geofence's center.
     * @param longitude  Longitude of the Geofence's center.
     * @param radius     Radius of the geofence circle.
     * @param expiration Geofence expiration duration
     * @param transition Type of Geofence transition.
     * @parma name
     * Name of Geofence.
     */
    public SimpleGeofence(int geofenceId, String name, String address, double latitude, double longitude,
                          float radius, long expiration, int transition, int delay, int responsiveness) {
        // Set the instance fields from the constructor
        this.mId = geofenceId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mExpirationDuration = expiration;
        this.mTransitionType = transition;
        this.mName = name;
        this.mAddress = address;
        this.mLoiteringDelay = delay;
        this.mResponsiveness = responsiveness;
    }

	public int getmResponsiveness() {
		return mResponsiveness;
	}

	public void setmResponsiveness(int mResponsiveness) {
		this.mResponsiveness = mResponsiveness;
	}

	public String getAddress() {
        return mAddress;
    }

    // Instance field getters
    public int getId() {
        return mId;
    }

    @Override
    public String toString() {
        return "SimpleGeofence [mId=" + mId + ", mLatitude=" + mLatitude
                + ", mLongitude=" + mLongitude + ", mRadius=" + mRadius
                + ", mExpirationDuration=" + mExpirationDuration
                + ", mTransitionType=" + mTransitionType + "And Actions]";
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public float getRadius() {
        return mRadius;
    }

    public long getExpirationDuration() {
        return mExpirationDuration;
    }

    public int getTransitionType() {
        return mTransitionType;
    }

    /**
     * Creates a Location Services Geofence object from a SimpleGeofence.
     *
     * @return A Geofence object
     */
    public Geofence toGeofence() {
        // Build a new Geofence object
    	Integer i = getId();
        return new Geofence.Builder().setRequestId(i.toString())
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(getLatitude(), getLongitude(), getRadius())
                .setExpirationDuration(mExpirationDuration)
                .setNotificationResponsiveness(mResponsiveness).setLoiteringDelay(mLoiteringDelay).build();
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
    public static final Parcelable.Creator<SimpleGeofence> CREATOR = new Parcelable.Creator<SimpleGeofence>() {
        public SimpleGeofence createFromParcel(Parcel in) {
            return new SimpleGeofence(in);
        }

        public SimpleGeofence[] newArray(int size) {
            return new SimpleGeofence[size];
        }
    };

    private SimpleGeofence(Parcel in) {
        mId = in.readInt();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mRadius = in.readFloat();
        mExpirationDuration = in.readLong();
        mTransitionType = in.readInt();

    }


    public int getmLoiteringDelay() {
		return mLoiteringDelay;
	}

	public void setmLoiteringDelay(int mLoiteringDelay) {
		this.mLoiteringDelay = mLoiteringDelay;
	}
}
