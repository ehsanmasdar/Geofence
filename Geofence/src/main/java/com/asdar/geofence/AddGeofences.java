package com.asdar.geofence;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AddGeofences extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBV15lTOpTwK2Jkv_zxWwfRyU8DsasucAY";
    private static ActionAdapter addadapter;
    private SharedPreferences mPrefs;
    private ListView addListView;
    private static ArrayList<Action> actionlist;
    private AlertDialog mActionSelectionDialog;
    private SimpleGeofence blah;
    private int radius = 100;
    private LocationClient mlocationClient;
    private Location currentLoc;
    private ListView exitListView;
    private ArrayList<Action> exitActionList;
    private ActionAdapter exitAdapter;
    private MergeAdapter m;
    private String finalName;
    private String finalAddress;

    public static void cancelLast() {
        actionlist.remove(actionlist.size() - 1);
        refreshAddAdapter();
    }


    public static void refreshAddAdapter() {
        addadapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlocationClient = new LocationClient(this, this, this);
        mlocationClient.connect();
        mPrefs = getBaseContext().getSharedPreferences(
                GeofenceUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        setContentView(R.layout.activity_add);
        //Enter
        actionlist = new ArrayList<Action>();
        addadapter = new ActionAdapter(getBaseContext(),
                R.layout.activity_add_actionlist, actionlist, false);
        //Exit
        exitActionList = new ArrayList<Action>();
        exitAdapter = new ActionAdapter(getBaseContext(),
                R.layout.activity_add_actionlist, exitActionList, true);
        addListView = (ListView) findViewById(R.id.addListViewEnter);
        m = new MergeAdapter();
        m.addAdapter(addadapter);
        m.addAdapter(exitAdapter);
        addListView.setAdapter(m);
        //Other content setup
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.confirm:
                try {
                    commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.add:
                try {
                    addActivity();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        return true;
    }


    private void addActivity() throws ClassNotFoundException, IOException {
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
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                mActionSelectionDialog.dismiss();
                Dialog d = a.editDialog(AddGeofences.this);
                d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        addadapter.notifyDataSetChanged();
                        exitAdapter.notifyDataSetChanged();
                        m.notifyDataSetChanged();
                    }
                });
                d.show();
                a.onDialogPostCreate(d);
                if ((actionlist.size() + exitActionList.size()) % 2 == 0) {
                    actionlist.add(a);
                } else {
                    exitActionList.add(a);
                }
            }
        };

        modeList.setAdapter(modeAdapter);
        modeList.setOnItemClickListener(mMessageClickedHandler);
        builder.setView(modeList);
        mActionSelectionDialog = builder.create();
        mActionSelectionDialog.show();
    }

    public void commit() throws IOException {
        final Geocoder geo = new Geocoder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.activity_add_confirm, null);
        final EditText nameedit = (EditText) v.findViewById(R.id.NameAdd);
        final AutoCompleteTextView addressedit = (AutoCompleteTextView) v.findViewById(R.id.AddressAdd);
        Spinner spinner = (Spinner) v.findViewById(R.id.RadiusAdd);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.distances_array_metric, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
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
        addressedit.setAdapter(new PlacesAutoCompleteAdapter(getApplicationContext(), R.layout.list_item));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setTitle("Global Settings");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (nameedit.getText().length() == 0) {
                    DialogFragment alert = ErrorThrower.newInstance(
                            "Enter a Name", false);
                    alert.show(getSupportFragmentManager(), "adderror");
                } else if (addressedit.getText().toString().length() < 1) {
                    DialogFragment alert = ErrorThrower.newInstance(
                            "Enter An Address", false);
                    alert.show(getSupportFragmentManager(), "adderror");
                } else try {
                    if ((geo.getFromLocationName(addressedit.getText().toString(), 1) == null) || (geo.getFromLocationName(addressedit.getText().toString(), 1).size() < 1)) {
                        DialogFragment alert = ErrorThrower.newInstance(
                                "Address not found", false);
                        alert.show(getSupportFragmentManager(), "adderror");
                    } else {
                        //Write out final vars
                        finalAddress = addressedit.getText().toString();
                        finalName = nameedit.getText().toString();
                        new CommitTask().execute();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.create().show();
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

    @Override
    public void onConnected(Bundle bundle) {
        currentLoc = mlocationClient.getLastLocation();
        mlocationClient.disconnect();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public String buildAddress(Address a) {
        String convert = "";
        for (int i = 0; i < a.getMaxAddressLineIndex(); i++) {
            convert = convert + a.getAddressLine(i) + " ";

        }
        return convert;

    }

    public class CommitTask extends AsyncTask<String, String, String> {
        ProgressDialog dialog;

        public CommitTask() {

        }

        @Override
        protected String doInBackground(String[] paramArrayOfString) {

            int startidtemp = 0;
            Editor editor = mPrefs.edit();
            List<Address> s = null;
            try {
                Geocoder geo = new Geocoder(getBaseContext());
                s = geo.getFromLocationName(finalAddress.toString(), 1);
            } catch (IOException e) {

                DialogFragment alert = ErrorThrower.newInstance(
                        "The devices` geocoder is disabled. The device must be restarted to add a geofence.", true);
                alert.show(getSupportFragmentManager(), "geocodererror");
            }
            GeofenceStore geofencestorage = new GeofenceStore(
                    AddGeofences.this);
            startidtemp = mPrefs.getInt("com.asdar.geofence.KEY_STARTID",
                    -1);
            SimpleGeofence g = new SimpleGeofence(
                    startidtemp,
                    finalName,
                    buildAddress(s.get(0)),
                    s.get(0).getLatitude(),
                    s.get(0).getLongitude(),
                    radius,
                    Geofence.NEVER_EXPIRE,
                    Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT,
                    0,
                    0
            );
            geofencestorage.setGeofence(startidtemp, g);
            editor.putInt("com.asdar.geofence.KEY_STARTID", startidtemp + 1);
            //Save enter
            GeofenceUtils.saveEnter(actionlist, editor, startidtemp);
            for (Action a : actionlist) {
                a.commit(getApplicationContext(), startidtemp, false);
            }
            //Save exit
            GeofenceUtils.saveExit(exitActionList, editor, startidtemp);
            for (Action a : exitActionList) {
                a.commit(getApplicationContext(), startidtemp, true);
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
            if (dialog == null) {
                this.dialog = new ProgressDialog(AddGeofences.this);
                this.dialog.setMessage("Creating...");
                this.dialog.show();
            }
        }
    }

    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
        private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
        private static final String OUT_JSON = "/json";
        private static final String API_KEY = "AIzaSyBV15lTOpTwK2Jkv_zxWwfRyU8DsasucAY";
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());
                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        private ArrayList<String> autocomplete(String s) {
            ArrayList<String> resultsList = null;
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            try {
                StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?input=" + URLEncoder.encode(s, "utf8"));
                if (currentLoc != null) {
                    sb.append("&location=" + currentLoc.getLatitude() + "," + currentLoc.getLongitude());
                    sb.append("&radius=" + 500);
                }
                sb.append("&sensor=true&key=" + API_KEY);
                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                Log.e("com.asdar.geofence", "Error processing Places API URL", e);
                return resultList;
            } catch (IOException e) {
                Log.e("com.asdar.geofence", "Error connecting to Places API", e);
                return resultList;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            try {
                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
                // Extract the Place descriptions from the results
                resultList = new ArrayList<String>(predsJsonArray.length());
                for (int i = 0; i < predsJsonArray.length(); i++) {
                    resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                }
            } catch (JSONException e) {
                Log.e("com.asdar.geofence", "Cannot process JSON results", e);
            }
            return resultList;
        }

    }
}
