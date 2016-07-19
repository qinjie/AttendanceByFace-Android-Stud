package com.android.msahakyan.expandablenavigationdrawer.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.OnSwipeTouchListener;
import com.android.msahakyan.expandablenavigationdrawer.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.app.Fragment;
import android.view.Gravity;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.GlobalVariable;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ServiceGenerator;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.StringClient;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ScheduleManager;
import com.android.msahakyan.expandablenavigationdrawer.Preferences;
import com.android.msahakyan.expandablenavigationdrawer.DetailedInformationActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TakeAttendanceTodayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TakeAttendanceTodayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TakeAttendanceTodayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Calendar calendar;
    private TableLayout[] tls = new TableLayout[2];
    TextView textTime;

    private View myView;

    private Activity context;

    private static final int MY_CHILD_ACTIVITY = 1;
    public static final String UPDATE_SCHEDULE_VIEW = "updateSchedule";

    private OnFragmentInteractionListener mListener;

    private CountDownTimer timer;

    private boolean isServerRespond = false;

    private ScrollView scrollView;

    private static final int YESTERDAY = 0;
    private static final int TODAY = 1;
    private static final int TOMORROW = 2;

    int[] rainbow;

    public TakeAttendanceTodayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TakeAttendanceTodayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TakeAttendanceTodayFragment newInstance(String param1, String param2) {
        TakeAttendanceTodayFragment fragment = new TakeAttendanceTodayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalVariable.checkConnected(getActivity());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = this.getActivity();
        calendar = Calendar.getInstance();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    int getTime(String time)
    {
        int temp_index = time.lastIndexOf(":");
        String temp_current_time = String.valueOf(time.subSequence(0, temp_index));
        int current_time = Integer.parseInt(temp_current_time);

        return current_time;
    }

    private void getTableLayout()
    {
        tls[0] = (TableLayout) myView.findViewById(R.id.tableLayout1);
        tls[1] = (TableLayout) myView.findViewById(R.id.tableLayout2);
    }

    private void displayTimeColumn()
    {
        for(int i = 0; i < ScheduleManager.timeNumber; i++)
        {
            List<String> values = new ArrayList<>();
            values.add(ScheduleManager.dailyTime[i]);

            TextView tvs = new TextView(context);

            tvs.setGravity(Gravity.CENTER);
            if (i % 2 == 1)
            {
                tvs.setBackgroundColor(Color.parseColor("#bbdefb"));
            }
            else
            {
                tvs.setBackgroundColor(Color.WHITE);
            }

            tvs.setText(values.get(0));

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            params.height = 140;
            tvs.setLayoutParams(params);

            TableRow trs = new TableRow(context);
            TableLayout.LayoutParams layoutRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trs.setLayoutParams(layoutRow);

            trs.addView(tvs);

            tls[0].addView(trs);
        }
    }

    private void createSubjectView(boolean isExistSubject, final int startTime, final int endTime, final int index, final JSONObject subject)
    {
        if (!isExistSubject)
        {
            for(int i = startTime; i < endTime; i++)
            {
                TextView tvs = new TextView(context);
                //tvs.setLines(5);

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                params.height = 140;
                tvs.setLayoutParams(params);

                TableRow trs = new TableRow(context);
                TableLayout.LayoutParams layoutRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                trs.setLayoutParams(layoutRow);

                trs.addView(tvs);

                tls[1].addView(trs);
            }

            return;
        }

        try
        {
            TextView tvs = new TextView(context);
            String temp;
            String result = "";
            String subject_area = subject.getString("subject_area");
            String class_section = subject.getString("class_section");
            String lecturer_name = subject.getString("lecturer_name");

            temp = subject_area + " " + class_section + System.getProperty ("line.separator")
                    + lecturer_name + System.getProperty ("line.separator");
            result += temp;

            temp = subject.getString("location");
            result += temp;

            tvs.setGravity(Gravity.CENTER);
            tvs.setTextColor(Color.WHITE);

            tvs.setText(result);

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            params.height = 140 * (endTime - startTime);
            tvs.setLayoutParams(params);

            GradientDrawable gd = new GradientDrawable();

            final int status = Integer.parseInt(subject.getString("status"));
            switch (status) {
                case 0:
                    gd.setColor(0xFFC0C0C0);
                    break;
                case 1:
                    gd.setColor(0xFF00FF7F);
                    break;
                case 2:
                    gd.setColor(0xFFFFA500);
                    break;
                case 3:
                    gd.setColor(0xFFCC0000);
                    break;
            }


            gd.setCornerRadius(5);
            gd.setStroke(1, 0xFF000000);
            tvs.setBackgroundDrawable(gd);

            TableRow trs = new TableRow(context);
            TableLayout.LayoutParams layoutRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trs.setLayoutParams(layoutRow);

            trs.addView(tvs);

            tls[1].addView(trs);

            tvs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariable.scheduleManager.setCurrentLession(index);
                    Intent intend = new Intent(context, DetailedInformationActivity.class);
                    startActivityForResult(intend, MY_CHILD_ACTIVITY);


                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (MY_CHILD_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    int result = data.getIntExtra(UPDATE_SCHEDULE_VIEW, 0);

                    if (result == 1)
                    {
                        displayScheduleToday();
                    }
                }
                break;
            }
        }
    }

    public void displayScheduleToday()
    {
        tls[1].removeAllViews();

        JSONArray schedule = GlobalVariable.scheduleManager.getDailySchedule();

        int time = 8;
        for(int subjectIndex = 0; subjectIndex < schedule.length(); subjectIndex++)
        {
            JSONObject subject = null;
            try
            {
                subject = schedule.getJSONObject(subjectIndex);
                int startSubjectTime = getTime(subject.getString("start_time"));
                int endSubjectTime = getTime(subject.getString("end_time"));

                createSubjectView(false, time, startSubjectTime, -1, null);
                createSubjectView(true, startSubjectTime, endSubjectTime, subjectIndex, subject);
                time = endSubjectTime;

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        createSubjectView(false, time, 18, -1, null);
    }

    private void getTimeTableOneDay(String date)
    {
        Preferences.showLoading(context, "Setup", "Loading data from server...");
        timer.start();

        SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getOneDay(date);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    Preferences.dismissLoading();
                    timer.cancel();
                    isServerRespond = true;

                    JSONArray data = new JSONArray(response.body().string());
                    GlobalVariable.scheduleManager.setDailySchedule(data);

                    displayScheduleToday();

                }
                catch(Exception e){
                    tls[1].removeAllViews();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                tls[1].removeAllViews();
                System.out.print("Tung");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_take_attendance_today, container, false);

        context.setTitle("Take Attendance Today");

        getTableLayout();
        displayTimeColumn();

        rainbow = context.getResources().getIntArray(R.array.rainbow);

        updateTimeView(TODAY);

        scrollView = (ScrollView) myView.findViewById(R.id.layout);
        scrollView.setOnTouchListener(new OnSwipeTouchListener(context)
        {
            public void onSwipeRight() {
                updateTimeView(YESTERDAY);
            };
            public void onSwipeLeft() {
                updateTimeView(TOMORROW);
            };
        });

        return myView;
    }

    private void updateTimeView(int mode)
    {
        switch (mode)
        {
            case TODAY: // today
                break;

            case YESTERDAY: // yesterday
                calendar.add(Calendar.DATE, -1);

                break;
            case TOMORROW: // tomorrow
                calendar.add(Calendar.DATE, +1);
                break;

            default:
                return;
        }

        try
        {
            //+ Time View
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MM/dd/yyyy");
            String stringTime = simpleDateFormat.format(calendar.getTime());
            textTime = (TextView) myView.findViewById(R.id.text_Time);
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            textTime.setText(stringTime);
            textTime.setTextColor(rainbow[today-1]);
            //- Time View

            //+ Load timetable
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String time = dateFormat.format(calendar.getTime());
            loadTimetable(time);
            //- Load timetable

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadTimetable(final String time)
    {
        isServerRespond = false;
        timer = new CountDownTimer(8000, 20) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try
                {
                    if (isServerRespond == false)
                    {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        String message = "Server did not respond. Please check your internet connection." +
                                "Do you want to retry?";
                        builder2.setMessage(message);
                        builder2.setCancelable(true);

                        builder2.setPositiveButton(
                                "RETRY",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        getTimeTableOneDay(time);
                                        dialog.cancel();
                                    }
                                });

                        builder2.setNegativeButton(
                                "CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Preferences.dismissLoading();
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert12 = builder2.create();
                        alert12.show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        getTimeTableOneDay(time);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
