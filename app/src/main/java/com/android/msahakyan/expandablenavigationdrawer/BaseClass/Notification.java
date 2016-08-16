package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Lord One on 6/14/2016.
 */
public class Notification {
    public static String [] messageList = {
            "Trained successfully!", // 0
            "Take Attendance successfully!", // 1
            "Your face doesn't match", // 2
            "You have to finish Training Face at least 3 times before Take Attendance!", // 3
            "Signed up successfully!\nYou must verify your email address before any further request", //4
            "Invalid data! Please try again!", // 5
            "You must connect to the internet!", // 6
            "Password changed successfully!", // 7
            "Incorrect password! Please try again!", // 8
            "Invalid new password! Please try again!", // 9
            "Reset password failed! Please try again!", // 10
            "Please check your email to reset your password!", // 11
            "Error occurred in local server! Please try again later!", //12
            "This email is not valid! Please try again!", // 13
            "This function need to be approved by your lecturer! Please try again later!", // 14
            "Please finish Setting->Face training before do another action!", // 15

    };

    public static String [] badRequestNotification = {
            "Incorrect username! Please try again!",            // 0
            "Incorrect password! Please try again!",            // 1
            "",                                                 // 2
            "Unverified email! Please try again!",              // 3
            "Unverified device! Please try again later!",       // 4
            "Unverified email device! Please try again later!", // 5
            "Invalid account",                                  // 6
            "Duplicate device"                                  // 7
    };

    public static void showMessage(final Activity activity, final int mesCode) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                activity);

                        builder.setMessage(messageList[mesCode]);
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

    public static void showBadRequestNotification(final Activity activity, final int notificationCode){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                activity);

                        builder.setMessage(badRequestNotification[notificationCode]);
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
