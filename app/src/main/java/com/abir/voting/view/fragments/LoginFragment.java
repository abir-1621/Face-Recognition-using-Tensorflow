package com.abir.voting.view.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.abir.voting.R;
import com.abir.voting.helper.FaceTrackingAnalyzer;
import com.abir.voting.model.VoterInfo;
import com.abir.voting.view.activity.SplashActivity;
import com.abir.voting.view.activity.VotingActivity;
import com.abir.voting.view_models.LoginSignUpViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


import android.graphics.Color;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
public class LoginFragment extends Fragment implements View.OnClickListener, LoginSignUpViewModel.OnSuccessLoginApiCall,FaceTrackingAnalyzer.OnTakePhoto {
    public static String accesstoken;
    private EditText getVerificationPhoneNumber, verificationET, vendorNameET, vendorTinET;
    private DatabaseReference databaseReference;
    private String verificationId, phoneNumber, imageUrl=null;
    private boolean hasFingerprint=false;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private RelativeLayout numberRelative, verificationcodeRelative, newVendorRelative,cameraRelative;
    private Boolean haveAccount = false;
    private ValueEventListener valueEventListener;
    private CardView reSendCode;
    //private CheckConnection connection;
    private LoginSignUpViewModel loginSignUpViewModel;
    private VoterInfo voterInfo;
    private ImageView imageView;
    private Bitmap faceBitmap;

    //for the camera
    public static final int REQUEST_CODE_PERMISSION = 101;
    public static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private TextureView tv;
    private ImageView iv,iv2;
    private static final String TAG = "FaceTrackingActivity";

    public static CameraX.LensFacing lens = CameraX.LensFacing.FRONT;


    BiometricPrompt biometricPrompt;
     BiometricPrompt.PromptInfo promptInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        findAttributes(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //basically this method is use for assign value
        loginSignUpViewModel = new ViewModelProvider(requireActivity()).get(LoginSignUpViewModel.class);
        loginSignUpViewModel.setOnLoginClickListener(this);
        verificationcodeRelative.setVisibility(View.GONE);

        databaseReference = FirebaseDatabase.getInstance().getReference("VoterInfo");
        mAuth = FirebaseAuth.getInstance();


        BiometricManager biometricManager = androidx.biometric.BiometricManager.from(requireContext());
        switch (biometricManager.canAuthenticate()) {

            // this means we can use biometric sensor
            case BiometricManager.BIOMETRIC_SUCCESS:
//                msgtex.setText("You can use the fingerprint sensor to login");
//                msgtex.setTextColor(Color.parseColor("#fafafa"));
                break;

            // this means that the device doesn't have fingerprint sensor
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//                msgtex.setText("This device doesnot have a fingerprint sensor");
//                loginbutton.setVisibility(View.GONE);
                break;

            // this means that biometric sensor is not available
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                msgtex.setText("The biometric sensor is currently unavailable");
//                loginbutton.setVisibility(View.GONE);
                break;

            // this means that the device doesn't contain your fingerprint
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                msgtex.setText("Your device doesn't have fingerprint saved,please check your security settings");
//                loginbutton.setVisibility(View.GONE);
                break;
        }
        // creating a variable for our Executor
        Executor executor = ContextCompat.getMainExecutor(requireContext());
        // this will give us result of AUTHENTICATION
        biometricPrompt = new BiometricPrompt(requireActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(requireContext(), "Login Success", Toast.LENGTH_SHORT).show();

                hasFingerprint=true;
                //                loginbutton.setText("Login Successful");
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        // creating a variable for our promptInfo
        // BIOMETRIC DIALOG
        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("GFG")
                .setDescription("Use your fingerprint to login ").setNegativeButtonText("Cancel").build();
//        loginbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });


    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        switch (v.getId()) {

            case R.id.vps_take_image_button:
                CameraX.unbindAll();
                cameraRelative.setVisibility(View.GONE);
                newVendorRelative.setVisibility(View.VISIBLE);
                firebaseUploadBitmap(faceBitmap);
                break;

            case R.id.vps_takeFinger:
                biometricPrompt.authenticate(promptInfo);
                break;

            case R.id.vps_takeImage:
                System.out.println("############################################# vps_takeImage");
                cameraRelative.setVisibility(View.VISIBLE);
                newVendorRelative.setVisibility(View.GONE);
                if (allPermissionsGranted()) {
                    tv.post(this::startCamera);
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION);
                }
                break;

            case R.id.vps_buttonReset:
                sendVerificationCode(phoneNumber);
                break;
            case R.id.vps_buttonSignIn:
                String name=vendorNameET.getText().toString();
                String nid=vendorTinET.getText().toString();
                if(!imageUrl.isEmpty()&&hasFingerprint){
                    voterInfo=new VoterInfo(name,nid,imageUrl,0,hasFingerprint);
                    databaseReference.child(phoneNumber).setValue(voterInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                }else if(!hasFingerprint){
                    Toast.makeText(requireActivity(),"Please register fingerprint on your device", Toast.LENGTH_LONG).show();
                }else if(imageUrl==null){
                    Toast.makeText(requireActivity(),"Please provide your photo", Toast.LENGTH_LONG).show();
                }



                break;
            case R.id.vpf_buttonContinue:
                phoneNumber = getVerificationPhoneNumber.getText().toString().trim();
                phoneNumber = "+88" + phoneNumber;
                if (phoneNumber.isEmpty() || phoneNumber.length() < 11) {
                    getVerificationPhoneNumber.setError("Number is required");
                    getVerificationPhoneNumber.requestFocus();
                    return;
                }
                numberRelative.setVisibility(View.GONE);
                verificationcodeRelative.setVisibility(View.VISIBLE);
                sendVerificationCode(phoneNumber);

                databaseReference.child(phoneNumber).addValueEventListener(valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println("in data snapshot@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + dataSnapshot.child("name").getValue());
                        haveAccount = dataSnapshot.getValue() != null;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                break;

            case R.id.vps_buttonVerify:
                String code = verificationET.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    verificationET.setError("enter code again");
                    verificationET.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                try {
                    verifyCode(code);
                } catch (Exception ignored) {
                }
                break;
        }

    }


    private void findAttributes(View view) {

        getVerificationPhoneNumber = view.findViewById(R.id.vpf_phone_et);
        Button buttonContinue = view.findViewById(R.id.vpf_buttonContinue);
        progressBar = view.findViewById(R.id.vps_progressbar);
        verificationET = view.findViewById(R.id.vps_editTextCode);
        newVendorRelative = view.findViewById(R.id.vps_relative_newVendor);
        cameraRelative= view.findViewById(R.id.vps_CameraRelative);
        vendorNameET = view.findViewById(R.id.vps_editTextName);
        vendorTinET = view.findViewById(R.id.vps_editTextTin);
        view.findViewById(R.id.vps_buttonVerify).setOnClickListener(this);
        view.findViewById(R.id.vps_takeFinger).setOnClickListener(this);
        view.findViewById(R.id.vps_takeImage).setOnClickListener(this);
        view.findViewById(R.id.vps_take_image_button).setOnClickListener(this);
        view.findViewById(R.id.vps_buttonSignIn).setOnClickListener(this);
        buttonContinue.setOnClickListener(this);
        numberRelative = view.findViewById(R.id.vpf_relative);
        verificationcodeRelative = view.findViewById(R.id.vps_relative);
        reSendCode = view.findViewById(R.id.vps_buttonReset);
        reSendCode.setOnClickListener(this);

        tv = view.findViewById(R.id.tracking_texture_view);
        iv = view.findViewById(R.id.tracking_image_view);

        imageView=view.findViewById(R.id.vps_imagePreview);
//        ibSwitch = view.findViewById(R.id.btn_switch_face);


    }







//    private boolean validatePhone() {
//        String phoneNumber = Objects.requireNonNull(login_phone.getEditText()).getText().toString().trim();
//
//        if (phoneNumber.isEmpty()) {
//            login_phone.setError("Field can't be empty");
//            progressBar.setVisibility(View.GONE);
//            return false;
//
//        } else if (phoneNumber.length() > 11) {
//            login_phone.setError("Please enter a 11 digits number.");
//            progressBar.setVisibility(View.GONE);
//            return false;
//        } else if (phoneNumber.length() < 11) {
//            login_phone.setError("Please enter a 11 digits number.");
//            progressBar.setVisibility(View.GONE);
//            return false;
//        } else {
//            login_phone.setError(null);
//            return true;
//        }
//    }

    private void signInwihCredinteal(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                 verificationcodeRelative.setVisibility(View.GONE);
                 if(haveAccount){
                     Intent intent = new Intent(requireContext(),
                             VotingActivity.class);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                     startActivity(intent);
                     requireActivity().finish();
                    }else {
                     newVendorRelative.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(requireActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    reSendCode.setVisibility(View.VISIBLE);
                }
            }


        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
              requireActivity(),               // Activity (for callback binding)
                mCallBacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s; //contain the verification code sent from google
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //detect the code automaticaly as like iphone
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verificationET.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            //if failed to get verification code
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            reSendCode.setVisibility(View.VISIBLE);
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInwihCredinteal(credential);
    }


    // Camera code
    @SuppressLint("RestrictedApi")
    private void startCamera() {
        initCamera();
//        ibSwitch.setOnClickListener(v -> {
        lens = CameraX.LensFacing.FRONT;
        try {
                Log.i(TAG, "" + lens);
                CameraX.getCameraWithLensFacing(lens);
                initCamera();
            } catch (CameraInfoUnavailableException e) {
                Log.e(TAG, e.toString());
            }
//        });
    }

    private void initCamera() {
        CameraX.unbindAll();
        PreviewConfig pc = new PreviewConfig
                .Builder()
                .setTargetResolution(new Size(tv.getWidth(), tv.getHeight()))
                .setLensFacing(lens)
                .build();

        Preview preview = new Preview(pc);
        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup vg = (ViewGroup) tv.getParent();
            vg.removeView(tv);
            vg.addView(tv, 0);
            tv.setSurfaceTexture(output.getSurfaceTexture());
        });

        ImageAnalysisConfig iac = new ImageAnalysisConfig
                .Builder()
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setTargetResolution(new Size(tv.getWidth(), tv.getHeight()))
                .setLensFacing(lens)
                .build();

        FaceTrackingAnalyzer faceTrackingAnalyzer=new FaceTrackingAnalyzer(tv, iv, lens);
        faceTrackingAnalyzer.setOnItemClickListener(this);
        ImageAnalysis imageAnalysis = new ImageAnalysis(iac);
        imageAnalysis.setAnalyzer(Runnable::run,faceTrackingAnalyzer
                );
        CameraX.bindToLifecycle(this, preview, imageAnalysis);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (allPermissionsGranted()) {
                tv.post(this::startCamera);
            } else {
                Toast.makeText(requireContext(),"Permissions not granted by the user.",Toast.LENGTH_SHORT).show();

            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void firebaseUploadBitmap(Bitmap bitmap) {
        if(bitmap==null){
            Toast.makeText(requireContext(),"Please take a proper image.",Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        StorageReference imageStorage = FirebaseStorage.getInstance().getReference();;
        StorageReference imageRef = imageStorage.child("images/" + phoneNumber);
        Task<Uri> urlTask = imageRef.putBytes(data).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                assert downloadUri != null;
                imageUrl = downloadUri.toString();
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.defaulimage));
                imageUrl=null;
                Toast.makeText(requireActivity(),"Image uploading failed, please try again.", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void uploadImage(Bitmap bitmap) {
        faceBitmap=bitmap;
    }
}

