package com.android.msahakyan.expandablenavigationdrawer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ErrorClass;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.Notification;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ServiceGenerator;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.SignupClass;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.StringClient;

import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    @InjectView(R.id.input_email)       EditText _emailText;
    @InjectView(R.id.btn_forgotPass)    Button   _signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        this.setTitle("Reset Password");

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewPass();
            }
        });
    }

    public void requestNewPass() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        Preferences.showLoading(ForgotPasswordActivity.this, "Reset Password", "Processing...");

        _signupButton.setEnabled(false);

        String email = _emailText.getText().toString();

        // Interact with local server
        //==========================

        //TODO
//        SignupClass user = new SignupClass(username, password, email, studentId, this);
//        signupAction(user);

        //--------------------------

    }

    public void onResetPassSuccess() {
        //TODO
//        Preferences.dismissLoading();
//        setResult(RESULT_OK, null);
//
//        Toast.makeText(getBaseContext(), "Signed up successfully!", Toast.LENGTH_LONG).show();
//
//        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
//        startActivity(intent);

    }

    public void onSignupFailed() {
        //TODO
//        Preferences.dismissLoading();
//        Toast.makeText(getBaseContext(), "Signup failed!", Toast.LENGTH_LONG).show();
//
//        _signupButton.setEnabled(true);

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

    public void resetPassAction(SignupClass user) {

        String returnMessage = "";

        //TODO

//        StringClient client = ServiceGenerator.createService(StringClient.class);
//
//        Call<ResponseBody> call = client.signup(user);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//
//                    int messageCode = response.code();
//                    if(messageCode == 200){
//                        onSignupSuccess();
//                    }
//                    else{
//                        // handle when cannot signup
//                        onSignupFailed();
//                        Notification.showMessage(SignUpActivity.this, 5);
//                        Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
//                        startActivity(intent);
//                    }
//
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                    ErrorClass.showError(SignUpActivity.this, 27);
//                    Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                ErrorClass.showError(SignUpActivity.this, 28);
//            }
//        });
    }
}
