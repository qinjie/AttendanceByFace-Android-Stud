package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Tung on 5/31/2016.
 */
public class LoginClass {
    String username = "NULL";
    String password = "NULL";
    String device_hash = "NULL";
    public LoginClass(String _username, String _password, Context context){
        username = _username;
        password = _password;
        device_hash = getMac(context);
    }

    private String getMac(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        return address;
    }
}

