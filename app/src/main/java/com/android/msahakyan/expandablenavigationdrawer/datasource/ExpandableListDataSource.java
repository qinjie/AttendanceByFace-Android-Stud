package com.android.msahakyan.expandablenavigationdrawer.datasource;

import android.content.Context;

import com.android.msahakyan.expandablenavigationdrawer.R;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        Map<String, List<String>> expandableListData = new TreeMap<>();

        List<String> takeAttendanceFunctions = Arrays.asList(context.getResources().getStringArray(R.array.film_genre));

        List<String> mainFunction = Arrays.asList(context.getResources().getStringArray(R.array.actionFilms));
        List<String> accountSetting = Arrays.asList(context.getResources().getStringArray(R.array.musicals));
//        List<String> dramaFilms = Arrays.asList(context.getResources().getStringArray(R.array.dramas));
//        List<String> thrillerFilms = Arrays.asList(context.getResources().getStringArray(R.array.thrillers));
//        List<String> comedyFilms = Arrays.asList(context.getResources().getStringArray(R.array.comedies));

        expandableListData.put(takeAttendanceFunctions.get(1), accountSetting);
        expandableListData.put(takeAttendanceFunctions.get(0), mainFunction);
//        expandableListData.put(takeAttendanceFunctions.get(2), dramaFilms);
//        expandableListData.put(takeAttendanceFunctions.get(3), thrillerFilms);
//        expandableListData.put(takeAttendanceFunctions.get(4), comedyFilms);

        return expandableListData;
    }
}
