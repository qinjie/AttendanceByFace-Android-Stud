package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.android.msahakyan.expandablenavigationdrawer.LogInActivity;

/**
 * Created by Lord One on 6/7/2016.
 */
public class GlobalVariable {
    public static final String apiKey = "8253a8dfd06d885e754ef8c596d4e809";
    public static final String apiSecret = "HlTQpKjISJ0Fxp1kkd4COSf12-_ErMrH";
    public static final int maxLengthFaceList = 5;
    public static ScheduleManager scheduleManager = new ScheduleManager();
    public static boolean loadedTimetableToday = false;
    public static final double imageArea = 200000;
    public static Activity activity;

    //+ Attendance History
    public static int currentSemester = 0;
    public static int currentSubjectView = 0;
    public static JSONObject currentAttendanceHistory = null;
    //- Attendance History

    //+ Timetable
    public static int currentTimeSelection = 0;
    public static int currentSubjectSelection = 0;
    public static JSONObject currentTimetable = null;
    //- Timetable

    //+ User Profile
    public static JSONObject currentUserProfile = null;
    //- User Profile

    //+ Subjects summary
    public static JSONObject subjectSummary = null;
    //- Subjects summary

    //+ Training Flat
    public static boolean isAllowedForTraining = false;
    public static boolean isNeededToTraining = false;
    //- Training Flat

    public static void rotateImage(String _imageDir) {
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(_imageDir, bounds);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(_imageDir, opts);
            ExifInterface exif = new ExifInterface(_imageDir);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Bitmap rotatedBitmap;
            if(rotationAngle != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
                rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
                File file = new File(_imageDir);
                file.delete();
                FileOutputStream out = new FileOutputStream(file);
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resizeImage(Activity activity, String mCurrentPhotoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        double ratio = Math.sqrt(GlobalVariable.imageArea / (oldHeight * oldWidth));

        int newWidth = (int) (oldWidth * ratio);
        int newHeight = (int) (oldHeight * ratio);
        Bitmap resized = bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        File file = new File(mCurrentPhotoPath);
        if(file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            resized.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            rotateImage(mCurrentPhotoPath);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static boolean haveFullTimetable(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String timeTable = pref.getString("fullTimeTable", null);
        return timeTable != null;
    }

    public static boolean obtainedAuCode (Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);
        if (auCode != null && auCode != "{\"password\":[\"Incorrect username or password.\"]}"){
            return true;
        }
        return false;
    }

    public static void setAuCodeInSP(Activity activity, String authorizationCode) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("authorizationCode", "Bearer " + authorizationCode);
        editor.apply();
    }

    public static String getThisPersonID(Activity activity, String auCode) {

        String personID = null;

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getPersonID();

        try {
            Response<ResponseBody> response = call.execute();
            JSONObject data = new JSONObject(response.body().string());
            personID = data.getString("person_id");
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return personID;
    }

    public static String get1FaceID(Activity activity, HttpRequests httpRequests, File imgFile) {
        String faceID = null;
        try {
            PostParameters postParameters = new PostParameters().setImg(imgFile).setMode("oneface");
            JSONObject faceResult = httpRequests.detectionDetect(postParameters);
            faceID = faceResult.getJSONArray("face").getJSONObject(0).getString("face_id");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if(faceID == null) {

        }
        return faceID;
    }

    public static String getMac(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        return address;
    }

    public static void checkConnected(final Activity activity) {
        String username = "123";
        String password = "321";
        StringClient client = ServiceGenerator.createService(StringClient.class);
        LoginClass up = new LoginClass(username, password, activity);

        Call<ResponseBody> call = client.login(up);
        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    int messageCode = response.code();
                    if(messageCode != 200 && messageCode != 400)
                        Notification.showMessage(activity, 6);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Notification.showMessage(activity, 6);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Notification.showMessage(activity, 6);
        }
    }

    public static void saveImageURL(Activity activity, String mCurrentPhotoPath) {
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        int lastIndex = pref.getInt("indexFaceList", -1);
        int currIndex = lastIndex + 1 >= GlobalVariable.maxLengthFaceList ? 0 : lastIndex + 1;

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("FaceList" + currIndex, mCurrentPhotoPath);
        editor.putInt("indexFaceList", currIndex);
        editor.apply();
    }

    public static void logoutAction(Activity activity){
        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.logout();

        Intent intent = new Intent(activity, LogInActivity.class);
        activity.startActivity(intent);
    }
}
