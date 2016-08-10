package com.android.msahakyan.expandablenavigationdrawer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.GlobalVariable;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.Notification;
import com.android.msahakyan.expandablenavigationdrawer.Fragment.ChangePasswordFragment;
import com.android.msahakyan.expandablenavigationdrawer.Fragment.ColorInstructionFragment;
import com.android.msahakyan.expandablenavigationdrawer.Fragment.FaceTrainingFragment;
import com.android.msahakyan.expandablenavigationdrawer.Fragment.TakeAttendanceTodayFragment;
import com.android.msahakyan.expandablenavigationdrawer.Fragment.TimeTableFragment;
import com.android.msahakyan.expandablenavigationdrawer.Fragment.HistoricalReportFragment;
import com.android.msahakyan.expandablenavigationdrawer.adapter.CustomExpandableListAdapter;
import com.android.msahakyan.expandablenavigationdrawer.datasource.ExpandableListDataSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ServiceGenerator;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.StringClient;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private ExpandableListView mExpandableListView;
    private ExpandableListAdapter mExpandableListAdapter;
    private List<String> mExpandableListTitle;
    private Map<String, List<String>> mExpandableListData;

    private boolean bStudentInfo = false;

    private ListView mDrawList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        mExpandableListView = (ExpandableListView) findViewById(R.id.navList);

        // Customize header view
        LayoutInflater inflater = getLayoutInflater();
        final View listHeaderView = inflater.inflate(R.layout.nav_header, null, false);

        //+ Update time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        String stringTime = simpleDateFormat.format(cal.getTime());

        TextView textTime = (TextView) listHeaderView.findViewById(R.id.date);
        textTime.setText(stringTime);
        //- Update time

        getStudentInformation();

        mExpandableListData = ExpandableListDataSource.getData(this);
        mExpandableListTitle = new ArrayList(mExpandableListData.keySet());

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Preferences.showLoading(this, "Setup", "Loading data from server...");
        SharedPreferences pref = this.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> new_call = client.getFaceIDList();

        new_call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject data = new JSONObject(response.body().string());
                    JSONArray arr = data.getJSONArray("face_id");
                    if (arr.length() == 0)
                    {
                        GlobalVariable.isNeededToTraining = true;
                    }
                    else
                    {
                        GlobalVariable.isNeededToTraining = false;
                    }
                }
                catch (Exception e){
                    GlobalVariable.isNeededToTraining = true;
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Preferences.dismissLoading();
            }
        });

        loadDefaultFragment();
    }

    private void loadDefaultFragment()
    {
        //+ Load default fragment
        android.app.Fragment fragment = null;

        if (GlobalVariable.isNeededToTraining == true)
        {
            fragment = new FaceTrainingFragment();
        }
        else
        {
            fragment = new TakeAttendanceTodayFragment();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        //- Load default fragment
    }

    void getStudentInformation() {

        final CountDownTimer timer = new CountDownTimer(8000, 20) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try
                {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getApplicationContext());
                    String message = "Server did not respond. Please check your internet connection." +
                            "Do you want to retry?";
                    builder2.setMessage(message);
                    builder2.setCancelable(true);

                    builder2.setPositiveButton(
                            "RETRY",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getStudentInformation();
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
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        timer.start();

        // Customize header view
        LayoutInflater inflater = getLayoutInflater();
        final View listHeaderView = inflater.inflate(R.layout.nav_header, null, false);

        //+ Update student email
        Preferences.showLoading(this, "Setup", "Loading data from server...");
        SharedPreferences pref = this.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getStudentProfile();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    bStudentInfo = true;
                    timer.cancel();
                    Preferences.dismissLoading();
                    int messageCode = response.code();
                    GlobalVariable.currentUserProfile = new JSONObject(response.body().string());

                    TextView email = (TextView) listHeaderView.findViewById(R.id.email);
                    email.setText(GlobalVariable.currentUserProfile.getString("email"));

                    mExpandableListView.addHeaderView(listHeaderView);
                    // Customize header view
                }
                catch (Exception e){
                    e.printStackTrace();
                    Preferences.dismissLoading();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Preferences.dismissLoading();
            }
        });
        //- Update student email
    }

    private void addDrawerItems() {
        mExpandableListAdapter = new CustomExpandableListAdapter(this, mExpandableListTitle, mExpandableListData);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setGroupIndicator(null);

        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                getSupportActionBar().setTitle(mExpandableListTitle.get(groupPosition).toString());
            }
        });

        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                getSupportActionBar().setTitle(R.string.film_genres);
            }
        });

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                getSupportActionBar().setTitle(mExpandableListTitle.get(groupPosition).toString());

                android.app.Fragment fragment = null;

                if(groupPosition == 0) {
                    if (GlobalVariable.isNeededToTraining == true)
                    {
                        Notification.showMessage(MainActivity.this, 15);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        return false;
                    }
                    fragment = new TakeAttendanceTodayFragment();
                }
                else if (groupPosition == 3)
                {
                    if (GlobalVariable.isNeededToTraining == true)
                    {
                        Notification.showMessage(MainActivity.this, 15);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        return false;
                    }
                    fragment = new ColorInstructionFragment();
                }
                else if (groupPosition == 4)
                {
                    GlobalVariable.logoutAction(MainActivity.this);
                    return true;
                }
                else
                {
                    return false;
                }

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();

                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                android.app.Fragment fragment = null;

                if (groupPosition == 1)
                {
                    switch (childPosition)
                    {
                        case 0: // Timetable
                            if (GlobalVariable.isNeededToTraining == true)
                            {
                                Notification.showMessage(MainActivity.this, 15);
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return false;
                            }
                            fragment = new TimeTableFragment();
                            break;
                        case 1: // Attendance history
                            if (GlobalVariable.isNeededToTraining == true)
                            {
                                Notification.showMessage(MainActivity.this, 15);
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return false;
                            }
                            fragment = new HistoricalReportFragment();
                            break;
                        default:
                            fragment = new TimeTableFragment();
                            break;
                    }
                }
                else if (groupPosition == 2)
                {
                    switch (childPosition)
                    {
                        case 0: // Face training
                            if (GlobalVariable.isAllowedForTraining == false) {
                                Preferences.showLoading(MainActivity.this, "Training", "Checking data from server...");

                                SharedPreferences pref = getSharedPreferences("ATK_pref", 0);
                                String auCode = pref.getString("authorizationCode", null);

                                StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
                                Call<ResponseBody> call = client.checkTrainFace();
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        try {
                                            Preferences.dismissLoading();

                                            JSONObject data = new JSONObject(response.body().string());
                                            if (data.getString("result").compareTo("true") == 0) {
                                                GlobalVariable.isAllowedForTraining = true;
                                            } else {
                                                GlobalVariable.isAllowedForTraining = false;
                                            }
                                            setTrainingFace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });

                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return false;
                            }
                            else
                            {
                                fragment = new FaceTrainingFragment();
                                break;
                            }
                        case 1: // Change password
                            fragment = new ChangePasswordFragment();
                            break;
                        default:
                            return false;
                    }
                }

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();

                mDrawerLayout.closeDrawer(GravityCompat.START);

                String selectedItem = ((List) (mExpandableListData.get(mExpandableListTitle.get(groupPosition))))
                        .get(childPosition).toString();
                getSupportActionBar().setTitle(selectedItem);

                return false;
            }
        });
    }

    private void setTrainingFace()
    {
        android.app.Fragment fragment = null;
        if (GlobalVariable.isAllowedForTraining == true)
        {
            fragment = new FaceTrainingFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
        else
        {
            Notification.showMessage(MainActivity.this, 14);
        }
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            if (!bStudentInfo)
            {
                getStudentInformation();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
