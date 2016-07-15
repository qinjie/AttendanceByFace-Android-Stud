package com.android.msahakyan.expandablenavigationdrawer.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.msahakyan.expandablenavigationdrawer.R;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ErrorClass;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.GlobalVariable;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.Notification;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.ServiceGenerator;
import com.android.msahakyan.expandablenavigationdrawer.BaseClass.StringClient;
import com.android.msahakyan.expandablenavigationdrawer.Preferences;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FaceTrainingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FaceTrainingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaceTrainingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REQUEST_TAKE_PHOTO = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int count = 0;

    Activity context;

    private View myView;

    private TableLayout tl = null;

    private Button mTrainingBtn = null;

    private static final int numOfInstruction = 4;


    private OnFragmentInteractionListener mListener;

    public FaceTrainingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FaceTrainingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FaceTrainingFragment newInstance(String param1, String param2) {
        FaceTrainingFragment fragment = new FaceTrainingFragment();
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

    private void createInstructionView()
    {
        tl = (TableLayout) myView.findViewById(R.id.tableLayout);
        for(int i = 0; i < numOfInstruction; i++)
        {
            TextView tv = new TextView(context);

            if (i < 4)
            {
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                params.height = 140;
                tv.setLayoutParams(params);
            }
            else
            {
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                params.setMargins(2, 2, 2, 2);
                tv.setLayoutParams(params);
            }

            GradientDrawable gd = new GradientDrawable();

            switch (i) {
                case 0:
                    gd.setColor(0xFFC0C0C0);
                    tv.setText(R.string.instruction_1);
                    break;
                case 1:
                    gd.setColor(0xFF00FF7F);
                    tv.setText(R.string.instruction_2);
                    break;
                case 2:
                    gd.setColor(0xFFFFA500);
                    tv.setText(R.string.instruction_3);
                    break;
                case 3:
                    gd.setColor(0xFFCC0000);
                    tv.setText(R.string.instruction_4);
                    break;
            }

            tv.setTextSize(16);
            if (i < 4)
            {
                gd.setCornerRadius(5);
                gd.setStroke(1, 0xFF000000);
                tv.setBackgroundDrawable(gd);
                tv.setTextColor(Color.WHITE);
                tv.setGravity(Gravity.CENTER);
            }
            else
            {
                tv.setBackgroundColor(Color.WHITE);
                tv.setTextColor(Color.RED);
            }

            TableRow trs = new TableRow(context);
            TableLayout.LayoutParams layoutRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trs.setLayoutParams(layoutRow);

            trs.addView(tv);

            tl.addView(trs);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_face_training, container, false);

        context.setTitle("Face Training");

        createInstructionView();

        mTrainingBtn = (Button) myView.findViewById(R.id.btn_training);
        mTrainingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
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

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ErrorClass.showError(context, 4);
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == context.RESULT_OK) {
            // =============================

            trainingFunction();

            // -----------------------------
        }

    }

    void trainingFunction() {
        Activity activity = this.getActivity();
        GlobalVariable.resizeImage(activity, mCurrentPhotoPath);

        TrainThread trainThread = new TrainThread(mCurrentPhotoPath, context);
        trainThread.start();
    }
}

class TrainThread extends Thread{
    Thread t;
    String mCurrentPhotoPath = null;
    Activity activity;
    CountDownTimer timer;

    public TrainThread(String _mCurrentPhotoPath, Activity _activity){
        mCurrentPhotoPath = _mCurrentPhotoPath;
        activity = _activity ;
    }

    public void run(){

        Preferences.showLoading(activity, "Training on progress ...", "");

        HttpRequests httpRequests = new HttpRequests(GlobalVariable.apiKey, GlobalVariable.apiSecret);
        File imgFile = new File(mCurrentPhotoPath);

        SharedPreferences pref = activity.getSharedPreferences("ATK_pref", 0);
        String auCode = pref.getString("authorizationCode", null);

        String newFaceID = GlobalVariable.get1FaceID(activity, httpRequests, imgFile);
            if(newFaceID != null) {
                timer.cancel();
            String personID = GlobalVariable.getThisPersonID(activity, auCode);

            if (personID.compareTo("") != 0) { //this person has been trained before

                ArrayList faceIDList = getThisFaceIDList(auCode);
                faceIDList = substitute1FacefromPerson(httpRequests, personID, faceIDList, newFaceID);
                postFaceIDListtoLocalServer(auCode, faceIDList);
            } else {
                personID = create1Person(httpRequests, newFaceID);
                postPersonIDtoLocalServer(auCode, personID);
                ArrayList<String> faceIDList = new ArrayList<String>();
                faceIDList.add(newFaceID);
                postFaceIDListtoLocalServer(auCode, faceIDList);
            }
            //Show notification about sucessful training
            Notification.showMessage(activity, 0);
        }

        Preferences.dismissLoading();
    }

    void postPersonIDtoLocalServer(String auCode, String personID){
        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.postPersonID(personID);

        try{
            Response<ResponseBody> response = call.execute();
            int messageCode = response.code();
        }
        catch(Exception e){
            e.printStackTrace();
            ErrorClass.showError(activity, 18);
        }

    }

    String create1Person(HttpRequests httpRequests, String faceID){
        String personID = null;

        try {
            PostParameters postParameters = new PostParameters().setFaceId(faceID);
            JSONObject person = httpRequests.personCreate(postParameters);
            personID = person.getString("person_id");

            postParameters = new PostParameters().setPersonId(personID);
            httpRequests.trainVerify(postParameters);
        }
        catch (Exception e){
            e.printStackTrace();
            ErrorClass.showError(activity, 16);
        }

        return personID;
    }

    void postFaceIDListtoLocalServer(String auCode, ArrayList<String> faceIDList) {
        try {
            StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
            Call<ResponseBody> call = client.postFaceIDList(faceIDList);
            Response<ResponseBody> response = call.execute();
            int messageCode = response.code();
            if(messageCode != 200) {
                ErrorClass.showError(activity, 15);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            ErrorClass.showError(activity, 14);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    ArrayList substitute1FacefromPerson(HttpRequests httpRequests, String personID, ArrayList faceIDList, String newFaceID){

        try {

            // get the earliest faceID in the list, that is the faceID with index 0
            if (faceIDList != null && faceIDList.size() == GlobalVariable.maxLengthFaceList) {
                String oldFaceID = faceIDList.get(0).toString();
                // remove it on Face++
                PostParameters postParameters = new PostParameters().setPersonId(personID).setFaceId(oldFaceID);
                httpRequests.personRemoveFace(postParameters);
                // remove it on the list
                faceIDList.remove(0);
            }

            // add 1 face on Face++
            PostParameters postParameters = new PostParameters().setPersonId(personID).setFaceId(newFaceID);
            httpRequests.personAddFace(postParameters);
            // add 1 face on the list
            if (faceIDList != null)
                faceIDList.add(newFaceID);
            else {
                faceIDList = new ArrayList();
                faceIDList.add(newFaceID);
            }

            //re-train person on Face++
            postParameters = new PostParameters().setPersonId(personID);
            httpRequests.trainVerify(postParameters);

            return faceIDList;

        }
        catch(Exception e){
            e.printStackTrace();
            ErrorClass.showError(activity, 13);
        }

        return null;
    }

    ArrayList getThisFaceIDList(String auCode) {

        ArrayList<String> result = null;

        StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
        Call<ResponseBody> call = client.getFaceIDList();

        try {
            Response<ResponseBody> response = call.execute();
            JSONObject data = new JSONObject(response.body().string());
            JSONArray arr = data.getJSONArray("face_id");

            result = new ArrayList<String>();
            for (int i = 0; i < arr.length(); i++)
                result.add(arr.getString(i).toString());

        }
        catch(Exception e) {
            e.printStackTrace();
            ErrorClass.showError(activity, 12);
        }

        return result;

    }
}
