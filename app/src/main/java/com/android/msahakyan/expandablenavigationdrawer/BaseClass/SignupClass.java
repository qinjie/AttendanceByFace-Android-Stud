package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

import android.content.Context;

/**
 * Created by Tung on 5/30/2016.
 */
public class SignupClass {
    String username = "NULL";
    String password = "NULL";
    String email = "NULL";
    String student_id = "NULL";
    String role = "NULL";
    String device_hash = "NULL";

    public SignupClass() {}

    public SignupClass(String _username, String _password, String _email, String _student_id, String _role, Context context){
        username = _username;
        password = _password;
        email = _email;
        student_id = _student_id;
        role = _role;
        device_hash = GlobalVariable.getMac(context);
    }



}
