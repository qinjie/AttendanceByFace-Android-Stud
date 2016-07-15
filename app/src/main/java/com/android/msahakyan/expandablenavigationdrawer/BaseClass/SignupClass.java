package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

import android.content.Context;

/**
 * Created by Tung on 5/30/2016.
 */
public class SignupClass {
    String username = "NULL";
    String password = "NULL";
    String email = "NULL";
    String SID = "NULL";
    String device_hash = "NULL";

    public SignupClass() {}

    public SignupClass(String _username, String _password, String _email, String _SID, Context context){
        username = _username;
        password = _password;
        email = _email;
        SID = _SID;
        device_hash = GlobalVariable.getMac(context);
    }



}
