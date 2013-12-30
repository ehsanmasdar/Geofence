package com.asdar.geofence;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;


public class EditGeofences extends FragmentActivity {
    private Integer idglob;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        idglob = i.getIntExtra("id", 0);
        
        setContentView(R.layout.activity_edit);
        try {
            populate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populate() throws IOException {
      
        EditText addressedit = (EditText) findViewById(R.id.Addressedit);
        EditText radiusedit = (EditText) findViewById(R.id.Radiusedit);
        EditText nameedit = (EditText) findViewById(R.id.NameEdit);
        Switch[] s = new Switch[GeofenceUtils.ActionNum];
        s[0] = (Switch) findViewById(R.id.EditSwitch1);
        Geocoder geo = new Geocoder(getBaseContext());
        List<Address> a = new ArrayList<Address>();
        String address = " ";
            SharedPreferences mPrefs = (SharedPreferences) getBaseContext()
                    .getSharedPreferences(GeofenceUtils.SHARED_PREFERENCES,
                            Context.MODE_PRIVATE);
            GeofenceStore geostore = new GeofenceStore(getBaseContext());

            a = geo.getFromLocation(mPrefs.getFloat(geostore
                    .getGeofenceFieldKey(idglob, GeofenceUtils.KEY_LATITUDE),
                    GeofenceUtils.INVALID_FLOAT_VALUE), mPrefs.getFloat(geostore
                    .getGeofenceFieldKey(idglob, GeofenceUtils.KEY_LONGITUDE),
                    GeofenceUtils.INVALID_FLOAT_VALUE), 1);
            address = mPrefs.getString(geostore.getGeofenceFieldKey(idglob, GeofenceUtils.KEY_ADDRESS), GeofenceUtils.INVALID_STRING_VALUE);
            Integer f = (int) mPrefs.getFloat(geostore.getGeofenceFieldKey(idglob, GeofenceUtils.KEY_RADIUS), GeofenceUtils.INVALID_FLOAT_VALUE);
            radiusedit.setText(f.toString(), EditText.BufferType.EDITABLE);
            nameedit.setText(mPrefs.getString(geostore.getGeofenceFieldKey(idglob, GeofenceUtils.KEY_NAME), GeofenceUtils.INVALID_STRING_VALUE), TextView.BufferType.EDITABLE);

        

        addressedit.setText(address, EditText.BufferType.EDITABLE);

    }

    public void commit() throws IOException {
     
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

            DialogFragment alert =  ErrorThrower.newInstance("Address not Found", false);
            alert.show(getSupportFragmentManager(), "editerror");

        } else {

            GeofenceStore geofencestorage = new GeofenceStore(this);
            Switch[] sw = new Switch[GeofenceUtils.ActionNum];
            sw[0] = (Switch) findViewById(R.id.EditSwitch1);
            Boolean[] b = new Boolean[GeofenceUtils.ActionNum];
            b[0] = sw[0].isChecked();
            SimpleGeofence g = new SimpleGeofence(idglob, namedit.getText().toString(), buildAddress(s.get(0).getLatitude(), s.get(0).getLongitude()), s
                    .get(0).getLatitude(), s.get(0).getLongitude(), (long)
                    Double.parseDouble(radiusedit.getText().toString()),
                    Geofence.NEVER_EXPIRE,
                    Geofence.GEOFENCE_TRANSITION_DWELL,120000,30000);
            geofencestorage.setGeofence(idglob, g);
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
       

        finish();
    }

    protected void onPause() {
        super.onPause();
       
    }

    @Override
    protected void onResume() {
        super.onResume();
       
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
