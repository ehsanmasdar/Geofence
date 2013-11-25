package com.asdar.geofence;

import java.io.IOException;

import java.util.ArrayList;


import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;

import android.content.DialogInterface;
import android.location.Address;
import android.os.Bundle;

import org.holoeverywhere.app.DialogFragment;

public class AddressChooser extends DialogFragment {
    private Address selection;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle b = this.getArguments();
        ArrayList<String> convertedaddress = b.getStringArrayList("key");
        String[] conarray = new String[convertedaddress.size()];
        for (int i = 0; i < convertedaddress.size(); i++) {
            conarray[i] = convertedaddress.get(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Search Results").setItems(conarray, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                try {
                    mListener.onDialogClick0(AddressChooser.this, which);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return builder.create();

    }

    public Address getSelection() {
        return selection;
    }

    public interface AddressChooserListener {
        public void onDialogClick0(DialogFragment dialog, int selection) throws IOException;

    }

    // Use this instance of the interface to deliver action events
    AddressChooserListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddressChooserListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
