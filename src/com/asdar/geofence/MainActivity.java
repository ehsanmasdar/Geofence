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
	private EventListAdapter eventlistadapter;
	private double upperRightLongitude;
	private Location currentloc;
	private GeofenceStore geofencestorage;
	private List<Geofence> currentGeofences;
	private SharedPreferences mPrefs;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
	private static final String SHARED_PREFERENCES = "SharedPreferences";
	private ArrayList<SimpleGeofence> currentSimpleGeofences;
	private LocationRequest mLocationRequest;
	private ArrayList<SimpleGeofence> locallist;
	private int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT >= 9) {
			// checkGeocoder();
		}
		mPrefs = getBaseContext().getSharedPreferences(SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		mLocationRequest = new LocationRequest().setPriority(
				LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(30000);
		mInProgress = false;
		if (mPrefs.getInt("com.asdar.geofence.KEY_STARTID", -1) == -1) {
            int startidtemp = 0;
            Editor editor = mPrefs.edit();
            editor.putInt("com.asdar.geofence.KEY_STARTID", startidtemp);
            editor.commit();
        }
		geofencestorage = new GeofenceStore(this);
		currentGeofences = new ArrayList<Geofence>();
		currentSimpleGeofences = new ArrayList<SimpleGeofence>();

		for (Integer i = 0; i < mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
				-1); i++) {
			
			currentSimpleGeofences.add(geofencestorage.getGeofence(
					i.toString(), getApplicationContext()));
			currentGeofences.add(geofencestorage.getGeofence(i.toString(),
					getApplicationContext()).toGeofence());
		}

		CharSequence text = "startid"
				+ mPrefs.getInt("com.asdar.geofence.KEY_STARTID", -1);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(getBaseContext(), text, duration);
		//toast.show();
		//show layout
		setContentView(R.layout.activity_main);
		//Initialize drawer
    	OnItemClickListener drawerItemClick = new OnItemClickListener() {
			@Override
			public void onItemClick(android.widget.AdapterView<?> adapterView,
					View view, int i, long l) {
				
			}
		};
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		ArrayList<String> draweritems = new ArrayList<String>();
		draweritems.add("Home"); 
		for (int i = 0; i < currentSimpleGeofences.size(); i++){
			draweritems.add(currentSimpleGeofences.get(i).getName());
		}
		ArrayAdapter<String> a = new ArrayAdapter<String> (this,R.layout.drawer_list_item,draweritems);
		mDrawerList.setAdapter(a);
        mDrawerList.setOnItemClickListener(drawerItemClick);
        //Initialize listview
		OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
			@Override
			public void onItemClick(android.widget.AdapterView<?> adapterView,
					View view, int i, long l) {

				Intent intent = new Intent(getBaseContext(),
						EditGeofences.class);
				intent.putExtra("id", i);
				startActivity(intent);

			}
		};
		listView = (ListView) findViewById(R.id.cardListView);
		listView.setOnItemClickListener(mMessageClickedHandler);
		eventlistadapter = new EventListAdapter(getBaseContext(),
				R.layout.activity_main_listrow, currentSimpleGeofences);
		listView.setAdapter(eventlistadapter);
		addGeofences();
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
		GeofenceStore ge = new GeofenceStore(this);
		ArrayList<SimpleGeofence> localcurrentSimpleGeofences = new ArrayList<SimpleGeofence>();
		for (Integer i = 0; i < mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
				-1); i++) {
			localcurrentSimpleGeofences.add(ge.getGeofence(i.toString(),
					getApplicationContext()));
		}

		GeofenceUtils.update(eventlistadapter, localcurrentSimpleGeofences);
		eventlistadapter.notifyDataSetChanged();
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
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
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
		Log.d("com.asdar.geofence", "started add geofence");

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

	

	/*
	 * public void mapopen(View view) throws IOException { EditText editText =
	 * (EditText) findViewById(R.id.edit_message); String message =
	 * editText.getText().toString(); Geocoder g = new
	 * Geocoder(getBaseContext());
	 * 
	 * try { results = g .getFromLocationName(message, 1000, lowerLeftLatitude,
	 * lowerLeftLongitude, upperRightLatitude, upperRightLongitude); } catch
	 * (IOException e) { e.printStackTrace(); } if (results.isEmpty()) {
	 * noResultsExist(); } else { georesults = getClosest(results);
	 * resultsExist(); }
	 * 
	 * }
	 */

	public void editopen(View view) throws IOException {

		Intent intent = new Intent(getBaseContext(), EditGeofences.class);
		ArrayList<SimpleGeofence> currentSimpleGeofences = new ArrayList<SimpleGeofence>();

		for (Integer i = 0; i < mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
				-1); i++) {
			currentSimpleGeofences.add(geofencestorage.getGeofence(
					i.toString(), getBaseContext()));
		}

		startActivity(intent);
	}

	public void addopen(View view) throws IOException {

		Intent intent = new Intent(getBaseContext(), AddGeofences.class);
		startActivity(intent);
	}

	private List<GeofenceAddress> getClosest(List<Address> results2) {

		double lat1 = currentloc.getLatitude();
		double lon1 = currentloc.getLongitude();
		ArrayList<GeofenceAddress> geoadd = new ArrayList<GeofenceAddress>(
				results2.size());
		for (Address results : results2) {
			geoadd.add(new GeofenceAddress(results, lat1, lon1));

		}

		Collections.sort(geoadd);
		/*
		 * String str = " "; int numlines =
		 * results2.get(smallest).getMaxAddressLineIndex(); for (int j = 0; j <
		 * numlines; j++) { str = str +
		 * results2.get(smallest).getAddressLine(j); } boolean isAlreadyinList =
		 * false; for (int a = 0; a < closestfew.size();a++){ String convert =
		 * " "; int numlines2 = results2.get(i).getMaxAddressLineIndex(); for
		 * (int j = 0; j < numlines2; j++) { convert = convert +
		 * results2.get(i).getAddressLine(j); } if (convert.compareTo(str)==0){
		 * isAlreadyinList = true; } } if (isAlreadyinList){
		 * donotsearch.add(smallest); }
		 */
		// else{
		// }

		// int numlines =
		// results2.get(smallest).getMaxAddressLineIndex();

		// String convert = "What Address I choose ";
		// for (int j = 0; j < numlines; j++) {
		// convert = convert
		// + results2.get(smallest).getAddressLine(j);
		// }
		// convert = convert + "At Lat:"
		// + results.get(smallest).getLatitude();
		// convert = convert + "At Lon:"
		// + results.get(smallest).getLongitude();
		// System.out.println(convert);

		// System.out.println("What Distance I choose:"
		// + distances.get(smallest));
		if (geoadd.size() >= 5) {
			return geoadd.subList(0, 5);

		} else {
			return geoadd;
		}

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
		currentloc = location;
		// TODO debug string
		Log.d("com.asdar.geofence", "Lat" + location.getLatitude() + "Long"
				+ location.getLongitude());
		lowerLeftLongitude = location.getLongitude();
		lowerLeftLatitude = location.getLatitude();
		upperRightLongitude = location.getLongitude();
		upperRightLatitude = location.getLatitude();

		if (upperRightLongitude + .5 > 180) {
			upperRightLongitude = 180;
		} else {
			upperRightLongitude += .5;
		}

		if (lowerLeftLongitude - .5 < -180) {
			lowerLeftLongitude = -180;
		} else {
			lowerLeftLongitude -= .5;
		}

		if (lowerLeftLatitude - .5 < -90) {
			lowerLeftLatitude = -90;
		} else {
			lowerLeftLatitude -= .5;
		}

		if (upperRightLatitude + .5 > 90) {
			upperRightLatitude = 90;
		} else {
			upperRightLatitude += .5;
		}

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

	public void resultsExist() {
		ArrayList<String> convertedaddress = new ArrayList<String>();
		for (int i = 0; i < georesults.size(); i++) {
			String convert = " ";
			int numlines = georesults.get(i).getAddress()
					.getMaxAddressLineIndex();

			if (georesults.get(i).getAddress().getFeatureName() != null) {
				convert = georesults.get(i).getAddress().getFeatureName() + ":"
						+ "\n";
				for (int j = 0; j < numlines; j++) {
					convert = convert
							+ georesults.get(i).getAddress().getAddressLine(j)
							+ " ";
				}
			} else {
				/*
				 * convert = convert + results.get(i).getSubThoroughfare() + " "
				 * + results.get(i).getThoroughfare() + " " +
				 * results.get(i).getSubAdminArea() + "," +
				 * results.get(i).getAdminArea() + " " +
				 * results.get(i).getCountryName() + " " +
				 * results.get(i).getPostalCode();
				 */
				for (int j = 0; j < numlines; j++) {
					convert = convert
							+ georesults.get(i).getAddress().getAddressLine(j);
				}
			}

			convertedaddress.add(convert);
		}
		Bundle b = new Bundle();
		b.putStringArrayList("key", convertedaddress);
		d = new AddressChooser();
		d.setArguments(b);
		d.show(getSupportFragmentManager());
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


	/*
	 * public void onDialogClick0(DialogFragment dialog, final int selection)
	 * throws IOException { final double[] latlng = new double[2]; latlng[0] =
	 * results.get(selection).getLatitude(); latlng[1] =
	 * results.get(selection).getLongitude(); final double[] latlng2 = latlng;
	 * AlertDialog.Builder builder; builder = new
	 * AlertDialog.Builder(MainActivity.this); builder.setMessage("Choices");
	 * builder.setNegativeButton("Add Geofence", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * 
	 * if (!mDbxAcctMgr.hasLinkedAccount()) { Double startidtemp = (double)
	 * mPrefs.getInt("com.asdar.geofence.KEY_STARTID", -1); SimpleGeofence s =
	 * null; try { s = new SimpleGeofence( startidtemp.toString(),
	 * "Placeholder", buildAddress(latlng2[0], latlng2[1]), latlng2[0],
	 * latlng2[1], 100, Geofence.NEVER_EXPIRE,
	 * Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT,
	 * new Action(getBaseContext())); } catch (IOException e) {
	 * e.printStackTrace(); }
	 * geofencestorage.setGeofence(startidtemp.toString(), s);
	 * 
	 * currentGeofences.add(geofencestorage.getGeofence( startidtemp.toString(),
	 * getBaseContext()).toGeofence()); Editor editor = mPrefs.edit();
	 * editor.putInt("com.asdar.geofence.KEY_STARTID",
	 * mPrefs.getInt("com.asdar.geofence.KEY_STARTID", -1) + 1);
	 * editor.commit(); } else { try { if (!store.isOpen()) { store =
	 * DbxDatastore.openDefault(mDbxAcct);
	 * 
	 * } Double startidtemp = geotable.get("-1").getDouble("mStartId");
	 * geotable.getOrInsert(startidtemp.toString()) .set("mId",
	 * startidtemp.toString()) .set("mLatitude", latlng[0]) .set("mLongitude",
	 * latlng[1]) .set("mRadius", Double.valueOf(100))
	 * .set("mExpirationDuration", Geofence.NEVER_EXPIRE)
	 * .set("mTransitionType", Geofence.GEOFENCE_TRANSITION_ENTER |
	 * Geofence.GEOFENCE_TRANSITION_EXIT) .set("mAudioMute", true);
	 * store.sync(); recordlist = geotable.query().asList(); SimpleGeofence g =
	 * new SimpleGeofence( recordlist.get((int)
	 * geotable.get("-1").getDouble("mStartId")), getBaseContext());
	 * currentGeofences.add(geofencestorage .getGeofence(startidtemp.toString(),
	 * getBaseContext()) .toGeofence()); geotable.get("-1").set("mStartId",
	 * geotable.get("-1").getDouble("mStartId") + 1); store.close(); } catch
	 * (DbxException e) { e.printStackTrace(); } } if
	 * (mDbxAcctMgr.hasLinkedAccount()) { CharSequence text = "t"; try { text =
	 * "start id:" + geotable.get("-1").getDouble("mStartId") + 1; } catch
	 * (DbxException e) { e.printStackTrace(); } int duration =
	 * Toast.LENGTH_SHORT; Toast toast = Toast.makeText(getBaseContext(), text,
	 * duration); toast.show(); } else { CharSequence text = "startid" +
	 * mPrefs.getInt("com.asdar.geofence.KEY_STARTID", -1); int duration =
	 * Toast.LENGTH_SHORT; Toast toast = Toast.makeText(getBaseContext(), text,
	 * duration); toast.show(); }
	 * 
	 * } }); builder.setPositiveButton("Map", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * 
	 * Intent intent = new Intent(getBaseContext(), DisplayMap.class);
	 * intent.putExtra(LOC, latlng2); intent.putExtra(Feature,
	 * results.get(selection) .getFeatureName()); startActivity(intent); } });
	 * 
	 * AlertDialog a = builder.create(); a.show(); }
	 * 
	 * public String buildAddress(Double latitude, Double logitude) throws
	 * IOException { Geocoder geo = new Geocoder(getBaseContext()); String
	 * convert = " ";
	 * 
	 * List<Address> georesults = geo.getFromLocation(latitude, logitude, 1);
	 * for (int i = 0; i < georesults.get(0).getMaxAddressLineIndex(); i++) {
	 * convert = convert + georesults.get(0).getAddressLine(i) + " ";
	 * 
	 * } return convert;
	 * 
	 * }
	 */
}
