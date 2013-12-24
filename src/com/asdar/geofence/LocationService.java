package com.asdar.geofence;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	private final IBinder binder = new LocationBinder();
	LocationClient mLocationClient;
	LocationRequest mLocationRequest;
	SharedPreferences mPrefs;

	public int onStartCommand(Intent intent, int flags, int startID) {
		Log.d("com.asdar.geofence", "started location service");
		mPrefs = getBaseContext().getSharedPreferences(
				GeofenceUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		makeForeground();
		startLocationListening(10000);
		return 1;
	}

	public void startLocationListening(int interval) {
		mLocationRequest = new LocationRequest().setPriority(
				LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(interval);
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
	}

	public void makeForeground() {
		Intent intent = new Intent(this, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(intent);
		PendingIntent localPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		builder.setContentTitle("Geofence");
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentText("Geofence is recieveing location");
		builder.setPriority(-2);
		builder.setContentIntent(localPendingIntent);
		builder.setOnlyAlertOnce(true);
		startForeground(100, builder.build());
		Log.d("com.asdar.geofence", "foreground init");

	}

	public void makeUseOfLocation(Location loc) {
		ArrayList<SimpleGeofence> g = GeofenceUtils.getSimpleGeofences(mPrefs,
				getBaseContext());
		for (SimpleGeofence a : g) {
			float radius = a.getRadius()/1000;
			if (radius >= distance(loc.getLatitude(),loc.getLongitude(),a.getLatitude(),a.getLongitude())){
				Intent intent = new Intent(this, ReceiveTransitionsIntentService.class);
			}
		}
		Log.d("com.asdar.Geofence", "Lat: " + loc.getLatitude() + " Long: "
				+ loc.getLongitude());
	}

	public IBinder onBind(Intent paramIntent) {
		return this.binder;
	}

	public void onConnected(Bundle paramBundle) {
		Log.d("com.asdar.geofence",
				"location sercvice connected to location client");
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	public void onConnectionFailed(ConnectionResult paramConnectionResult) {
	}

	public void onDestroy() {
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	public void onDisconnected() {
		mLocationClient = null;
	}

	public void onLocationChanged(Location loc) {
		makeUseOfLocation(loc);
	}

	public void sendNotification() {

	}

	public class LocationBinder extends Binder {
		public LocationBinder() {
		}

		LocationService getService() {
			return LocationService.this;
		}
	}

	public static double distance(double lat1, double lon1, double lat2,
			double lon2) {

		final int R = 6371; // Radius of the earth
		Double dLat = Math.toRadians(lat2 - lat1);
		Double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		//
		Double distance = R * c;
		return distance;

	}
}
