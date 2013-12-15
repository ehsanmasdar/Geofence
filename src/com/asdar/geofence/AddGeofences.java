package com.asdar.geofence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class AddGeofences extends Activity {
	private SharedPreferences mPrefs;
	private static ActionAdapter addadapter;
	private ListView addListView;
	private ArrayList<Action> actionlist;
	private AlertDialog mActionSelectionDialog;
	private boolean mActionChosen;
	private SimpleGeofence blah;

	public SimpleGeofence getGeofence() {
		return blah;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPrefs = getBaseContext().getSharedPreferences(
				GeofenceUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		String APP_KEY = "a6kopt2el9go62x";
		String APP_SECRET = "r5nhykcj43f0rbj";

		setContentView(R.layout.activity_add);
		actionlist = new ArrayList<Action>();
		addadapter = new ActionAdapter(getBaseContext(),
				R.layout.activity_add_actionlist, actionlist);
		addListView = (ListView) findViewById(R.id.addListView);
		addListView.setAdapter(addadapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.confirm:
			try {
				commit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.add:
			try {
				startAddActivity();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

		return true;
	}

	private void startAddActivity() throws ClassNotFoundException, IOException {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Action");
		ListView modeList = new ListView(this);
		final String[] uiOptions = GeofenceUtils
				.generateNames(getApplicationContext());
		final String[] classNames = GeofenceUtils
				.generateOptions(getApplicationContext());
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				uiOptions);
		OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
			@Override
			public void onItemClick(android.widget.AdapterView<?> adapterView,
					View view, int i, long l) {
				Action a = null;
				try {
					a = (Action) Class.forName(classNames[i]).newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mActionSelectionDialog.dismiss();
				a.editDialog(AddGeofences.this).show();
				actionlist.add(a);
			}
		};

		modeList.setAdapter(modeAdapter);
		modeList.setOnItemClickListener(mMessageClickedHandler);
		builder.setView(modeList);
		mActionSelectionDialog = builder.create();
		mActionSelectionDialog.show();

	}

	public static void refreshAddAdapter() {
		addadapter.notifyDataSetChanged();
	}

	public void commit() throws IOException {
		EditText addressedit = (EditText) findViewById(R.id.Addressedit);
		Geocoder geo = new Geocoder(getBaseContext());
		List<Address> s = geo.getFromLocationName(addressedit.getText()
				.toString(), 1);
		if (s.isEmpty()) {
			DialogFragment alert = ErrorThrower.newInstance(
					"Address not Found", false);
			alert.show(getSupportFragmentManager(), "adderror");
		} else {
			new CommitTask().execute();
			new RegisterTask().execute();
		}
	}

	public String buildAddress(Double latitude, Double logitude)
			throws IOException {
		Geocoder geo = new Geocoder(getBaseContext());
		String convert = " ";

		List<Address> georesults = geo.getFromLocation(latitude, logitude, 1);
		for (int i = 0; i < georesults.get(0).getMaxAddressLineIndex(); i++) {
			convert = convert + georesults.get(0).getAddressLine(i) + " ";

		}
		return convert;

	}

	@Override
	public void onBackPressed() {

		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	public class CommitTask extends AsyncTask<String, String, String> {
		ProgressDialog dialog;

		public CommitTask() {

		}

		@Override
		protected String doInBackground(String[] paramArrayOfString) {

			EditText addressedit = (EditText) findViewById(R.id.Addressedit);
			EditText radiusedit = (EditText) findViewById(R.id.Radiusedit);
			EditText namedit = (EditText) findViewById(R.id.NameAdd);
			Geocoder geo = new Geocoder(getBaseContext());
			List<Address> s = null;
			try {
				s = geo.getFromLocationName(addressedit.getText().toString(), 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Integer startidtemp = 0;
			Editor editor = mPrefs.edit();
			try {
				GeofenceStore geofencestorage = new GeofenceStore(
						AddGeofences.this);
				startidtemp = mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
						-1);
				SimpleGeofence g = new SimpleGeofence(startidtemp.toString(),
						namedit.getText().toString(), buildAddress(s.get(0)
								.getLatitude(), s.get(0).getLongitude()), s
								.get(0).getLatitude(), s.get(0).getLongitude(),
						Long.parseLong(radiusedit.getText().toString()),
						Geofence.NEVER_EXPIRE,
						Geofence.GEOFENCE_TRANSITION_ENTER
								| Geofence.GEOFENCE_TRANSITION_EXIT, 0, 0);
				geofencestorage.setGeofence(startidtemp.toString(), g);
				editor.putInt("com.asdar.geofence.KEY_STARTID", startidtemp + 1);
				GeofenceUtils.save(actionlist, editor, startidtemp.toString());
				blah = g;

			} catch (IOException e) {
				e.printStackTrace();
			}

			for (Action a : actionlist) {
				a.commit(getApplicationContext(), startidtemp.toString());
			}
			return startidtemp.toString();
		}

		@Override
		protected void onPostExecute(String str) {
			this.dialog.dismiss();
			finish();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.dialog = new ProgressDialog(AddGeofences.this);
			this.dialog.setMessage("Creating...");
			this.dialog.show();

		}
	}

	public class RegisterTask extends AsyncTask<String, String, String>
			implements ConnectionCallbacks, OnConnectionFailedListener,
			OnAddGeofencesResultListener, LocationListener {
		ProgressDialog dialog;
		boolean done;
		private LocationClient mLocationClient;

		public RegisterTask() {

		}

		@Override
		protected String doInBackground(String[] paramArrayOfString) {
			/*
			 * Test for Google Play services after setting the request type. If
			 * Google Play services isn't present, the proper request can be
			 * restarted.
			 */
			/*
			 * Create a new location client object. Since the current activity
			 * class implements ConnectionCallbacks and
			 * OnConnectionFailedListener, pass the current activity object as
			 * the listener for both parameters
			 */
			mLocationClient = new LocationClient(AddGeofences.this, this, this);

			LocationRequest mLocationRequest = new LocationRequest()
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
					.setInterval(30000);
			mLocationClient.connect();
			while (true) {
				if (done = true) {
					break;
				}

			}
			return "";
		}

		@Override
		protected void onPostExecute(String str) {
			this.dialog.dismiss();
			finish();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.dialog = new ProgressDialog(AddGeofences.this);
			this.dialog.setMessage("Registering...");
			this.dialog.show();

		}

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAddGeofencesResult(int statusCode, String[] arg1) {
			if (LocationStatusCodes.SUCCESS == statusCode) {
				done = true;
			}
		}

		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			// TODO Auto-generated method stub

		}
		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onConnected(Bundle arg0) {
			// Get the PendingIntent for the request
			PendingIntent mGeofencePendingIntent = getTransitionPendingIntent();
			// Send a request to add the current geofences
			// TODO debug string
			ArrayList<Geofence> list = new ArrayList<Geofence>();
			list.add(blah.toGeofence());
			mLocationClient.addGeofences(list, mGeofencePendingIntent, this);
		}
		private PendingIntent getTransitionPendingIntent() {
			// Create an explicit Intent
			Intent intent = new Intent(AddGeofences.this, ReceiveTransitionsIntentService.class);
			/*
			 * Return the PendingIntent
			 */
			return PendingIntent.getService(AddGeofences.this, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		}
	
	}

}
