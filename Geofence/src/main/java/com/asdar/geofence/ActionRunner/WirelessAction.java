package com.asdar.geofence.ActionRunner;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.asdar.geofence.Action;

/**
 * Created by s2094505 on 2/3/14.
 */
public class WirelessAction implements Action {
    @Override
    public void execute(Context context) {

    }

    @Override
    public void commit(Context context, int id) {

    }

    @Override
    public Dialog editDialog(Context context) {
        return null;
    }

    @Override
    public View addView(Context context, int position) {
        return null;
    }

    @Override
    public Action generateSavedState(Context context, int id) {
        return null;
    }

    @Override
    public String notificationText() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String listText() {
        return null;
    }

    @Override
    public int getIcon() {
        return 0;
    }
}
