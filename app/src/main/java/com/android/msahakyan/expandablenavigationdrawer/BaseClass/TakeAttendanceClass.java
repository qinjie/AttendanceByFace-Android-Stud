package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

/**
 * Created by Lord One on 6/13/2016.
 */
public class TakeAttendanceClass {
    int timetable_id;
    double face_percent;
    String face_id;
    public TakeAttendanceClass(int _timetable_id, double _face_percent, String _face_id){
        timetable_id = _timetable_id;
        face_percent = _face_percent;
        face_id = _face_id;
    }
}
