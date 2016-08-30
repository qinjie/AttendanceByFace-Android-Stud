package com.android.msahakyan.expandablenavigationdrawer.datasource;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.android.msahakyan.expandablenavigationdrawer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by msahakyan on 22/10/15.
 */
public class ExpandableListDataSource {

    /**
     * Returns fake data of films
     *
     * @param context
     * @return
     */
    public static Map<String, List<String>> getData(Context context) {
        Map<String, List<String>> expandableListData = new LinkedHashMap<>();

        List<String> takeAttendanceFunctions = Arrays.asList(context.getResources().getStringArray(R.array.functions));

        List<String> attendanceTaking = Arrays.asList(context.getResources().getStringArray(R.array.attendanceTaking));
        expandableListData.put(takeAttendanceFunctions.get(0), attendanceTaking);

        List<String> schedule = Arrays.asList(context.getResources().getStringArray(R.array.schedule));
        expandableListData.put(takeAttendanceFunctions.get(1), schedule);

        List<String> setting = Arrays.asList(context.getResources().getStringArray(R.array.setting));
        expandableListData.put(takeAttendanceFunctions.get(2), setting);

        List<String> help = Arrays.asList(context.getResources().getStringArray(R.array.help));
        expandableListData.put(takeAttendanceFunctions.get(3), help);

        List<String> logOut = Arrays.asList(context.getResources().getStringArray(R.array.logOut));
        expandableListData.put(takeAttendanceFunctions.get(4), logOut);

        return expandableListData;
    }
}
