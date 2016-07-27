package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Lord One on 6/14/2016.
 */
public class ErrorClass {
    private static String [] errorList = {
            "Exception caught when loading Full Timetable - GlobalVariale.getFullTimeTable", // 0
            "Fatal error when login - LogInActivity.loginAction", // 1
            "Exception caught when login - LogInActivity.loginAction", // 2
            "Failed when call for response - LogInActivity.loginAction", // 3
            "Error occurred while creating the File - Training Fragment.dispatchTakePictureIntent ", // 4
            "Exception caught while resize image - GlobalVariable.resizeImage", // 5
            "Exception caught while checking if user is logged in - GlobalVariable.checkLoggedIn", //6
            "Error while call for response - GlobalVariable.checkLoggedIn", // 7
            "Exception caught when convert to JSONObject from response.body - GlobalVariable.loadTimetableByWeek", // 8
            "Error while call for response - GlobalVariable.loadTimetableByWeek", // 9
            "Exception caught while get response from local server - GlobalVariable.getThisPersonID", // 10
            "No face detected from image - GlobalVariable.get1FaceID", // 11
            "Exception caught while request face ID List from local server - TrainingFragment.getThisFaceIDList", // 12
            "Exception caught while modify the face ID List - TrainingFragment.substitute1FacefromPerson", //13
            "Exception caught while send face ID List to local server - TrainingFragment.postFaceIDListtoLocalServer", // 14
            "Receive message code != 200 - TrainingFragment.postFaceIDListtoLocalServer", // 15
            "Exception caught while create person and get person ID - TrainingFragment.create1Person", // 16
            "Exception caught while loading full time table - LoginActivity.onCreate", // 17
            "Exception caught while post person ID to local server - TrainingFragment.postPersonIDtoLocalServer", // 18
            "Exception caught when sending result to local server! - VerifyThread.sendResultToLocalServer", // 19
            "Failed to connect local server! - VerifyThread.sendResultToLocalServer", // 20
            "Exception caught while get result from Face++ - VerifyThread.getVerification", // 21
            "Exception caught while create File - DetailedInformationActivity.dispatchTakePictureIntent", // 22
            "Exception caught while connect to beacon - DetailedInformationActivity.beaconManager.connect", // 23
            "DetailedInformationActivity.initDetailedData", // 24
            "DetailedInformationActivity.getTime", // 25
            "DetailedInformationActivity.onCreate", // 26
            "Error occurs when sign up - SignUpActivity.signupAction", // 27
            "Connection error when sign up - SignUpActivity.signupAction", // 28
            "findViewById exception- DetailedInformationActivity.initDetailedData", // 29
            "Cannot detect any face from this photo - GlobalVariable.get1FaceID", // 30
            "Error 31, DetailedInformationActivity.VerifyThread.run", // 31
            "Error 32, Cannot detect any face in the photo", // 32
            "Error 33, Cannot check if now is valid time for checking attendance - DetailedInformationActivity.checkIfItsTime", // 33
            "Error 34, message code not 200 or 400 - RegisterNewDeviceActivity.register", // 34
            "Error 35, cannot execute call function - RegisterNewDeviceActivity.register", // 35
            "Error 36, message code returned out of range - ChangePasswordActivity.changePasswordAction", // 36
            "Error 37, cannot connect to Local Server. Please check your internet connection and try again later!", // 37
            "Error occurs when reset password", // 38
            "Connection error when reset password", // 39
            "Invalid email. Please try again!", // 40
            "Server failed! Please try again!", // 41
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
