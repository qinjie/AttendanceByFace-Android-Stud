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

import com.android.msahakyan.expandablenavigationdrawer.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.app.Fragment;
import android.view.Gravity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.GlobalVariable;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ServiceGenerator;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.StringClient;
import com.android.msahakyan.expandablenavigationdrawer.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimeTableFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimeTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View myView;

    private Activity context;

    private Spinner spin;
    private Spinner subjectSpin;

    private String classSubject[];

    private TableLayout[] tls = new TableLayout[3];

    private CountDownTimer timer;

    private boolean isServerRespond = false;

    public TimeTableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeTableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeTableFragment newInstance(String param1, String param2) {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = this.getActivity();
    }

    private void getTableLayout()
    {
        tls[0] = (TableLayout) myView.findViewById(R.id.tableLayout1);
        tls[1] = (TableLayout) myView.findViewById(R.id.tableLayout2);
        tls[2] = (TableLayout) myView.findViewById(R.id.tableLayout3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_time_table, container, false);

        context.setTitle("Timetable");

        spin = (Spinner) myView.findViewById(R.id.time_table_spinner);
        subjectSpin = (Spinner) myView.findViewById(R.id.header2);

        getTableLayout();

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
                                        getListsemesterAndLastestSemesterClassesFunction();
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

        getListsemesterAndLastestSemesterClassesFunction();
//        initTimeSpinner();

        return myView;
    }

    private void initTimeSpinner()
    {
        ArrayList<String> spinnerArray = new ArrayList<String>();

        spinnerArray.add("In next 7 days");
        spinnerArray.add("In next 14 days");
        spinnerArray.add("In next 30 days");
        spinnerArray.add("In next 60 days");

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(context,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray);

        spin.setAdapter(spinnerArrayAdapter);

        spin.setSelection(GlobalVariable.currentTimeSelection);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (GlobalVariable.currentTimeSelection != position)
                {
                    GlobalVariable.currentTimeSelection = position;
                    getTimeTableNextKDays(getK(position));
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    private void setSpinnerForSubjectView()
    {
        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("All subjects");
        for(int i = 0; i < classSubject.length; i++)
        {
            spinnerArray.add(classSubject[i]);
        }

        try
        {
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,
                    R.layout.spinner_item,
                    spinnerArray);

            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

            subjectSpin.setAdapter(spinnerArrayAdapter);
            subjectSpin.setSelection(GlobalVariable.currentSubjectSelection);

            subjectSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    if (GlobalVariable.currentSubjectSelection != position)
                    {
                        GlobalVariable.currentSubjectSelection = position;
                        loadRecord();
                    }
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getDate(String date)
    {
        if (date.compareTo("SUN") == 0) return "Sunday";
        if (date.compareTo("MON") == 0) return "Monday";
        if (date.compareTo("TUES") == 0) return "Tuesday";
        if (date.compareTo("WED") == 0) return "Wednesday";
        if (date.compareTo("THUR") == 0) return "Thursday";
        if (date.compareTo("FRI") == 0) return "Friday";
        if (date.compareTo("SAT") == 0) return "Saturday";

        return "";
    }

    private String getComponent(String component)
    {
        if (component.compareTo("TUT") == 0) return "Tutorial";
        if (component.compareTo("LEC") == 0) return "Lecture";
        if (component.compareTo("PRA") == 0) return "Practical";

        return "";
    }

    private void removeAllView()
    {
        tls[0].removeAllViews();
        tls[1].removeAllViews();
        tls[2].removeAllViews();
    }

    private void initDateColumn(JSONArray value, String date, int count)
    {
        try
        {
            JSONObject subject = value.getJSONObject(0);

            //+ Define same height for each row
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            params.height = 150 * value.length();
            //- Define same height for each row

            //+ Create background based on status
            GradientDrawable gd = new GradientDrawable();

            //+ Add item to Date column
            String weekDay = subject.getString("weekday");
            String dateInfo = getDate(weekDay) + ", " + System.getProperty ("line.separator") + date;

            TextView tvDate = new TextView(context);
            tvDate.setText(dateInfo);
            tvDate.setGravity(Gravity.CENTER);

            if (count % 2 == 0)
            {
                gd.setColor(0xFFFFFFFF);
                tvDate.setTextColor(Color.parseColor("#B3BBCC"));
            }
            else
            {
                gd.setColor(0xffbbdefb);
                tvDate.setTextColor(Color.WHITE);
            }

            tvDate.setBackgroundDrawable(gd);
            tvDate.setLayoutParams(params);

            TableRow tableRowDate = new TableRow(context);
            tableRowDate.addView(tvDate);
            tableRowDate.setLayoutParams(params);

            tls[0].addView(tableRowDate);
            //- Add item to Date column

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initDateColumn(String dateInfo, int count, int size)
    {
        //+ Define same height for each row
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        params.height = 150 * size;
        //- Define same height for each row

        //+ Create background based on status
        GradientDrawable gd = new GradientDrawable();

        //+ Add item to Date column
        TextView tvDate = new TextView(context);
        tvDate.setText(dateInfo);
        tvDate.setGravity(Gravity.CENTER);

        if (count % 2 == 0)
        {
            gd.setColor(0xFFFFFFFF);
            tvDate.setTextColor(Color.parseColor("#B3BBCC"));
        }
        else
        {
            gd.setColor(0xffbbdefb);
            tvDate.setTextColor(Color.WHITE);
        }

        tvDate.setBackgroundDrawable(gd);
        tvDate.setLayoutParams(params);

        TableRow tableRowDate = new TableRow(context);
        tableRowDate.addView(tvDate);
        tableRowDate.setLayoutParams(params);

        tls[0].addView(tableRowDate);
        //- Add item to Date column
    }

    private void initSubjectAndTimeColumns(JSONArray value, int count)
    {
        try
        {
            //+ Define same height for each row
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            params.height = 150;
            //- Define same height for each row

            for(int i = 0; i < value.length(); i++)
            {
                JSONObject subject = value.getJSONObject(i);
                //+ Create background based on status
                GradientDrawable gd = new GradientDrawable();

                //+ Add item to Subject Information column
                String subject_area = subject.getString("subject_area");
                String class_section = subject.getString("class_section");
                String temp = subject_area + " " + class_section;

                String component = subject.getString("component");

                String location = subject.getString("location");

                String lecturer_name = subject.getString("lecturer_name");

                String subjectInfo = temp + System.getProperty ("line.separator") +
                        getComponent(component) + System.getProperty ("line.separator") +
                        location + System.getProperty ("line.separator") +
                        lecturer_name;

                TextView tvSubjectInfo = new TextView(context);
                tvSubjectInfo.setText(subjectInfo);
                tvSubjectInfo.setTextColor(Color.WHITE);
                tvSubjectInfo.setGravity(Gravity.CENTER);

                if (count % 2 == 0)
                {
                    gd.setColor(0xFFFFFFFF);
                    tvSubjectInfo.setTextColor(Color.parseColor("#33b5e5"));
                }
                else
                {
                    gd.setColor(0xff33b5e5);
                    tvSubjectInfo.setTextColor(Color.WHITE);
                }

                tvSubjectInfo.setBackgroundDrawable(gd);
                tvSubjectInfo.setLayoutParams(params);

                TableRow tableRowSubject = new TableRow(context);
                tableRowSubject.addView(tvSubjectInfo);
                tableRowSubject.setLayoutParams(params);

                tls[1].addView(tableRowSubject);
                //- Add item to Subject Information column

                //+ Add item to Time column
                String startTime = subject.getString("start_time");
                String endTime = subject.getString("end_time");

                String time = startTime + System.getProperty ("line.separator")
                        + "-" + System.getProperty ("line.separator")
                        + endTime;

                TextView tvTimeInfo = new TextView(context);
                tvTimeInfo.setText(time);
                tvTimeInfo.setTextColor(Color.WHITE);
                tvTimeInfo.setGravity(Gravity.CENTER);

                if (count % 2 == 0)
                {
                    gd.setColor(0xFFFFFFFF);
                    tvTimeInfo.setTextColor(Color.parseColor("#33b5e5"));
                }
                else
                {
                    gd.setColor(0xFF33b5e5);
                    tvTimeInfo.setTextColor(Color.WHITE);
                }

                tvTimeInfo.setBackgroundDrawable(gd);
                tvTimeInfo.setLayoutParams(params);

                TableRow tableRowTime = new TableRow(context);
                tableRowTime.addView(tvTimeInfo);
                tableRowTime.setLayoutParams(params);

                tls[2].addView(tableRowTime);
                //- Add item to Time column

                count++;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initSubjectAndTimeColumns(JSONObject subject, int count)
    {
        try
        {
            //+ Define same height for each row
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            params.height = 150;
            //- Define same height for each row

            //+ Create background based on status
            GradientDrawable gd = new GradientDrawable();

            //+ Add item to Subject Information column
            String subject_area = subject.getString("subject_area");
            String class_section = subject.getString("class_section");
            String temp = subject_area + " " + class_section;

            String component = subject.getString("component");

            String location = subject.getString("location");

            String lecturer_name = subject.getString("lecturer_name");

            String subjectInfo = temp + System.getProperty ("line.separator") +
                    getComponent(component) + System.getProperty ("line.separator") +
                    location + System.getProperty ("line.separator") +
                    lecturer_name;

            TextView tvSubjectInfo = new TextView(context);
            tvSubjectInfo.setText(subjectInfo);
            tvSubjectInfo.setTextColor(Color.WHITE);
            tvSubjectInfo.setGravity(Gravity.CENTER);

            if (count % 2 == 0)
            {
                gd.setColor(0xFFFFFFFF);
                tvSubjectInfo.setTextColor(Color.parseColor("#33b5e5"));
            }
            else
            {
                gd.setColor(0xff33b5e5);
                tvSubjectInfo.setTextColor(Color.WHITE);
            }

            tvSubjectInfo.setBackgroundDrawable(gd);
            tvSubjectInfo.setLayoutParams(params);

            TableRow tableRowSubject = new TableRow(context);
            tableRowSubject.addView(tvSubjectInfo);
            tableRowSubject.setLayoutParams(params);

            tls[1].addView(tableRowSubject);
            //- Add item to Subject Information column

            //+ Add item to Time column
            String startTime = subject.getString("start_time");
            String endTime = subject.getString("end_time");

            String time = startTime + System.getProperty ("line.separator")
                    + "-" + System.getProperty ("line.separator")
                    + endTime;

            TextView tvTimeInfo = new TextView(context);
            tvTimeInfo.setText(time);
            tvTimeInfo.setTextColor(Color.WHITE);
            tvTimeInfo.setGravity(Gravity.CENTER);

            if (count % 2 == 0)
            {
                gd.setColor(0xFFFFFFFF);
                tvTimeInfo.setTextColor(Color.parseColor("#33b5e5"));
            }
            else
            {
                gd.setColor(0xFF33b5e5);
                tvTimeInfo.setTextColor(Color.WHITE);
            }

            tvTimeInfo.setBackgroundDrawable(gd);
            tvTimeInfo.setLayoutParams(params);

            TableRow tableRowTime = new TableRow(context);
            tableRowTime.addView(tvTimeInfo);
            tableRowTime.setLayoutParams(params);

            tls[2].addView(tableRowTime);
            //- Add item to Time column

            count++;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadRecordForAllSubjects()
    {
        try
        {
            int count = -1;
            int countSub = 0;
            Iterator<String> iter =  GlobalVariable.currentTimetable.keys();
            while (iter.hasNext())
            {
                String key = iter.next();
                try
                {
                    count++;
                    JSONArray value = GlobalVariable.currentTimetable.getJSONArray(key);
                    initDateColumn(value, key, count);
                    initSubjectAndTimeColumns(value, countSub);
                    countSub += value.length();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadRecordForSpecificSubject()
    {
        try
        {
            String str_subject = classSubject[GlobalVariable.currentSubjectSelection-1];

            int countTime = -1;
            int count = -1;
            JSONObject temp = GlobalVariable.currentTimetable;

            Iterator<String> iter =  temp.keys();
            while (iter.hasNext())
            {
                int isExist = 0;
                String key = iter.next();
                String dateInfo = "";
                try
                {
                    JSONArray value = temp.getJSONArray(key);
                    for(int i = 0 ; i < value.length(); i++)
                    {
                        JSONObject subject = value.getJSONObject(i);
                        String class_secsion = subject.getString("class_section");
                        if (class_secsion.compareTo(str_subject) == 0)
                        {
                            isExist++;
                            count++;
                            initSubjectAndTimeColumns(subject, count);

                            String weekDay = subject.getString("weekday");
                            dateInfo = getDate(weekDay) + ", " + System.getProperty ("line.separator") + key;
                        }
                    }

                    if (isExist > 0)
                    {
                        countTime++;
                        initDateColumn(dateInfo, countTime, isExist);
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadRecord()
    {
        removeAllView();
        if (GlobalVariable.currentSubjectSelection == 0)
        {
            loadRecordForAllSubjects();
        } else
        {
            loadRecordForSpecificSubject();
        }
    }

    void getListClassesFunction(String semester) {
        SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getListClasses(semester);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Preferences.dismissLoading();
                    int messageCode = response.code();
                    JSONArray listClasses = new JSONArray(response.body().string());
                    classSubject = new String[listClasses.length()];

                    for(int i = 0; i < listClasses.length(); i++)
                    {
                        classSubject[i] = listClasses.getString(i);
                    }

                    GlobalVariable.currentSubjectSelection = 0;
                    setSpinnerForSubjectView();

                    System.out.print("OK");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private int getK(int index)
    {
        switch (index)
        {
            case 0:
                return 7;
            case 1:
                return 14;
            case 2:
                return 30;
            case 3:
                return 60;
            default:
                return 0;
        }
    }

    void getListsemesterAndLastestSemesterClassesFunction() {
        Preferences.showLoading(context, "Setup", "Loading data from server...");
        timer.start();
        SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getListSemesters();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    isServerRespond = true;
                    timer.cancel();
                    int messageCode = response.code();
                    JSONArray listSemesters = new JSONArray(response.body().string());
                    String lastSmt = listSemesters.getString(listSemesters.length() - 1);
                    getListClassesFunction(lastSmt);

                    getTimeTableNextKDays(getK(GlobalVariable.currentTimeSelection));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void getTimeTableNextKDays(int k) {
        Preferences.showLoading(context, "Initialize", "Loading data from server...");
        SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getNextDays(k);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Preferences.dismissLoading();
                    int messageCode = response.code();
                    GlobalVariable.currentTimetable = new JSONObject(response.body().string());
                    GlobalVariable.currentSubjectSelection = 0;
                    subjectSpin.setSelection(GlobalVariable.currentSubjectSelection);
                    loadRecord();
                    System.out.print("OK");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
