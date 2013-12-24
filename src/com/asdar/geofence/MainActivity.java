package com.asdar.geofence;

import java.io.IOException;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.Toast;

import android.view.Menu;

import android.widget.AdapterView.OnItemClickListener;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import android.view.MenuInflater;
import android.view.MenuItem;
import com.asdar.geofence.GeofenceUtils.REQUEST_TYPE;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import com.asdar.geofence.AudioMuteAction;

public class MainActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, OnAddGeofencesResultListener,
		LocationListener {

	public final static String LOC = "com.asdar.geofence.LOC";
	public final static String Feature = "com.asdar.geofence.Feature";
	private DialogFragment d;
	private List<Address> results;
	private List<GeofenceAddress> georesults;
	private double lowerLeftLatitude;
	private double lowerLeftLongitude;
	private double upperRightLatitude;
	private LocationClient mLocationClient;
	private PendingIntent mGeofencePendingIntent;
	private ListView listView;
	private boolean mInProgress;
	private REQUEST_TYPE mRequestType;
	private double upperRightLongitude;
	public  Location currentloc;
	private GeofenceStore geofencestorage;
	private List<Geofence> currentGeofences;
	private SharedPreferences mPrefs;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private static final String SHARED_PREFERENCES = "SharedPreferences";
	private ArrayList<SimpleGeofence> currentSimpleGeofences;
	private ArrayList<SimpleGeofence> locallist;
	private int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private boolean homeFragment;
	private ArrayAdapter<String> draweradapter;
	private ArrayList<String> draweritems;
	private ActionBarDrawerToggle mDrawerToggle;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT >= 9) {
			// checkGeocoder();
		}
		mPrefs = getBaseContext().getSharedPreferences(SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		Intent intent = new Intent();
		intent.setAction("com.asdar.geofence.locationstart");
		sendBroadcast(intent);
		mInProgress = false;
		if (mPrefs.getInt("com.asdar.geofence.KEY_STARTID", -1) == -1) {
			int startidtemp = 0;
			Editor editor = mPrefs.edit();
			editor.putInt("com.asdar.geofence.KEY_STARTID", startidtemp);
			editor.commit();
		}
		geofencestorage = new GeofenceStore(this);
		currentGeofences = new ArrayList<Geofence>();
		currentSimpleGeofences = GeofenceUtils.getSimpleGeofences(mPrefs, getApplicationContext());
		for (Integer i = 0; i < mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
				-1); i++) {
			currentGeofences.add(currentSimpleGeofences.get(i).toGeofence());
		}
		addGeofences();
		CharSequence text = "startid"
				+ mPrefs.getInt("com.asdar.geofence.KEY_STARTID", -1);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(getBaseContext(), text, duration);
		// toast.show();
		// show layout
		setContentView(R.layout.activity_main);
		// Initialize drawer
		OnItemClickListener drawerItemClick = new OnItemClickListener() {
			@Override
			public void onItemClick(android.widget.AdapterView<?> adapterView,
					View view, int i, long l) {
				swapFragment(i - 2);
			}
		};

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(getTitle());
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Select Geofence");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		draweritems = new ArrayList<String>();
		draweritems.add("Home");
		draweritems.add("Map");
		for (int i = 0; i < currentSimpleGeofences.size(); i++) {
			draweritems.add(currentSimpleGeofences.get(i).getName());
		}
		draweradapter = new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, draweritems);
		mDrawerList.setAdapter(draweradapter);
		mDrawerList.setOnItemClickListener(drawerItemClick);
		swapFragment(-2);
	}

	public void swapFragment(int i) {
		if (i == -2) {
			HomeFragment fragment = new HomeFragment();

			// Insert the fragment by replacing any existing fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
			// Highlight the selected item, update the title, and close the
			// drawer
			mDrawerList.setItemChecked(0, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			homeFragment = true;
			setTitle("Geofence");
		}
		else if (i == -1){
			MapFragment fragment = new MapFragment();

			// Insert the fragment by replacing any existing fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
			// Highlight the selected item, update the title, and close the
			// drawer
			mDrawerList.setItemChecked(1, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			homeFragment = true;
			setTitle("Geofence");
		}

		else {
			ActionFragment fragment = new ActionFragment();
			Bundle args = new Bundle();
			args.putInt("id", i);
			fragment.setArguments(args);

			// Insert the fragment by replacing any existing fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
			// Highlight the selected item, update the title, and close the
			// drawer
			mDrawerList.setItemChecked(i+2, true);
			mDrawerLayout.closeDrawer(mDrawerList);
			Integer a = i;
			setTitle(new GeofenceStore(getApplicationContext()).getGeofence(a, getApplicationContext()).getName());

		}
	}
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		regenerateList();
	}
	public void regenerateList() {
		SharedPreferences mPrefs = (SharedPreferences) getApplicationContext().getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
		GeofenceStore ge = new GeofenceStore(getApplicationContext());
		ArrayList<String> simpleGeofenceNames = new ArrayList<String>();
		simpleGeofenceNames.add("Home");
		simpleGeofenceNames.add("Map");
		for (Integer i = 0; i < mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
				-1); i++) {
			simpleGeofenceNames.add(ge.getGeofence(i,
					getApplicationContext()).getName());
		}

		GeofenceUtils.update(draweradapter, simpleGeofenceNames);
		draweradapter.notifyDataSetChanged();
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void checkGeocoder() {
		if (!Geocoder.isPresent()) {
			DialogFragment alert = ErrorThrower
					.newInstance(
							"Geocoding is required for this app. \n The app will now exit.",
							true);
			alert.show(getSupportFragmentManager(), "addresses");
		}
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		// TODO debug string
		Log.d("com.asdar.geofence", "connected to location service (mainactivity)");
		switch (mRequestType) {
		case ADD:
			// Get the PendingIntent for the request
			mGeofencePendingIntent = getTransitionPendingIntent();
			// Send a request to add the current geofences
			if (currentGeofences.size() > 0) {
				// TODO debug string
				mLocationClient.addGeofences(currentGeofences,
						mGeofencePendingIntent, this);
			}
			break;
		}
		currentloc = mLocationClient.getLastLocation();
	}

	// Implementation of OnConnectionFailedListener.onConnectionFailed
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// Turn off the request flag
		mInProgress = false;
		/*
		 * If the error has a resolution, start a Google Play services activity
		 * to resolve it.
		 */
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this,
						GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
			// If no resolution is available, display an error dialog
		} else {
			// Get the error code
			int errorCode = connectionResult.getErrorCode();
			// Get the error dialog from Google Play services
			android.app.Dialog errorDialog = GooglePlayServicesUtil
					.getErrorDialog(errorCode, this,
							CONNECTION_FAILURE_RESOLUTION_REQUEST);
			errorDialog.show();

			/*
			 * // If Google Play services can provide an error dialog if
			 * (errorDialog != null) { // Create a new DialogFragment for the
			 * error dialog DialogFragment errorFragment = new DialogFragment();
			 * // Set the dialog in the DialogFragment
			 * errorFragment.setDialog(errorDialog); // Show the error dialog in
			 * the DialogFragment errorFragment.show(
			 * getSupportFragmentManager()); }
			 */

		}
	}

	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
		// If adding the geofences was successful
		if (LocationStatusCodes.SUCCESS == statusCode) {
			// TODO debug string
		} else {
			Log.e("com.asdar.geofence", "statuscode" + " " + statusCode);
			switch (statusCode) {
			case 1000:
				ErrorThrower
						.newInstance(
								"This app requires Location Services to be enabled. \n The app will now exit.",
								true).show(getSupportFragmentManager(),
								"Error 1000");
				break;
			case 1001:
				ErrorThrower
						.newInstance(
								"You have over 100 Geofences.\n The app wil cease to function till you have less than 100.",
								false).show(getSupportFragmentManager(),
								"Error 1001");

			}
		}
		// Turn off the in progress flag and disconnect the client
		mInProgress = false;
		mLocationClient.disconnect();
	}

	@Override
	public void onDisconnected() {
		// Turn off the request flag
		mInProgress = false;
		// Destroy the current location client
		mLocationClient = null;
	}

	private PendingIntent getTransitionPendingIntent() {
		// Create an explicit Intent
		Intent intent = new Intent(this, ReceiveTransitionsIntentService.class);
		/*
		 * Return the PendingIntent
		 */
		return PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	public void addGeofences() {
		// Start a request to add geofences
		mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;
		// TODO: debug string

		/*
		 * Test for Google Play services after setting the request type. If
		 * Google Play services isn't present, the proper request can be
		 * restarted.
		 */
		/*
		 * Create a new location client object. Since the current activity class
		 * implements ConnectionCallbacks and OnConnectionFailedListener, pass
		 * the current activity object as the listener for both parameters
		 */
		mLocationClient = new LocationClient(this, this, this);
		// If a request is not already underway
		if (!mInProgress) {
			// Indicate that a request is underway
			mInProgress = true;
			// Request a connection from the client to Location Services
			// TODO debug string
			Log.d("com.asdar.geofence", "started connection");
			mLocationClient.connect();
		} else {
			/*
			 * A request is already underway. You can handle this situation by
			 * disconnecting the client, re-setting the flag, and then re-trying
			 * the request.
			 */
		}
	}


	public void editopen(View view) throws IOException {

		Intent intent = new Intent(getBaseContext(), EditGeofences.class);
		ArrayList<SimpleGeofence> currentSimpleGeofences = new ArrayList<SimpleGeofence>();

		for (Integer i = 0; i < mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
				-1); i++) {
			currentSimpleGeofences.add(geofencestorage.getGeofence(
					i, getBaseContext()));
		}

		startActivity(intent);
	}

	public void addopen(View view) throws IOException {

		Intent intent = new Intent(getBaseContext(), AddGeofences.class);
		startActivity(intent);
	}

	

	public static double distance(double lat1, double lon1, double lat2,
			double lon2) {

		final int R = 6371; // Radius of the earth
		Double dLat = toRad(lat2 - lat1);
		Double dLon = toRad(lon2 - lon1);
		lat1 = toRad(lat1);
		lat2 = toRad(lat2);
		Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		//
		Double distance = R * c;
		return distance;

	}

	private static Double toRad(Double value) {
		return value * Math.PI / 180;
	}

	public void onLocationChanged(Location location) {
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onProviderDisabled(String provider) {
	}

	@Override
	public String toString() {
		return "MainActivity [lowerLeftLatitude=" + lowerLeftLatitude
				+ ", lowerLeftLongitude=" + lowerLeftLongitude
				+ ", upperRightLatitude=" + upperRightLatitude
				+ ", upperRightLongitude=" + upperRightLongitude + "]";
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent i = new Intent(getBaseContext(), SettingsActivity.class);
			startActivity(i);
			break;
		case R.id.add:
			Toast.makeText(this, "Add started", Toast.LENGTH_SHORT).show();
			try {
				addopen(new View(getBaseContext()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

		return true;
	}
	public Location getCurrentloc() {
		return currentloc;
	}

	public  void setCurrentloc(Location currentloc) {
		this.currentloc = currentloc;
	}

}