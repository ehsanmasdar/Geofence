package com.asdar.geofence;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.google.android.gms.location.Geofence;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by Ehsan on 8/17/13.
 */
public class DropboxHelper extends Activity {
    private String APP_KEY;
    private String APP_SECRET;
    private DbxAccountManager mDbxAcctMgr;
    private DbxAccount mDbxAcct;
    private DbxDatastore store;
    private DbxTable geotable;
    static final int REQUEST_LINK_TO_DBX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APP_KEY = "a6kopt2el9go62x";
        APP_SECRET = "r5nhykcj43f0rbj";
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                APP_KEY, APP_SECRET);
        if (mDbxAcctMgr.hasLinkedAccount()) {
            mDbxAcctMgr.unlink();
            Toast.makeText(this, "Unlinked Account", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            dropboxinit();
        }
    }

    public void dropboxinit() {
        mDbxAcctMgr
                .startLink(DropboxHelper.this, REQUEST_LINK_TO_DBX);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
              /*   try {
                   mDbxAcct = mDbxAcctMgr.getLinkedAccount();
                    store = DbxDatastore.openDefault(mDbxAcct);
                    geotable = store.getTable("Geofence");
                    store.sync();
                    store.close();
                } catch (DbxException e) {
                    e.printStackTrace();
                }*/
                new DropboxInitFinish().execute();
                
                try {
                	DbxAccount mDbxAccount = mDbxAcctMgr.getLinkedAccount();
                    DbxDatastore store = DbxDatastore.openDefault(mDbxAccount);
					store.sync();                
					store.close();
				} catch (DbxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                Toast.makeText(this, "Successfully Linked Account", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Link Failed, Please Try Again Later", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public class DropboxInitFinish extends AsyncTask<String, String, String> {
        ProgressDialog dialog;

        public DropboxInitFinish() {

        }

        protected String doInBackground(String[] paramArrayOfString) {
            try {
                APP_KEY = "a6kopt2el9go62x";
                APP_SECRET = "r5nhykcj43f0rbj";
                mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                        APP_KEY, APP_SECRET);
                DbxAccount mDbxAccount = mDbxAcctMgr.getLinkedAccount();
                DbxDatastore store = DbxDatastore.openDefault(mDbxAccount);
                store.sync();
                geotable = store.getTable("Geofence");
                DbxRecord d = geotable.getOrInsert("-1");
                if (!d.hasField("mStartId")) {
                    d.set("mStartId", 0);
                    store.sync();
                }
            	store.close(); 
            }
             catch (DbxException e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String str) {            

            this.dialog.dismiss();

        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(DropboxHelper.this);
            this.dialog.setMessage("Finalizing Link...");
            this.dialog.show();

        }
    }
}
