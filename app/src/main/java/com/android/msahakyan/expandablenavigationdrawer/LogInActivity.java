package com.android.msahakyan.expandablenavigationdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ErrorClass;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.GlobalVariable;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.LoginClass;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.Notification;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ServiceGenerator;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.StringClient;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_REGISTER_DEVICE = 1;
    private static final int REQUEST_FORGOT_PASSWORD = 2;

    @InjectView(R.id.input_username)
    EditText _usernameText;
    @InjectView(R.id.input_password)     EditText _passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_forgotPass)
    TextView _forgotPassLink;
    @InjectView(R.id.link_signup)        TextView _signupLink;
    @InjectView(R.id.link_updateMacAddress)    TextView _registerDeviceLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        this.setTitle("Log In");

        if (GlobalVariable.obtainedAuCode(this)) {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("ATK_pref", 0);
            String data = pref.getString("fullTimetable", null);
            try {
                JSONArray temp = new JSONArray(data);
                GlobalVariable.scheduleManager.setSchedule(temp);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorClass.showError(this, 17);
            }

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        GlobalVariable.checkConnected(LogInActivity.this);

        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _forgotPassLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivityForResult(intent, REQUEST_FORGOT_PASSWORD);
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        _registerDeviceLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the RegisterNewDeviceActivity activity
                Intent intent = new Intent(getApplicationContext(), RegisterNewDeviceActivity.class);
                startActivityForResult(intent, REQUEST_REGISTER_DEVICE);
            }
        });

        if (!isNetworkAvailable())
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Take Attendance");
            builder.setMessage("This application requests Internet connection. Do you want to turn it on?");
            builder.setNegativeButton("Disable", null);
            builder.setPositiveButton("Enable",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( final DialogInterface dialogInterface, final int i) {
                            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            wifiManager.setWifiEnabled(true);
                        }
                    });

            builder.create().show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        Preferences.showLoading(this, "Log In", "Authenticating...");

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        //=======================================

        loginAction(username, password, this);

        //---------------------------------------

        Preferences.dismissLoading();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the NavigationActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
//        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 4 || username.length() > 255) {
            _usernameText.setError("enter a valid username address");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 255) {
            _passwordText.setError("at least 6 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void loginAction(String username, String password, final Activity activity) {
        StringClient client = ServiceGenerator.createService(StringClient.class);

        LoginClass up = new LoginClass(username, password, this);

        Call<ResponseBody> call = client.login(up);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int messageCode = response.code();

                    if (messageCode == 200) {
                        JSONObject data = new JSONObject(response.body().string());
                        String authorizationCode = data.getString("token");
                        GlobalVariable.setAuCodeInSP(LogInActivity.this, authorizationCode);
                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else if(messageCode == 400) {
                        JSONObject data = new JSONObject(response.errorBody().string());
                        int errorCode = data.getInt("code");
                        Notification.showLoginNoti(activity, errorCode);
                        onLoginFailed();
                    }
                    else{
                        ErrorClass.showError(LogInActivity.this, 1);
                        onLoginFailed();
                    }
                } catch (Exception e) {
                    System.out.print("Exception caught Login");
                    ErrorClass.showError(LogInActivity.this, 2);
                    onLoginFailed();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.print("Error Login");
                ErrorClass.showError(LogInActivity.this, 3);
                onLoginFailed();
            }
        });

    }

}
