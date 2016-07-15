package com.android.msahakyan.expandablenavigationdrawer.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.app.Fragment;

import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ErrorClass;
import com.android.msahakyan.expandablenavigationdrawer.Preferences;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.Notification;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ServiceGenerator;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.StringClient;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.android.msahakyan.expandablenavigationdrawer.R;

import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChangePasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View myView;

    private static final String TAG = "ChangePasswordActivity";

    EditText _currentpasswordText;
    EditText _passwordText;
    EditText _confirmedPasswordText;
    Button   _changePassButton;

    String currentPassword;
    String password;
    String confirmedPassword;

    Activity context;

    CountDownTimer timer;

    boolean isServerRespond = false;

    private OnFragmentInteractionListener mListener;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = this.getActivity();
    }

    public void changePassword() {
        Log.d(TAG, "Change Password");

        if (!validate()) {
            onChangePasswordFailed();
            return;
        }

        Preferences.showLoading(context, "Change Password", "Processing...");

        currentPassword = _currentpasswordText.getText().toString();
        password = _passwordText.getText().toString();
        confirmedPassword = _confirmedPasswordText.getText().toString();

        isServerRespond = false;
        timer = new CountDownTimer(8000, 20) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try
                {
                    if (isServerRespond == false)
                    {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        String message = "Server did not respond. Please check your internet connection." +
                                "Do you want to retry?";
                        builder2.setMessage(message);
                        builder2.setCancelable(true);

                        builder2.setPositiveButton(
                                "RETRY",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        changePasswordAction();
                                        dialog.cancel();
                                    }
                                });

                        builder2.setNegativeButton(
                                "CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Preferences.dismissLoading();
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert12 = builder2.create();
                        alert12.show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        changePasswordAction();
    }

    void showMessage(String message) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
        builder2.setMessage(message);
        builder2.setCancelable(true);

        builder2.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert12 = builder2.create();
        alert12.show();
    }

    public void onChangePasswordSuccess() {
        Preferences.dismissLoading();

        _currentpasswordText.setText("");
        _passwordText.setText("");
        _confirmedPasswordText.setText("");

    }

    public void onChangePasswordFailed() {
        Preferences.dismissLoading();
    }

    public boolean validate() {

        String currentPassword = _currentpasswordText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmedPassword = _confirmedPasswordText.getText().toString();

        if (currentPassword.isEmpty() || currentPassword.length() < 4 || currentPassword.length() > 10) {
            showMessage("Current password need to be between 4 and 10 alphanumeric characters");
            return false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            showMessage("New password need to be between 4 and 10 alphanumeric characters");
            return false;
        }

        if (password.compareTo(currentPassword) == 0) {
            showMessage("New password must be different with current password");
            return false;
        }

        if (confirmedPassword.compareTo(password) != 0) {
            showMessage("These passwords don't match. Try again?");
            return false;
        }

        return true;
    }

    void changePasswordAction() {

        timer.start();
        SharedPreferences pref = context.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        JsonObject toUp = new JsonObject();
        toUp.addProperty("oldPassword", currentPassword);
        toUp.addProperty("newPassword", password);

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.changePassword(toUp);

        call.enqueue (new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    timer.cancel();
                    isServerRespond = true;
                    int messageCode = response.code();
                    if (messageCode == 200) {
                        Notification.showMessage(context, 7);
                        onChangePasswordSuccess();
                    } else if (messageCode == 400) {
                        JSONObject data = new JSONObject(response.errorBody().string());
                        int errorCode = data.getInt("code");
                        if(errorCode == 1)
                            Notification.showMessage(context, 8);
                        else if(errorCode == 8)
                            Notification.showMessage(context, 9);
                        onChangePasswordFailed();
                    } else {
                        ErrorClass.showError(context, 36);
                        onChangePasswordFailed();
                    }
                }
                catch (Exception e) {
                    timer.cancel();
                    isServerRespond = true;
                    e.printStackTrace();
                    onChangePasswordFailed();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorClass.showError(context, 37);
                timer.cancel();
                onChangePasswordFailed();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_change_password, container, false);

        context.setTitle("Change Password");

        _currentpasswordText   = (EditText) myView.findViewById(R.id.input_old_password);
        _passwordText          = (EditText) myView.findViewById(R.id.input_password);
        _confirmedPasswordText = (EditText) myView.findViewById(R.id.input_confirmpass);
        _changePassButton      = (Button)   myView.findViewById(R.id.btn_changePass);

        _changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        return myView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
