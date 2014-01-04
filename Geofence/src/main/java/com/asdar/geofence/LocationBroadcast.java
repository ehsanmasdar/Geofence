package com.asdar.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationBroadcast extends BroadcastReceiver {
	public void onReceive(Context c, Intent intent) {
		Intent service = new Intent(c, LocationService.class);
		c.startService(service);
	}
}
