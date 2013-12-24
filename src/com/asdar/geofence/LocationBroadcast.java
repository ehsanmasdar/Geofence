package com.asdar.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationBroadcast extends BroadcastReceiver {
	public void onReceive(Context c, Intent intent) {
		Log.d("com.asdar.geofence", "got broadcast");
		Intent service = new Intent(c, LocationService.class);
		c.startService(service);
	}
}
