package com.bipuldevashish.givnidelivery.ui.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bipuldevashish.givnidelivery.R;
import com.bipuldevashish.givnidelivery.model.User;
import com.bipuldevashish.givnidelivery.ui.login.LoginActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.text.TextUtils.isEmpty;
import static com.bipuldevashish.givnidelivery.utils.Helper.doStringsMatch;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    public static int PReqCode = 1;

    //widgets
    private EditText mUserName, mEmail, mPassword, mConfirmPassword;
    private ProgressBar mProgressBar;

    //vars
    private FirebaseFirestore mDb;
    Uri imageUri;
    ImageView mProfileimageView;
    private String imageUrl;
    private StorageReference storageProfilePicsRef;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUserName = findViewById(R.id.etName);
        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etPassword);
        mConfirmPassword = findViewById(R.id.etCpassword);
        mProgressBar = findViewById(R.id.progressBar);
        mProfileimageView = findViewById(R.id.profile_image);
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");
        mDb = FirebaseFirestore.getInstance();


        //click handle
        findViewById(R.id.changeProfile).setOnClickListener(this);
        findViewById(R.id.registerBtn).setOnClickListener(this);
        findViewById(R.id.goBack).setOnClickListener(this);

    }


    public void registerNewEmail(String name, String email, String password, String url) {

        showDialog();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                        uploadProfileImage();

                        //insert some default data
                        User user = new User();
                        user.setEmail(email);
                        user.setUsername(name);
                        user.setAvatar(url);
                        user.setUser_id(FirebaseAuth.getInstance().getUid());

                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                .build();
                        mDb.setFirestoreSettings(settings);

                        DocumentReference newUserRef = mDb
                                .collection(getString(R.string.collection_users))
                                .document(FirebaseAuth.getInstance().getUid());

                        newUserRef.set(user).addOnCompleteListener(task1 -> {
                            hideDialog();

                            if (task1.isSuccessful()) {
                                redirectLoginScreen();
                            } else {
                                View parentLayout = findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                        hideDialog();
                    }

                    // ...
                });
    }

    private void pickImageFromGallery() {
        Log.d(TAG, "pickImageFromGallery: select image from gallery");
        Intent intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(10, 10)
                .getIntent(RegisterActivity.this);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.d(TAG, "onActivityResult: Fetched data");

            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.getUri();
                Log.d(TAG, "onActivityResult: imageuri =" + imageUri);
                mProfileimageView.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(RegisterActivity.this, "You did not choose any image !", Toast.LENGTH_SHORT).show();

            }
        }


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.changeProfile: {
                Log.d(TAG, "onClick: pick image profile from gallery");
                pickImageFromGallery();
                break;
            }

            case R.id.goBack: {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.registerBtn: {
                Log.d(TAG, "onClick: attempting to register.");
                validateAndSignUp();
            }
        }
    }

    private void validateAndSignUp() {

        //check for null valued EditText fields
        Log.d(TAG, "onClick: imageUrl = " + imageUrl);
         if (isEmpty(mUserName.getText().toString())) {
            mUserName.setError("Please Enter Your Full Name");
        } else if (isEmpty(mEmail.getText().toString())) {
            mEmail.setError("Please Enter Your Email");
        } else if (isEmpty(mPassword.getText().toString())) {
            mPassword.setError("Please Enter Password");
        } else if (isEmpty(mConfirmPassword.getText().toString())) {
            mConfirmPassword.setError("Please Confirm Your Password");
        }
        //check if passwords match
        else if (doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())) {

            //Initiate registration task
            registerNewEmail(mUserName.getText().toString(), mEmail.getText().toString(), mPassword.getText().toString(), imageUrl);
        } else {
            Toast.makeText(RegisterActivity.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
        }

    }


    private void redirectLoginScreen() {
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void uploadProfileImage() {
        Log.d(TAG, "uploadProfileImage: uploading profile picture");
        if (imageUri != null) {
            Log.d(TAG, "uploadProfileImage: uri is not null");
            final StorageReference fileRef = storageProfilePicsRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask((Continuation) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {

                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    Log.d(TAG, "uploadProfileImage: myuri = " + imageUrl);

                }
            });
        }
    }

}