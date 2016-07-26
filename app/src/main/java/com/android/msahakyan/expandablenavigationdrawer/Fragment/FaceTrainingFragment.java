package com.android.msahakyan.expandablenavigationdrawer.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.ViewSwitcher;

import com.android.msahakyan.expandablenavigationdrawer.BaseClass.OnSwipeTouchListener;
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

    private static final int numOfTrainedImage = 5;

    Activity context;

    private View myView;

    private TableLayout tl = null;

    private Button mTrainingBtn = null;

    private CheckBox checkBox;

    private boolean isReset = false;

    private static final int numOfInstruction = 4;

    private ArrayList<String> trainingImages;

    private ImageSwitcher imageSwitcher;

    private OnFragmentInteractionListener mListener;

    private int currentIndex = -1;

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

//    private void createInstructionView()
//    {
//        tl = (TableLayout) myView.findViewById(R.id.tableLayout);
//        for(int i = 0; i < numOfInstruction; i++)
//        {
//            TextView tv = new TextView(context);
//
//            if (i < 4)
//            {
//                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, Gravity.CENTER);
//                params.height = 140;
//                tv.setLayoutParams(params);
//            }
//            else
//            {
//                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//                params.setMargins(2, 2, 2, 2);
//                tv.setLayoutParams(params);
//            }
//
//            GradientDrawable gd = new GradientDrawable();
//
//            switch (i) {
//                case 0:
//                    gd.setColor(0xFFC0C0C0);
//                    tv.setText(R.string.instruction_1);
//                    break;
//                case 1:
//                    gd.setColor(0xFF00FF7F);
//                    tv.setText(R.string.instruction_2);
//                    break;
//                case 2:
//                    gd.setColor(0xFFFFA500);
//                    tv.setText(R.string.instruction_3);
//                    break;
//                case 3:
//                    gd.setColor(0xFFCC0000);
//                    tv.setText(R.string.instruction_4);
//                    break;
//            }
//
//            tv.setTextSize(16);
//            if (i < 4)
//            {
//                gd.setCornerRadius(5);
//                gd.setStroke(1, 0xFF000000);
//                tv.setBackgroundDrawable(gd);
//                tv.setTextColor(Color.WHITE);
//                tv.setGravity(Gravity.CENTER);
//            }
//            else
//            {
//                tv.setBackgroundColor(Color.WHITE);
//                tv.setTextColor(Color.RED);
//            }
//
//            TableRow trs = new TableRow(context);
//            TableLayout.LayoutParams layoutRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
//            trs.setLayoutParams(layoutRow);
//
//            trs.addView(tv);
//
//            tl.addView(trs);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_face_training, container, false);

        context.setTitle("Face Training");

        mTrainingBtn = (Button) myView.findViewById(R.id.btn_training);
        mTrainingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        imageSwitcher = (ImageSwitcher) myView.findViewById(R.id.imageSwitcher);
        try
        {
            // Declare the animations and initialize them
            Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

            // set the animation type to imageSwitcher
            imageSwitcher.setInAnimation(in);
            imageSwitcher.setOutAnimation(out);

            imageSwitcher.setOnTouchListener(new OnSwipeTouchListener(context)
            {
                public void onSwipeRight() {
                    currentIndex--;
                    updateImageSwitcher();
                };
                public void onSwipeLeft() {
                    currentIndex++;
                    updateImageSwitcher();
                };
            });

            imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    ImageView imageView = new ImageView(context);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT);
                    imageView.setLayoutParams(layoutParams);

                    return imageView;
                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        loadTrainImage();
        updateImageSwitcher();

        checkBox = (CheckBox) myView.findViewById(R.id.item_check);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked())
                {
                    isReset = true;
                }
                else {
                    isReset = false;
                }
            }
        });

        return myView;
    }

    private boolean loadTrainImage()
    {
        trainingImages = getTrainedImageList();
        if (trainingImages.get(0).compareTo("") != 0)
        {
            currentIndex = 0;
            return true;
        }

        return false;
    }

    private void updateImageSwitcher()
    {
        try
        {
            if (currentIndex == numOfTrainedImage)
            {
                currentIndex = 0;
            }
            else if (currentIndex == -1)
            {
                currentIndex = numOfTrainedImage - 1;
            }

            File imgFile = null;
            if (trainingImages.get(currentIndex).compareTo("") != 0)
            {
                imgFile = new File(trainingImages.get(currentIndex));
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    Drawable drawable = new BitmapDrawable(myBitmap);
                    imageSwitcher.setImageDrawable(drawable);
                }
            }
            else
            {
                imageSwitcher.setImageDrawable(getResources().getDrawable(R.drawable.untrained_image));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
                // Error occurred while creating the File;
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

        if (isReset == true)
        {
            checkBox.setChecked(!checkBox.isChecked());
            new trainTask().execute(true);

        }
        else
        {
            new trainTask().execute(false);
        }

    }

    ArrayList<String> getTrainedImageList() {

        ArrayList<String> list = new ArrayList<>();

        SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
        int currIndex = pref.getInt("indexFaceList", -1);
        for(int i = currIndex; i >= 0; i--) {
            list.add(pref.getString("FaceList" + i, ""));
        }
        for(int i = GlobalVariable.maxLengthFaceList - 1; i > currIndex; i--) {
            list.add(pref.getString("FaceList" + i, ""));
        }

        return list;
    }


    private class trainTask extends AsyncTask<Boolean, Void, Void> {
        boolean removeAll;

        @Override
        protected Void doInBackground(Boolean... toRemoveAll) {
            removeAll = toRemoveAll[0];

            Preferences.showLoading(getActivity(), "Training on progress ...", "");

            HttpRequests httpRequests = new HttpRequests(GlobalVariable.apiKey, GlobalVariable.apiSecret);
            File imgFile = new File(mCurrentPhotoPath);

            SharedPreferences pref = getActivity().getSharedPreferences("ATK_pref", 0);
            String auCode = pref.getString("authorizationCode", null);

            String newFaceID = GlobalVariable.get1FaceID(getActivity(), httpRequests, imgFile);
            if(newFaceID != null) {
                String personID = GlobalVariable.getThisPersonID(getActivity(), auCode);

                if (personID.compareTo("") != 0) { //this person has been trained before

                    ArrayList faceIDList = getThisFaceIDList(auCode);
                    faceIDList = substitute1FacefromPerson(httpRequests, personID, faceIDList, newFaceID, removeAll);
                    postFaceIDListtoLocalServer(auCode, faceIDList);
                } else {
                    personID = create1Person(httpRequests, newFaceID);
                    postPersonIDtoLocalServer(auCode, personID);
                    ArrayList<String> faceIDList = new ArrayList<String>();
                    faceIDList.add(newFaceID);
                    postFaceIDListtoLocalServer(auCode, faceIDList);
                }
                //Show notification about sucessful training
                GlobalVariable.saveImageURL(getActivity(), mCurrentPhotoPath);
                Notification.showMessage(getActivity(), 0);

            }

            Preferences.dismissLoading();

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            loadTrainImage();
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
            }

            return personID;
        }

        void postFaceIDListtoLocalServer(String auCode, ArrayList<String> faceIDList) {
            try {
                StringClient client = ServiceGenerator.createService(StringClient.class, auCode);
                Call<ResponseBody> call = client.postFaceIDList(faceIDList);
                Response<ResponseBody> response = call.execute();
                int messageCode = response.code();

                if (messageCode == 200) // SUCCESS
                {

                }
                else
                {
                    if (messageCode == 400) // BAD REQUEST HTTP
                    {

                    }
                    else if (messageCode == 401) // UNAUTHORIZED
                    {

                    }
                    else if (messageCode == 500) // SERVER FAILED
                    {
                        Notification.showMessage(context, 12);
                    }
                    else {

                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        ArrayList substitute1FacefromPerson(HttpRequests httpRequests, String personID, ArrayList faceIDList, String newFaceID, boolean removeAll){

            try {

                // get the earliest faceID in the list, that is the faceID with index 0
                if(removeAll) {
                    PostParameters postParameters = new PostParameters().setPersonId(personID).setFaceId("all");
                    httpRequests.personRemoveFace(postParameters);
                    faceIDList = null;
                }
                else {
                    if (faceIDList != null && faceIDList.size() == GlobalVariable.maxLengthFaceList) {
                        String oldFaceID = faceIDList.get(0).toString();
                        // remove it on Face++
                        PostParameters postParameters = new PostParameters().setPersonId(personID).setFaceId(oldFaceID);
                        httpRequests.personRemoveFace(postParameters);
                        // remove it on the list
                        faceIDList.remove(0);
                    }
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
            }

            return result;

        }
    }

}
