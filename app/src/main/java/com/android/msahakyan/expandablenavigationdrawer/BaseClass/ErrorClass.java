package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Lord One on 6/14/2016.
 */
public class ErrorClass {
    private static String [] errorList = {
            "Error occurred while creating the Image file! Please check the memory of your phone and try again!", // 0
            "Error occurred! Please restart the application!", // 1
            "Error occurred while connecting to iBeacon! Please restart the application and try again!", // 2
            "Error occurred while connecting to local server! Please try again later! ", // 3
            "Invalid email. Please try again!", // 4
    };

    public static void showError(final Activity activity, final int errorCode) {

        activity.runOnUiThread(new Runnable() {
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                activity);

                        builder.setMessage(errorList[errorCode]);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

            }
        });


    }


}
