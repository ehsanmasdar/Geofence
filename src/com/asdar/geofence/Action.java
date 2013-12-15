package com.asdar.geofence;


import org.holoeverywhere.app.Dialog;

import android.content.Context;
import android.view.View;

public interface Action {
    public void execute(Context context);

    public String toString();

    public void commit(Context context, String id);
    public Dialog editDialog(Context context);

    public View addView(Context context, int position);
    public Action generateSavedState(Context context, String id);
    public String notificationText();
    public String getDescription();
}