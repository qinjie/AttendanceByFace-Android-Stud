package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

/**
 * Created by Sonata on 6/3/2016.
 */

import org.json.JSONArray;
import org.json.JSONObject;

public class ScheduleManager {
    public final static String[] dailyTime = { "8:00AM", "9:00AM", "10:00AM", "11:00AM", "12:00PM",
            "1:00PM", "2:00PM", "3:00PM", "4:00PM", "5.00PM"};

    public final static int timeNumber = 10;

    public final static int weekId = 0;

    JSONArray semesterSchedule;
    JSONArray dailySchedule = null;
    boolean isTakeAttendace[] = new boolean[timeNumber];
    public int currentLessionIndex;

    public void setSchedule(JSONArray schedule)
    {
        semesterSchedule = schedule;
    }
    public void setDailySchedule(JSONArray schedule)
    {
        if (schedule != null)
        {
            dailySchedule = schedule;
            initAttendanceStatus();
        }
    }
    private void initAttendanceStatus()
    {
        for(int subjectIndex = 0; subjectIndex < dailySchedule.length(); subjectIndex++) {
            JSONObject subject = null;
            try {
                subject = dailySchedule.getJSONObject(subjectIndex);
                final int status = Integer.parseInt(subject.getString("status"));
                if (status == 0)
                {
                    setIsTakeAttendance(subjectIndex, false);
                }
                else
                {
                    setIsTakeAttendance(subjectIndex, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void setIsTakeAttendance(int i, boolean value)
    {
        isTakeAttendace[i] = value;
    }
    public boolean isTakeAttendance(int i)
    {
        return isTakeAttendace[i];
    }
    public void setCurrentLession(int index)
    {
        currentLessionIndex = index;
    }
    public boolean isInitDailySchedule()
    {
        return (dailySchedule != null);
    }
    public JSONObject getWeeklySchedule(int weekId)
    {
        JSONObject data = null;
        try
        {
            data = semesterSchedule.getJSONObject(weekId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //TODO: show error
        }

        return data;
    }

    public JSONArray getDailySchedule()
    {
        return dailySchedule;
    }
    public void updateSchedule(JSONObject serverResult)
    {
        try
        {
            JSONObject subject = dailySchedule.getJSONObject(currentLessionIndex);
            subject.put("recorded_at", serverResult.getString("recorded_at"));
            if (serverResult.getString("is_late").compareTo("true") == 0)
            {
                subject.put("status", "2");
            }
            else
            {
                subject.put("status", "1");
            }

            dailySchedule.put(currentLessionIndex, subject);
            setIsTakeAttendance(currentLessionIndex, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
