package com.android.msahakyan.expandablenavigationdrawer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ErrorClass;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.GlobalVariable;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.Notification;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ServiceGenerator;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.StringClient;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.TakeAttendanceClass;
import com.android.msahakyan.expandablenavigationdrawer.Fragment.TakeAttendanceTodayFragment;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DetailedInformationActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;

    private BeaconManager beaconManager;
    private Region region;

    TextView tvBeaconInRangeBtn;
    Button mCaptureImageBtn;

    boolean remindDiscover = false;
    int currentIndex;
    boolean isTakeAttendance;
    JSONObject subject;

    Animation animation = null;

    boolean inStage;
    boolean outStage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_information);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        this.setTitle("Detailed Information");

        beaconManager = new BeaconManager(this);

        currentIndex = GlobalVariable.scheduleManager.currentLessionIndex;
        isTakeAttendance = GlobalVariable.scheduleManager.isTakeAttendance(currentIndex);
        inStage = isTakeAttendance;
        JSONArray schedule = GlobalVariable.scheduleManager.getDailySchedule();
        try
        {
            subject = schedule.getJSONObject(currentIndex);

            String UUIDs = subject.getString("uuid");
            int major = Integer.parseInt(subject.getString("major"));
            int minor = Integer.parseInt(subject.getString("minor"));

            region = new Region("ranged region", UUID.fromString(UUIDs), major, minor);

        } catch (Exception e)
        {
            e.printStackTrace();
            ErrorClass.showError(this, 26);
        }

        getSubjectInformation();
        initDetailedData();

        tvBeaconInRangeBtn = (TextView) findViewById(R.id.tv_beaconInRange);
        tvBeaconInRangeBtn.setWidth(120);
        tvBeaconInRangeBtn.setLines(2);

        mCaptureImageBtn = (Button) findViewById(R.id.btn_captureImage);
        mCaptureImageBtn.setWidth(120);
        mCaptureImageBtn.setLines(2);

        SharedPreferences pref = this.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            setButtonsInvisible();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enable :)
                setButtonsInvisible();
            }
            else
            {
                if(isTakeAttendance)
                    setButtonsInvisible();
                else{
                    new CheckIfShowTakeButton().execute(auCode);
                }
            }
        }
    }

    void setButtonsInvisible() {
        tvBeaconInRangeBtn.setVisibility(Button.INVISIBLE);
        mCaptureImageBtn.setVisibility(Button.INVISIBLE);
    }

    void setButtonsVisible() {
        initBeaconEvent();
        initBlinkingButton();

        tvBeaconInRangeBtn.setVisibility(Button.VISIBLE);
        tvBeaconInRangeBtn.setTextColor(Color.WHITE);
        String message = "SEARCHING" + System.getProperty ("line.separator")
                + "BEACON...";

        tvBeaconInRangeBtn.setText(message);
        tvBeaconInRangeBtn.setTextColor(Color.RED);
        tvBeaconInRangeBtn.setGravity(Gravity.CENTER);
        tvBeaconInRangeBtn.startAnimation(animation);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF);
        gd.setCornerRadius(5);
        gd.setStroke(1, 0xFFCC0000);
        tvBeaconInRangeBtn.setBackgroundDrawable(gd);

        mCaptureImageBtn.setVisibility(Button.INVISIBLE);
        mCaptureImageBtn.setBackgroundColor(Color.parseColor("#008000"));
        mCaptureImageBtn.setTextColor(Color.WHITE);
        addListenerToCaptureImageBtn();
    }

    protected void getSubjectInformation()
    {
        currentIndex = GlobalVariable.scheduleManager.currentLessionIndex;
        isTakeAttendance = GlobalVariable.scheduleManager.isTakeAttendance(currentIndex);
        JSONArray schedule = GlobalVariable.scheduleManager.getDailySchedule();
        try
        {
            subject = schedule.getJSONObject(currentIndex);

            String UUIDs = subject.getString("uuid");
            int major = Integer.parseInt(subject.getString("major"));
            int minor = Integer.parseInt(subject.getString("minor"));

            region = new Region("ranged region", UUID.fromString(UUIDs), major, minor);

        } catch (Exception e)
        {
            e.printStackTrace();
            ErrorClass.showError(this, 26);
        }
    }

    private void initBlinkingButton()
    {
        animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
    }

    private void addListenerToCaptureImageBtn()
    {
        mCaptureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                if (remindDiscover)
                {
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    String getTime()
    {
        String result = "";
        try
        {
            String time = subject.getString("start_time");
            String weekDay = subject.getString("weekday");

            result = time + " " + weekDay;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ErrorClass.showError(this, 25);
        }

        return result;
    }

    public void initDetailedData()
    {
        try
        {
            int status = Integer.parseInt(subject.getString("status"));
            for(int i = 0; i < 7; i++)
            {
                TextView tv = new TextView(this);
                String text = null;
                int id = -1;

                switch (i)
                {
                    case 0:
                        text = subject.getString("subject_area");
                        id = R.id.tv1;
                        break;

                    case 1:
                        text = subject.getString("class_section");
                        id = R.id.tv2;
                        break;

                    case 2:
                        text = subject.getString("lecturer_name");
                        id = R.id.tv3;
                        break;

                    case 3:
                        text = subject.getString("location");
                        id = R.id.tv4;
                        break;

                    case 4:
                        text = getTime();
                        id = R.id.tv5;
                        break;

                    case 5:
                        id = R.id.tv6;

                        switch (status) {
                            case 0:
                                tv = (TextView) findViewById(id);
                                text = "NOT YET";
                                tv.setText(text);
                                tv.setTextColor(Color.parseColor("#C0C0C0"));
                                break;
                            case 1:
                                tv = (TextView) findViewById(id);
                                text = "PRESENT";
                                tv.setText(text);
                                tv.setTextColor(Color.parseColor("#00FF7F"));
                                break;
                            case 2:
                                tv = (TextView) findViewById(id);
                                text = "LATE";
                                tv.setText(text);
                                tv.setTextColor(Color.parseColor("#FFA500"));
                                break;
                            case 3:
                                tv = (TextView) findViewById(id);
                                text = "ABSENT";
                                tv.setText(text);
                                tv.setTextColor(Color.parseColor("#CC0000"));
                                break;
                        }
                        break;

                    case 6:
                        text = subject.getString("recorded_at");
                        if (text.equals("null"))
                        {
                            text = "--:--";
                        }

                        id = R.id.tv7;
                        break;
                }

                try
                {
                    if (i != 5)
                    {
                        tv = (TextView) findViewById(id);
                        tv.setText(text);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    ErrorClass.showError(this, 29);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ErrorClass.showError(this, 24);
        }
    }

    private void initBeaconEvent()
    {
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon = list.get(0);

                    int txPower = beacon.getMeasuredPower();
                    double rssi = beacon.getRssi();
                    double distance = calculateDistance(txPower, rssi);

                    if (distance <= 5.0) {
                        if (!remindDiscover) {
                            remindDiscover = true;
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailedInformationActivity.this);
                            builder2.setMessage("You are in right location!");
                            builder2.setCancelable(true);

                            builder2.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert12 = builder2.create();
                            alert12.show();

                            tvBeaconInRangeBtn.clearAnimation();
                            String message = "BEACON" + System.getProperty ("line.separator")
                                    + "IN RANGE!";

                            tvBeaconInRangeBtn.setText(message);
                            tvBeaconInRangeBtn.setTextColor(Color.parseColor("#FF00FF7F"));
                            tvBeaconInRangeBtn.setGravity(Gravity.CENTER);

                            GradientDrawable gd = new GradientDrawable();
                            gd.setColor(0xFFFFFFFF);
                            gd.setCornerRadius(5);
                            gd.setStroke(1, 0xFF00FF7F);
                            tvBeaconInRangeBtn.setBackgroundDrawable(gd);

                            mCaptureImageBtn.setVisibility(Button.VISIBLE);
                            mCaptureImageBtn.startAnimation(animation);
                        }
                    } else {
                        remindDiscover = false;

                        String message = "SEARCHING" + System.getProperty ("line.separator")
                                + "BEACON...";

                        tvBeaconInRangeBtn.setText(message);
                        tvBeaconInRangeBtn.setTextColor(Color.RED);
                        tvBeaconInRangeBtn.setGravity(Gravity.CENTER);

                        GradientDrawable gd = new GradientDrawable();
                        gd.setColor(0xFFFFFFFF);
                        gd.setCornerRadius(5);
                        gd.setStroke(1, 0xFFCC0000);
                        tvBeaconInRangeBtn.setBackgroundDrawable(gd);

                        tvBeaconInRangeBtn.startAnimation(animation);

                        mCaptureImageBtn.clearAnimation();
                        mCaptureImageBtn.setVisibility(Button.INVISIBLE);
                    }
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try
                {
                    String UUIDs = subject.getString("uuid");
                    int major = Integer.parseInt(subject.getString("major"));
                    int minor = Integer.parseInt(subject.getString("minor"));

                    region = new Region("ranged region", UUID.fromString(UUIDs), major, minor);
                    beaconManager.startRanging(region);

//                    beaconManager.startMonitoring(new Region("monitored region",
//                            UUID.fromString(UUIDs), major, minor));
                } catch (Exception e)
                {
                    e.printStackTrace();
                    ErrorClass.showError(DetailedInformationActivity.this, 23);
                }
            }
        });

//        try
//        {
//            String UUIDs = subject.getString("uuid");
//            int major = Integer.parseInt(subject.getString("major"));
//            int minor = Integer.parseInt(subject.getString("minor"));
//
//            region = new Region("ranged region", UUID.fromString(UUIDs), major, minor);
//
//            beaconManager.startRanging(region);

//            beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
//                @Override
//                public void onEnteredRegion(Region region, List<Beacon> list) {
//                    AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailedInformationActivity.this);
//                    builder2.setMessage("onEnteredRegion!");
//                    builder2.setCancelable(true);
//
//                    builder2.setPositiveButton(
//                            "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    AlertDialog alert12 = builder2.create();
//                    alert12.show();
//                }
//
//                @Override
//                public void onExitedRegion(Region region) {
//                    AlertDialog.Builder builder2 = new AlertDialog.Builder(DetailedInformationActivity.this);
//                    builder2.setMessage("onExitedRegion!");
//                    builder2.setCancelable(true);
//
//                    builder2.setPositiveButton(
//                            "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    AlertDialog alert12 = builder2.create();
//                    alert12.show();
//                }
//            });

        beaconManager.setForegroundScanPeriod(5000, 2000);
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }

    protected static double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return 1000.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                ErrorClass.showError(this, 22);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);

        outStage = GlobalVariable.scheduleManager.isTakeAttendance(currentIndex);
        if (inStage != outStage)
        {
            resultIntent.putExtra(TakeAttendanceTodayFragment.UPDATE_SCHEDULE_VIEW, 1);
        }
        else
        {
            resultIntent.putExtra(TakeAttendanceTodayFragment.UPDATE_SCHEDULE_VIEW, 0);
        }

        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);

                outStage = GlobalVariable.scheduleManager.isTakeAttendance(currentIndex);
                if (inStage != outStage)
                {
                    resultIntent.putExtra(TakeAttendanceTodayFragment.UPDATE_SCHEDULE_VIEW, 1);
                }
                else
                {
                    resultIntent.putExtra(TakeAttendanceTodayFragment.UPDATE_SCHEDULE_VIEW, 0);
                }
                
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            VerifyThread verifyThread = new VerifyThread(mCurrentPhotoPath, this, beaconManager, region);
            verifyThread.start();
        }
        else
        {
            mCaptureImageBtn.startAnimation(animation);
        }
    }

    private class CheckIfShowTakeButton extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... auCode) {
            String result = "false";
            StringClient client = ServiceGenerator.createService(StringClient.class, auCode[0]);

            JSONArray schedule = GlobalVariable.scheduleManager.getDailySchedule();
            try {
                int timetableID = ((JSONObject) schedule.get(currentIndex)).getInt("timetable_id");
                JsonObject toUp = new JsonObject();
                toUp.addProperty("timetable_id", timetableID);
                Call<ResponseBody> call = client.atAttendanceTime(toUp);
                Response<ResponseBody> response = call.execute();

                int mesCode = response.code();
                if(mesCode != 200) throw new Exception();
                String body = response.body().string();
                JSONObject data = new JSONObject(body);
                result = data.getString("result");

            }
            catch (Exception e){
                ErrorClass.showError(DetailedInformationActivity.this, 33);
            }

            return (result.compareTo("true") == 0);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                setButtonsVisible();
            }
            else
            {
                TextView notification = new TextView(getApplicationContext());
                notification.setText("This subject is not available to take attendance now.");
                notification.setTextSize(16);
                notification.setTextColor(Color.RED);
                notification.setGravity(Gravity.CENTER);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                notification.setLayoutParams(params);

                GradientDrawable gd = new GradientDrawable();
                gd.setColor(0xFFFFFFFF);
                gd.setCornerRadius(5);
                gd.setStroke(1, 0xFFCC0000);
                notification.setBackgroundDrawable(gd);

                LinearLayout display = (LinearLayout) findViewById(R.id.layout_report);
                display.removeAllViews();
                display.addView(
                        notification
                );

            }
        }
    }
}

class VerifyThread extends Thread{
    Thread t;
    String mCurrentPhotoPath = null;
    Activity activity;
    BeaconManager beaconManager;
    Region region;
    public VerifyThread(String _mCurrentPhotoPath, Activity _activity, BeaconManager _beaconManager, Region _region) {
        mCurrentPhotoPath = _mCurrentPhotoPath;
        activity = _activity;
        beaconManager = _beaconManager;
        region = _region;
    }

    public void run() {

        Preferences.showLoading(activity, "Verifying on progress ...", "");

        HttpRequests httpRequests = new HttpRequests(GlobalVariable.apiKey, GlobalVariable.apiSecret);
        File imgFile = new File(mCurrentPhotoPath);
        GlobalVariable.resizeImage(activity, mCurrentPhotoPath);

        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        String personID = GlobalVariable.getThisPersonID(activity, auCode);
        if(personID.compareTo("") != 0){
            String faceID = GlobalVariable.get1FaceID(activity, httpRequests, imgFile);
            if(faceID != null) {
                double result = getVerification(httpRequests, personID, faceID);
                final JSONObject serverResult = sendResultToLocalServer(result);

                try {
                    String record_at = serverResult.getString("recorded_at");
                    if (!record_at.isEmpty()) {
                        GlobalVariable.scheduleManager.updateSchedule(serverResult);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                beaconManager.stopRanging(region);

                                Button captureImage = (Button) activity.findViewById(R.id.btn_captureImage);
                                TextView beaconInRange = (TextView) activity.findViewById(R.id.tv_beaconInRange);

                                captureImage.clearAnimation();
                                beaconInRange.clearAnimation();

                                captureImage.setVisibility(Button.INVISIBLE);
                                beaconInRange.setVisibility(Button.INVISIBLE);

                                TextView status = (TextView) activity.findViewById(R.id.tv6);
                                TextView record_at = (TextView) activity.findViewById(R.id.tv7);
                                try {
                                    String result = serverResult.getString("is_late");
                                    if (result.compareTo("true") == 0) {
                                        status.setText("LATE");
                                        status.setTextColor(Color.parseColor("#FFA500"));
                                    } else {
                                        status.setText("PRESENT");
                                        status.setTextColor(Color.parseColor("#00FF7F"));
                                    }

                                    record_at.setText(serverResult.getString("recorded_at"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    ErrorClass.showError(activity, 31);
                }
            }
        }
        else{
            Notification.showMessage(activity, 3);
        }
        Preferences.dismissLoading();
    }

    JSONObject sendResultToLocalServer(double result) {

        int currentIndex = GlobalVariable.scheduleManager.currentLessionIndex;
        JSONArray schedule = GlobalVariable.scheduleManager.getDailySchedule();
        try {
            int timetableID = ((JSONObject) schedule.get(currentIndex)).getInt("timetable_id");

            TakeAttendanceClass toUp = new TakeAttendanceClass(timetableID, result);

            SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
            String auCode = pref.getString("authorizationCode", null);

            StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
            Call<ResponseBody> call = client.takeAttendance(toUp);
            Response<ResponseBody> response = call.execute();
            int resCode = response.code();

            if(resCode == 200) { // successful
                String resStr = response.body().string();
                JSONObject resJson = new JSONObject(resStr);
                Notification.showMessage(activity, 1);
                return (resJson);
            }
            else if(resCode == 400) { // face doesn't match
                Notification.showMessage(activity, 2);
                return null;
            }
            else {
                ErrorClass.showError(activity, 20);
                return null;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            ErrorClass.showError(activity, 19);
        }
        return null;
    }

    private double getVerification(HttpRequests httpRequests, String personID, String faceID) {
        double result = 0;
        PostParameters postParameters = new PostParameters().setPersonId(personID).setFaceId(faceID);
        try{
            JSONObject fppResult = httpRequests.recognitionVerify(postParameters);
            result = fppResult.getDouble("confidence");
            if(!fppResult.getBoolean("is_same_person"))
                result = 100 - result;
        }
        catch(Exception e){
            System.out.print("Process interrupted!");
            ErrorClass.showError(activity, 21);
        }

        return result;
    }

}

