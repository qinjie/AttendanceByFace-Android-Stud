package com.android.msahakyan.expandablenavigationdrawer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ErrorClass;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.GlobalVariable;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.Notification;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ServiceGenerator;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.StringClient;
import com.google.gson.JsonObject;
import com.google.gson.internal.Excluder;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterNewDeviceActivity extends AppCompatActivity {

    private static final String TAG = "RegisterNewDeviceActivity";

    @InjectView(R.id.input_username)    EditText _usernameText;
    @InjectView(R.id.input_password)    EditText _passwordText;
    @InjectView(R.id.btn_register)      Button   _registerButton;
    @InjectView(R.id.link_login)        TextView _loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_device);

        this.setTitle("Register New Device");

        ButterKnife.inject(this);

        _registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void register() {
        //TODO register new device
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        JsonObject toUp = new JsonObject();
        toUp.addProperty("username", username);
        toUp.addProperty("password", password);
        toUp.addProperty("device_hash", GlobalVariable.getMac(this));

        StringClient client = ServiceGenerator.createService(StringClient.class);
        Call<ResponseBody> call = client.registerDevice(toUp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int messageCode = response.code();
                if(messageCode == 200){
                    onRegisterSuccess();
                }
                else if(messageCode == 400){
                    try {
                        JSONObject data = new JSONObject(response.errorBody().string());
                        int errorCode = data.getInt("code");
                        onRegisterFailed(errorCode);
                    }
                    catch (Exception e){}
                }
                else{
                    ErrorClass.showError(RegisterNewDeviceActivity.this, 3);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorClass.showError(RegisterNewDeviceActivity.this, 3);
            }
        });

    }

    public void onRegisterSuccess() {
//        progressDialog.dismiss();
        setResult(RESULT_OK, null);

        Toast.makeText(getBaseContext(), "Device registered successfully!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(RegisterNewDeviceActivity.this, LogInActivity.class);
        startActivity(intent);

    }

    public void onRegisterFailed(int errorCode) {
//        progressDialog.dismiss();sterFailed
//        Toast.makeText(getBaseContext(), "Signup failed!", Toast.LENGTH_LONG).show();
        Notification.showLoginNoti(RegisterNewDeviceActivity.this, errorCode);
//        _signupButton.setEnabled(true);

    }

}

