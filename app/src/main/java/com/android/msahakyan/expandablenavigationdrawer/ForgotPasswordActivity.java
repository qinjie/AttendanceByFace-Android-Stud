package com.android.msahakyan.expandablenavigationdrawer;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.Notification;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ServiceGenerator;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.StringClient;
import com.google.gson.JsonObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    @InjectView(R.id.input_email)       EditText _emailText;
    @InjectView(R.id.btn_forgotPass)    Button   _resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.inject(this);

        this.setTitle("Reset Password");

        _resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewPass();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void requestNewPass() {

        if (!validate()) {
            onResetPassFailed();
            return;
        }

        Preferences.showLoading(ForgotPasswordActivity.this, "Reset Password", "Processing...");

        _resetPasswordButton.setEnabled(false);

        String email = _emailText.getText().toString();

        // Interact with local server
        //==========================

        resetPassAction(email);

        //--------------------------

    }

    public void onResetPassSuccess() {
        Preferences.dismissLoading();
        setResult(RESULT_OK, null);
        _resetPasswordButton.setEnabled(true);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RESET PASSWORD");
        builder.setMessage("Please check your email to get reset password link!");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( final DialogInterface dialogInterface, final int i) {
                        Intent intent = new Intent(ForgotPasswordActivity.this, LogInActivity.class);
                        startActivity(intent);
                    }
                });

        builder.create().show();
    }

    public void onResetPassFailed() {
        _resetPasswordButton.setEnabled(true);
        Preferences.dismissLoading();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        return valid;
    }

    public void resetPassAction(String email) {

        StringClient client = ServiceGenerator.createService(StringClient.class);

        JsonObject userEmail = new JsonObject();
        userEmail.addProperty("email", email);

        Call<ResponseBody> call = client.resetPassword(userEmail);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int messageCode = response.code();

                    if (messageCode == 200) // SUCCESS
                    {
                        onResetPassSuccess();
                    }
                    else
                    {
                        onResetPassFailed();
                        if (messageCode == 400) // BAD REQUEST HTTP
                        {
                            Notification.showMessage(ForgotPasswordActivity.this, 13);
                        }
                        else if (messageCode == 401) // UNAUTHORIZED
                        {
                            //TODO
                        }
                        else if (messageCode == 500) // SERVER FAILED
                        {
                            Notification.showMessage(ForgotPasswordActivity.this, 12);
                        }
                        else {

                        }
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    onResetPassFailed();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onResetPassFailed();
            }
        });
    }
}
