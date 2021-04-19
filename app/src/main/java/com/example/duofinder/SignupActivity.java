package com.example.duofinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mUsernameEt;
    private EditText mPasswordEt;
    private EditText mPasswordConfEt;
    private EditText mEmailEt;
    private TextView mSignInTV;
    private Button mSignUpBtn;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        wireUp();
    }

    private void wireUp() {
        mAuth = FirebaseAuth.getInstance();
        mUsernameEt = findViewById(R.id.editTextUsername);
        mEmailEt = findViewById(R.id.editTextTextEmailAddress);
        mPasswordEt = findViewById(R.id.editTextTextPassword);
        mPasswordConfEt = findViewById(R.id.editTextTextPasswordConf);
        mSignUpBtn = findViewById(R.id.buttonSignUp);
        mProgress = findViewById(R.id.progressBar);
        mSignInTV = findViewById(R.id.textViewSignIn);
        mSignInTV.setOnClickListener(v -> {
            startActivity(SignInActivity.intentFactory(SignupActivity.this));
        });
        mSignUpBtn.setOnClickListener(v -> {
            String username = mUsernameEt.getText().toString().trim();
            String email = mEmailEt.getText().toString().trim();
            String password = mPasswordEt.getText().toString().trim();
            String passwordConf = mPasswordConfEt.getText().toString().trim();
            if (!usernameIsValid(username)) {
                mUsernameEt.setError("Must begin with letter, no whitespace, and length 6-30.");
                mUsernameEt.requestFocus();
                return;
            }
            if (!passwordIsValid(password)) {
                mPasswordEt.setError("Must not contain whitespace and length 6-30.");
                mPasswordEt.requestFocus();
                return;
            }
            if (!password.equals(passwordConf)) {
                mPasswordConfEt.setError("Password does not match.");
                mPasswordConfEt.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailEt.setError("Provide valid E-Mail.");
                mEmailEt.requestFocus();
                return;
            }
            mProgress.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(username, email);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignupActivity.this, "User registered.", Toast.LENGTH_LONG).show();
                                            startActivity(SignInActivity.intentFactory(SignupActivity.this));
                                        } else {
                                            Toast.makeText(SignupActivity.this, "Try again.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(SignupActivity.this, "Try again.", Toast.LENGTH_LONG).show();
                            }
                            mProgress.setVisibility(View.GONE);
                        }
                    });

        });

    }

    /**
     * Checks if a username's format is valid.
     *
     * @param username the username to check.
     * @return a boolean representing if the username is valid.
     */
    private boolean usernameIsValid(String username) {
        Pattern usernameRegex = Pattern.compile(
                "^[a-zA-Z]" +       // starts with a letter
                        "(?!.*\\s|.*\\W)" + // contains no whitespace/special chars
                        ".{5,29}" +         // between 6 and 30 characters long
                        "$");               // end of string
        return usernameRegex.matcher(username).matches();
    }

    /**
     * Checks if a password's format is valid.
     *
     * @param password desired password.
     * @return True if password is valid.
     */
    private boolean passwordIsValid(String password) {
        Pattern passwordRegex = Pattern.compile(
                "^*" +       // start of string
                        "[^\\s]" +   // contains no whitespace
                        "{5,29}" +   // between 6 and 30 characters long
                        "$");        // end of string
        return passwordRegex.matcher(password).matches();
    }

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param ctx the Context to switch from
     * @return the Intent to switch to this activity
     */
    public static Intent intentFactory(Context ctx) {
        return new Intent(ctx, SignupActivity.class);
    }

}