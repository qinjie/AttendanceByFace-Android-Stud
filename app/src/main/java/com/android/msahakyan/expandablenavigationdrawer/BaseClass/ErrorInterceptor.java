package com.android.msahakyan.expandablenavigationdrawer.BaseClass;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class ErrorInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        // before request
        Request request = chain.request();

        // execute request
        Response response = chain.proceed(request);


        // after request

        // inspect status codes of unsuccessful responses
        switch (response.code()){
            case 401:
                Log.e("TEST","Unauthorized error for: " +request.url());
                GlobalVariable.logoutAction(GlobalVariable.activity);
        }

        return response;
    }
}