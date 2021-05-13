package com.example.duofinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duofinder.DB.POJO.User;
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
    private TextView mForgotTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        wireUp();
    }

    public void wireUp() {
        mAuth = FirebaseAuth.getInstance();
        mPasswordEt = findViewById(R.id.editTextTextPassword);
        mEmailEt = findViewById(R.id.editTextTextEmailAddress);
        mLogInBtn = findViewById(R.id.buttonLogin);
        mProgress = findViewById(R.id.progressBar);
        mSignUpTV = findViewById(R.id.textViewCreateAcc);
        mForgotTV = findViewById(R.id.textViewForgot);

        mSignUpTV.setOnClickListener(v -> {
            startActivity(SignUpActivity.intentFactory(this));
        });

        mForgotTV.setOnClickListener(v -> showAlert());

        mLogInBtn.setOnClickListener(v -> signIn());
    }

    /**
     * Disables back Button
     */
    @Override
    public void onBackPressed() {
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
                    finish();
                    return;
                }
                mProgress.setVisibility(View.GONE);
                Toast.makeText(SignInActivity.this, "Unable to Sign in. Check credentials.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showAlert(){
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(params);
        editText.requestFocus();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Confirm", null);
        AlertDialog alert = builder.create();
        alert.setTitle("Enter Email");
        alert.setView(editText);

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button confirm = alert.getButton(AlertDialog.BUTTON_POSITIVE);

                confirm.setOnClickListener(v -> {
                    String email = editText.getText().toString().trim();
                    if (email.isEmpty()) {
                        editText.setError("Enter your Email address.");
                        editText.requestFocus();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        editText.setError("Enter a valid Email address.");
                        editText.requestFocus();
                        return;
                    }
                    mAuth.sendPasswordResetEmail(email);
                    Toast.makeText(SignInActivity.this, "Check your email to reset your password.", Toast.LENGTH_LONG).show();
                    alert.dismiss();

                });
            }
        });
        alert.show();

    }
}