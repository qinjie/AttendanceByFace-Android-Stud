package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Tung on 5/31/2016.
 */
public interface StringClient {

    @GET("api/home")
    Call<String> getString();

    @GET("user/logout")
    Call<ResponseBody> logout();

    @GET("api/check-student")
    Call<ResponseBody> checkStudent();

    @GET("user/person-id")
    Call<ResponseBody> getPersonID();

    @GET("user/face-id")
    Call<ResponseBody> getFaceIDList();

    @GET("timetable/week?week=1")
    Call<ResponseBody> getTimetableByWeek();

    @GET("timetable/total-week")
    Call<ResponseBody> getFullTimetable();

    @GET("timetable/today")
    Call<ResponseBody> getTimetableToday();

    @GET("attendance/list-semester")
    Call<ResponseBody> getListSemesters();

    @GET("attendance/list-class-section")
    Call<ResponseBody> getListClasses(@Query("semester") String semester);

    @GET("attendance/attendance-history")
    Call<ResponseBody> getAttendanceHistory(@Query("semester") String semester);

    @GET("timetable/next-days")
    Call<ResponseBody> getNextDays(@Query("days") int k);

    @GET("timetable/one-day")
    Call<ResponseBody> getOneDay(@Query("date") String date);

    @GET("user/check-train-face")
    Call<ResponseBody> checkTrainFace();

    @POST("user/student-login")
    Call<ResponseBody> login(@Body LoginClass up);

    @POST("user/signup-student")
    Call<ResponseBody> signup(@Body SignupClass user);

    @POST("user/reset-password")
    Call<ResponseBody> resetPassword(@Body JsonObject email);

    @POST("user/set-face-id")
    Call<ResponseBody> postFaceIDList(@Body ArrayList<String> face_id);

    @POST("user/set-person-id")
    Call<ResponseBody> postPersonID(@Body String person_id);

    @POST("timetable/check-attendance")
    Call<ResponseBody> atAttendanceTime(@Body JsonObject isOntime);

    @POST("timetable/take-attendance")
    Call<ResponseBody> takeAttendance(@Body TakeAttendanceClass toUp);

    @POST("user/register-device")
    Call<ResponseBody> registerDevice(@Body JsonObject toUp);

    @POST("user/change-password")
    Call<ResponseBody> changePassword(@Body JsonObject toUp);

    @GET("student/profile")
    Call<ResponseBody> getStudentProfile();
}
