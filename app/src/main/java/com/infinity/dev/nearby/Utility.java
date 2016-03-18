package com.infinity.dev.nearby;

/**
 * Created by suny on 6/8/15.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;


public class Utility {

    Context context;

    public Utility(Context context){
        this.context = context;
    }

    public void showSettingsDialog(){
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();

        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("Want to enable GPS");

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Settings",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showDialog(){
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        alertDialog.setMessage("No network available");
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Retry",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isNetworkAvailable()) {
                            showDialog();
                        }
                    }
                });
        alertDialog.show();
    }

}
