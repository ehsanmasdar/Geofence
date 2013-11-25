package com.asdar.geofence;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;

import com.dropbox.sync.android.DbxException;

public interface Action {
    public void execute(Context context);

    public String toString();

    public void commit(Context context, String id);
    public Dialog editDialog(Context context);

    public View addView(Context context, int position);
    public Action generateSavedState(Context context, String id) throws DbxException;
    public String notificationText();
    public String description();
}