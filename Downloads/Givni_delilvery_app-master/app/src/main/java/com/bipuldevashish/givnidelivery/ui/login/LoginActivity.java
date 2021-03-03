package com.bipuldevashish.givnidelivery.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bipuldevashish.givnidelivery.R;
import com.bipuldevashish.givnidelivery.model.User;
import com.bipuldevashish.givnidelivery.ui.MainActivity;
import com.bipuldevashish.givnidelivery.ui.signup.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    LinearLayout noAccount;
    Button logIn;
    EditText email, password;
    ProgressBar mProgressBar;

    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase Auth Init
        firebaseAuth = FirebaseAuth.getInstance();

        // Progress Dialog Init
        mProgressBar = new ProgressBar(this);

        // View Ids
        noAccount = findViewById(R.id.linearlayoutNoaccount);
        logIn = findViewById(R.id.loginBtn);
        email = findViewById(R.id.etMobile);
        password = findViewById(R.id.etPassword);
        logIn.setOnClickListener(this);
        noAccount.setOnClickListener(this);
        setupFirebaseAuth();

    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void checkAndLogInUser() {

        String user_email = email.getText().toString().trim();
        String user_pass = password.getText().toString().trim();

        if (user_email.isEmpty()) {
            email.setError("Enter Mobile Number");
        }
        else if (user_pass.isEmpty()) {
            password.setError("Enter Password");
        } else {
            showDialog();
            signIn(user_email, user_pass);
        }
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .build();
                db.setFirestoreSettings(settings);

                DocumentReference userRef = db.collection(getString(R.string.collection_users))
                        .document(user.getUid());

                userRef.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: successfully set the user client.");
                        User user1 = task.getResult().toObject(User.class);
                        Log.d(TAG, "setupFirebaseAuth: " + user1);
                    }
                });

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        };
    }

    private void signIn(String email, String password) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> hideDialog()).addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    hideDialog();
                });

    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        {
            switch (view.getId()) {
                case R.id.loginBtn: {
                    checkAndLogInUser();
                    break;
                }

                case R.id.linearlayoutNoaccount: {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    }
}