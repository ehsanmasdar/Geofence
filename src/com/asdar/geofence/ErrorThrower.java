package com.asdar.geofence;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;

import android.content.DialogInterface;
import android.os.Bundle;

public class ErrorThrower extends DialogFragment {
    public static ErrorThrower newInstance(String message, Boolean kill) {
        ErrorThrower frag = new ErrorThrower();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putBoolean("kill", kill);
        frag.setArguments(args);
        return frag;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");
        final Boolean kill = getArguments().getBoolean("kill");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (kill) {
                    getActivity().finish();
                } else {
                    dialog.cancel();
                }
            }
        });
        return builder.create();
    }
}
