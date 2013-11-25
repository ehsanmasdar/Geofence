package com.asdar.geofence;


import org.holoeverywhere.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DisplayMap extends Activity {
    private GoogleMap map;
    private double latitude;
    private double longitude;

    @SuppressWarnings("null")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //      String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //   Geocoder g = new Geocoder (getBaseContext());
       
/*			try {
                ad = g.getFromLocationName (message, 10);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
        if (ad.size() >0){
        	 latitude = ad.get(0).getLatitude();
        	 longitude =ad.get(0).getLongitude();
        }
        */
        double[] latlng = intent.getDoubleArrayExtra(MainActivity.LOC);
        if (latlng == null) {
            latlng[0] = 0;
            latlng[1] = 0;
        }
        String feature = intent.getStringExtra(MainActivity.Feature);
        latitude = latlng[0];
        longitude = latlng[1];
        LatLng loc = new LatLng(latitude, longitude);
        LatLng loc1 = new LatLng(0, 0);
        GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getApplicationContext());
        setContentView(R.layout.activity_display_map);
        map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc1, 2));
        map.addMarker(new MarkerOptions().position(loc).title(feature));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

}
