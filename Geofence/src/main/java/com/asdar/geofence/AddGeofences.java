package com.asdar.geofence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.location.Geofence;

public class AddGeofences extends ActionBarActivity {
	private SharedPreferences mPrefs;
	private static ActionAdapter addadapter;
	private ListView addListView;
	private ArrayList<Action> actionlist;
	private AlertDialog mActionSelectionDialog;
	private SimpleGeofence blah;
    private int radius = 100;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPrefs = getBaseContext().getSharedPreferences(
				GeofenceUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		
		setContentView(R.layout.activity_add);
		actionlist = new ArrayList<Action>();
		addadapter = new ActionAdapter(getBaseContext(),
				R.layout.activity_add_actionlist, actionlist);
		addListView = (ListView) findViewById(R.id.addListView);
		addListView.setAdapter(addadapter);
        Spinner spinner = (Spinner) findViewById(R.id.RadiusAdd);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.distances_array_metric, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        radius = 100;
                        break;
                    case 1:
                        radius = 250;
                        break;
                    case 2:
                        radius = 500;
                        break;
                    case 3:
                        radius = 750;
                        break;
                    case 4:
                        radius = 1000;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        EditText nameedit = (EditText) findViewById(R.id.NameAdd);
		EditText addressedit = (EditText) findViewById(R.id.AddressAdd);
		Geocoder geo = new Geocoder(getBaseContext());
		List<Address> s = geo.getFromLocationName(addressedit.getText()
				.toString(), 1);
        if (nameedit.getText().length() == 0){
            DialogFragment alert =  ErrorThrower.newInstance(
                    "Please Enter a Name", false);
            alert.show(getSupportFragmentManager(), "adderror");
        }
		else if (s.isEmpty()) {
			DialogFragment alert =  ErrorThrower.newInstance(
					"Address not Found", false);
			alert.show(getSupportFragmentManager(), "adderror");
		}
        else {
			new CommitTask().execute();
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

			EditText addressedit = (EditText) findViewById(R.id.AddressAdd);
			EditText namedit = (EditText) findViewById(R.id.NameAdd);
			Geocoder geo = new Geocoder(getBaseContext());
			List<Address> s = null;
			try {
				s = geo.getFromLocationName(addressedit.getText().toString(), 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			int startidtemp = 0;
			Editor editor = mPrefs.edit();
			try {
				GeofenceStore geofencestorage = new GeofenceStore(
						AddGeofences.this);
				startidtemp = mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
						-1);
				SimpleGeofence g = new SimpleGeofence(startidtemp, namedit
						.getText().toString(), buildAddress(s.get(0)
						.getLatitude(), s.get(0).getLongitude()), s.get(0)
						.getLatitude(), s.get(0).getLongitude(),
						radius,
						Geofence.NEVER_EXPIRE,
						Geofence.GEOFENCE_TRANSITION_ENTER
								| Geofence.GEOFENCE_TRANSITION_EXIT, 0, 0);
				geofencestorage.setGeofence(startidtemp, g);
				editor.putInt("com.asdar.geofence.KEY_STARTID", startidtemp + 1);
				GeofenceUtils.save(actionlist, editor, startidtemp);
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (Action a : actionlist) {
				a.commit(getApplicationContext(), startidtemp);
			}
			return "";
		}

		@Override
		protected void onPostExecute(String str) {
            Intent stop = new Intent();
            stop.setAction("com.asdar.geofence.locationstop");
            sendBroadcast(stop);

            Intent start = new Intent();
            start.setAction("com.asdar.geofence.locationstart");
            sendBroadcast(start);
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
}
