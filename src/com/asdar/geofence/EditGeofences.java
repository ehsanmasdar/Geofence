package com.asdar.geofence;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.DialogFragment;

import android.content.SharedPreferences;

import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Switch;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxTable;
import com.google.android.gms.location.Geofence;


public class EditGeofences extends Activity {
    private Integer idglob;
    private DbxAccountManager mDbxAcctMgr;
    private DbxDatastore store;
    private DbxAccount mDbxAcct;
    private DbxTable geotable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String APP_KEY = "a6kopt2el9go62x";
        Intent i = getIntent();
        idglob = i.getIntExtra("id", 0);
        String APP_SECRET = "r5nhykcj43f0rbj";
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                APP_KEY, APP_SECRET);
        if (mDbxAcctMgr.hasLinkedAccount()) {
            try {
                mDbxAcct = mDbxAcctMgr.getLinkedAccount();
                store = DbxDatastore.openDefault(mDbxAcct);
                store.sync();
            } catch (DbxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            geotable = store.getTable("Geofence");
        }
        setContentView(R.layout.activity_edit);
        try {
            populate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populate() throws IOException {
        if (mDbxAcctMgr.hasLinkedAccount()) {
            if (!store.isOpen()) {
                store = DbxDatastore.openDefault(mDbxAcct);
            }
        }
        EditText addressedit = (EditText) findViewById(R.id.Addressedit);
        EditText radiusedit = (EditText) findViewById(R.id.Radiusedit);
        EditText nameedit = (EditText) findViewById(R.id.NameEdit);
        Switch[] s = new Switch[GeofenceUtils.ActionNum];
        s[0] = (Switch) findViewById(R.id.EditSwitch1);
        Geocoder geo = new Geocoder(getBaseContext());
        List<Address> a = new ArrayList<Address>();
        String address = " ";
        if (mDbxAcctMgr.hasLinkedAccount()) {
            store.close();
            if (!store.isOpen()) {
                store = DbxDatastore.openDefault(mDbxAcct);
            }
            address = geotable.get(idglob.toString()).getString("mAddress");

            Integer f = (int) geotable.getOrInsert(idglob.toString()).getDouble(
                    "mRadius");
            radiusedit.setText(f.toString(), EditText.BufferType.EDITABLE);
            nameedit.setText(geotable.getOrInsert(idglob.toString()).getString("mName"), TextView.BufferType.EDITABLE);
            store.close();
        } else {
            SharedPreferences mPrefs = (SharedPreferences) getBaseContext()
                    .getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
                            Context.MODE_PRIVATE);
            GeofenceStore geostore = new GeofenceStore(getBaseContext());

            a = geo.getFromLocation(mPrefs.getFloat(geostore
                    .getGeofenceFieldKey(idglob.toString(), GeofenceUtils.KEY_LATITUDE),
                    GeofenceUtils.INVALID_FLOAT_VALUE), mPrefs.getFloat(geostore
                    .getGeofenceFieldKey(idglob.toString(), GeofenceUtils.KEY_LONGITUDE),
                    GeofenceUtils.INVALID_FLOAT_VALUE), 1);
            address = mPrefs.getString(geostore.getGeofenceFieldKey(idglob.toString(), GeofenceUtils.KEY_ADDRESS), GeofenceUtils.INVALID_STRING_VALUE);
            Integer f = (int) mPrefs.getFloat(geostore.getGeofenceFieldKey(idglob.toString(), GeofenceUtils.KEY_RADIUS), GeofenceUtils.INVALID_FLOAT_VALUE);
            radiusedit.setText(f.toString(), EditText.BufferType.EDITABLE);
            nameedit.setText(mPrefs.getString(geostore.getGeofenceFieldKey(idglob.toString(), GeofenceUtils.KEY_NAME), GeofenceUtils.INVALID_STRING_VALUE), TextView.BufferType.EDITABLE);

        }

        addressedit.setText(address, EditText.BufferType.EDITABLE);

    }

    public void commit() throws IOException {
        if (mDbxAcctMgr.hasLinkedAccount()) {
            if (!store.isOpen()) {
                store = DbxDatastore.openDefault(mDbxAcct);

            }
            geotable = store.getTable("Geofence");
        }
        SharedPreferences mPrefs = (SharedPreferences) getBaseContext().getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        EditText addressedit = (EditText) findViewById(R.id.Addressedit);
        EditText radiusedit = (EditText) findViewById(R.id.Radiusedit);
        EditText namedit = (EditText) findViewById(R.id.NameEdit);
        Geocoder geo = new Geocoder(getBaseContext());
        //TODO debug string
        System.out.println("Address edit.tostring"
                + addressedit.getText().toString());
        List<Address> s = geo.getFromLocationName(addressedit.getText()
                .toString(), 1);
        if (s.isEmpty()) {

            DialogFragment alert = ErrorThrower.newInstance("Address not Found", false);
            alert.show(getSupportFragmentManager(), "editerror");

        } else if (!mDbxAcctMgr.hasLinkedAccount()) {

            GeofenceStore geofencestorage = new GeofenceStore(this);
            Switch[] sw = new Switch[GeofenceUtils.ActionNum];
            sw[0] = (Switch) findViewById(R.id.EditSwitch1);
            Boolean[] b = new Boolean[GeofenceUtils.ActionNum];
            b[0] = sw[0].isChecked();
            SimpleGeofence g = new SimpleGeofence(idglob.toString(), namedit.getText().toString(), buildAddress(s.get(0).getLatitude(), s.get(0).getLongitude()), s
                    .get(0).getLatitude(), s.get(0).getLongitude(), (long)
                    Double.parseDouble(radiusedit.getText().toString()),
                    Geofence.NEVER_EXPIRE,
                    Geofence.GEOFENCE_TRANSITION_DWELL,120000,30000);
            geofencestorage.setGeofence(idglob.toString(), g);
            finish();

        } else {


            geotable.getOrInsert(idglob.toString())
                    .set("mId", idglob.toString())
                    .set("mName", namedit.getText().toString())
                    .set("mAddress", buildAddress(s.get(0).getLatitude(), s.get(0).getLongitude()))
                    .set("mLatitude", s.get(0).getLatitude())
                    .set("mLongitude", s.get(0).getLongitude())
                    .set("mRadius",
                            Double.parseDouble(radiusedit.getText()
                                    .toString()))
                    .set("mExpirationDuration", Geofence.NEVER_EXPIRE)
                    .set("mTransitionType",
                            Geofence.GEOFENCE_TRANSITION_ENTER
                                    | Geofence.GEOFENCE_TRANSITION_EXIT);
            Switch[] sw = new Switch[GeofenceUtils.ActionNum];
            sw[0] = (Switch) findViewById(R.id.EditSwitch1);
            Boolean[] b = new Boolean[GeofenceUtils.ActionNum];
            b[0] = sw[0].isChecked();
            store.sync();
            store.close();
            finish();
        }
    }

    public String buildAddress(Double latitude, Double logitude) throws IOException {
        Geocoder geo = new Geocoder(getBaseContext());
        String convert = " ";
        List<Address> georesults = geo.getFromLocation(latitude, logitude, 1);
        for (int i = 0; i < georesults.get(0).getMaxAddressLineIndex(); i++) {
            convert = convert
                    + georesults.get(0).getAddressLine(i) + " ";

        }
        return convert;

    }

    public void onBackPressed() {
        if (mDbxAcctMgr.hasLinkedAccount()) {
            if (store.isOpen()) {
                store.close();
            }
        }

        finish();
    }

    protected void onPause() {
        super.onPause();
        if (mDbxAcctMgr.hasLinkedAccount()) {
            if (store.isOpen()) {
                store.close();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDbxAcctMgr.hasLinkedAccount()) {

            try {
                if (!store.isOpen()) {
                    store = DbxDatastore.openDefault(mDbxAcct);
                }
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirmedit:
                try {
                    commit();
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
}
