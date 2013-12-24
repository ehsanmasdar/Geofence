package com.asdar.geofence;

import android.content.Context;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class GeofenceStore {

    /**
     * @param args
     */
    // Keys for flattened geofences stored in SharedPreferences

    // The SharedPreferences object in which geofences are stored
    private final SharedPreferences mPrefs;
    // The name of the SharedPreferences

    private Context c;

    public GeofenceStore(Context context) {
        mPrefs = context.getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        c = context;
    }

    /**
     * Returns a stored geofence by its id, or returns null if it's not found.
     *
     * @param id The ID of a stored geofence
     * @return A geofence defined by its center and radius. See
     */
    public SimpleGeofence getGeofence(int id, Context c) {
        /*
         * Get the latitude for the geofence identified by id, or
		 * INVALID_FLOAT_VALUE if it doesn't exist
		 */
        boolean mAudioMute = mPrefs.getBoolean(getGeofenceFieldKey(id, GeofenceUtils.KEY_AUDIO_MUTE), GeofenceUtils.INVALID_BOOLEAN_VALUE);
        double lat = mPrefs.getFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
                GeofenceUtils.INVALID_FLOAT_VALUE);
        /*
         * Get the longitude for the geofence identified by id, or
		 * INVALID_FLOAT_VALUE if it doesn't exist
		 */
        double lng = mPrefs.getFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
                GeofenceUtils.INVALID_FLOAT_VALUE);
        String name = mPrefs.getString(getGeofenceFieldKey(id, GeofenceUtils.KEY_NAME),
                GeofenceUtils.INVALID_STRING_VALUE);
        String address = mPrefs.getString(getGeofenceFieldKey(id, GeofenceUtils.KEY_ADDRESS), GeofenceUtils.INVALID_STRING_VALUE);
		/*
		 * Get the radius for the geofence identified by id, or
		 * INVALID_FLOAT_VALUE if it doesn't exist
		 */
        float radius = mPrefs.getFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
                GeofenceUtils.INVALID_FLOAT_VALUE);
		/*
		 * Get the expiration duration for the geofence identified by id, or
		 * INVALID_LONG_VALUE if it doesn't exist
		 */
        long expirationDuration = mPrefs.getLong(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
                GeofenceUtils.INVALID_LONG_VALUE);
		/*
		 * Get the transition type for the geofence identified by id, or
		 * INVALID_INT_VALUE if it doesn't exist
		 */
        int delay = mPrefs.getInt(getGeofenceFieldKey(id,GeofenceUtils.KEY_DELAY), GeofenceUtils.INVALID_INT_VALUE);
        int responsiveness = mPrefs.getInt(getGeofenceFieldKey(id, GeofenceUtils.KEY_RESPONSIVENESS), GeofenceUtils.INVALID_INT_VALUE);
        int transitionType = mPrefs
                .getInt(getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
                        GeofenceUtils.INVALID_INT_VALUE);
        // If none of the values is incorrect, return the object
        if (lat != GeofenceUtils.INVALID_FLOAT_VALUE && lng != GeofenceUtils.INVALID_FLOAT_VALUE
                && radius != GeofenceUtils.INVALID_FLOAT_VALUE
                && expirationDuration != GeofenceUtils.INVALID_LONG_VALUE
                && transitionType != GeofenceUtils.INVALID_INT_VALUE
                && delay != GeofenceUtils.INVALID_INT_VALUE
                && responsiveness != GeofenceUtils.INVALID_INT_VALUE) {

            // Return a true Geofence object
            return new SimpleGeofence(id, name, address, lat, lng, radius, expirationDuration,
                    transitionType,delay, responsiveness);
            // Otherwise, return null.
        } else {
            return null;
        }
    }

    /**
     * Save a geofence.
     *
     * @param geofence The SimpleGeofence containing the values you want to save in
     *                 SharedPreferences
     */
    public void setGeofence(int id, SimpleGeofence geofence) {
		/*
		 * Get a SharedPreferences editor instance. Among other things,
		 * SharedPreferences ensures that updates are atomic and non-concurrent
		 */
        Editor editor = mPrefs.edit();
        // Write the Geofence values to SharedPreferences
        editor.putFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
                (float) geofence.getLatitude());
        editor.putFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
                (float) geofence.getLongitude());
        editor.putFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
                geofence.getRadius());
        editor.putLong(getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
                geofence.getExpirationDuration());
        editor.putInt(getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
                geofence.getTransitionType());
        editor.putInt(getGeofenceFieldKey(id, GeofenceUtils.KEY_DELAY),
                geofence.getmLoiteringDelay());
        editor.putInt(getGeofenceFieldKey(id, GeofenceUtils.KEY_RESPONSIVENESS),
                geofence.getmResponsiveness());
        
        editor.putString(getGeofenceFieldKey(id, GeofenceUtils.KEY_NAME), geofence.getName());
        editor.putString(getGeofenceFieldKey(id, GeofenceUtils.KEY_ADDRESS), geofence.getAddress());
        // Commit the changes
        editor.commit();
    }

    public void clearGeofence(int id) {
		/*
		 * Remove a flattened geofence object from storage by removing all of
		 * its keys
		 */
        Editor editor = mPrefs.edit();
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_AUDIO_MUTE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_NAME));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_ADDRESS));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_DELAY));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_RESPONSIVENESS));
        editor.commit();
    }

    /**
     * Given a Geofence object's ID and the name of a field (for example,
     * KEY_LATITUDE), return the key name of the object's values in
     * SharedPreferences.
     *
     * @param id        The ID of a Geofence object
     * @param fieldName The field represented by the key
     * @return The full key name of a value in SharedPreferences
     */
    static String getGeofenceFieldKey(int id, String fieldName) {
        return GeofenceUtils.KEY_PREFIX + "." + id + "." + fieldName;
    }

}
