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
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mPasswordEt;
    private EditText mEmailEt;
    private Button mLogInBtn;
    private TextView mSignUpTV;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        wireUp();
    }

    public void wireUp() {
        mAuth = FirebaseAuth.getInstance();
        persistentLogin();
        mPasswordEt = findViewById(R.id.editTextTextPassword);
        mEmailEt = findViewById(R.id.editTextTextEmailAddress);
        mLogInBtn = findViewById(R.id.buttonLogin);
        mProgress = findViewById(R.id.progressBar);
        mSignUpTV = findViewById(R.id.textViewCreateAcc);
        mSignUpTV.setOnClickListener(v -> {
            startActivity(SignUpActivity.intentFactory(this));
        });

        mLogInBtn.setOnClickListener(v -> signIn());
    }

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param ctx the Context to switch from
     * @return the Intent to switch to this activity
     */
    public static Intent intentFactory(Context ctx) {
        return new Intent(ctx, SignInActivity.class);
    }

    public void persistentLogin() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null)
            startActivity(MainActivity.intentFactory(SignInActivity.this));
    }

    public void signIn() {
        String email = mEmailEt.getText().toString().trim();
        String password = mPasswordEt.getText().toString().trim();
        if (email.isEmpty()) {
            mEmailEt.setError("Enter your Email address.");
            mEmailEt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailEt.setError("Enter a valid Email address.");
            mEmailEt.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            mPasswordEt.setError("Enter password.");
            mPasswordEt.requestFocus();
            return;
        }

        mProgress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mProgress.setVisibility(View.GONE);
                    startActivity(MainActivity.intentFactory(SignInActivity.this));
                    return;
                }
                mProgress.setVisibility(View.GONE);
                Toast.makeText(SignInActivity.this, "Unable to Sign in. Check credentials.", Toast.LENGTH_SHORT).show();
            }
        });


    }
}