package com.example.duofinder;
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
import com.example.duofinder.DB.POJO.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Activity is designed to allow a user to create an account.
 * @author Daniel Rangel Figueroa
 */
public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mUsernameEt;
    private EditText mPasswordEt;
    private EditText mPasswordConfEt;
    private EditText mEmailEt;
    private ProgressBar mProgress;
    private String mEmail;
    private String mPassword;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        wireUp();
    }

    /**
     * Wires up display.
     */
    private void wireUp() {
        mAuth = FirebaseAuth.getInstance();
        mUsernameEt = findViewById(R.id.editTextUsername);
        mEmailEt = findViewById(R.id.editTextTextEmailAddress);
        mPasswordEt = findViewById(R.id.editTextTextPassword);
        mPasswordConfEt = findViewById(R.id.editTextTextPasswordConf);
        Button mSignUpBtn = findViewById(R.id.buttonSignUp);
        mProgress = findViewById(R.id.progressBar);
        TextView mSignInTV = findViewById(R.id.textViewSignIn);

        mSignInTV.setOnClickListener(v -> {
            startActivity(SignInActivity.intentFactory(SignUpActivity.this));
            finish();
        });

        mSignUpBtn.setOnClickListener(v -> {
            if(isFormatted())
                    createAccount();
        });

    }

    /**
     * Disables back Button
     */
    @Override
    public void onBackPressed() {
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
        return new Intent(ctx, SignUpActivity.class);
    }

    /**
     * Checks if information from form is formatted. Throws an error if not.
     * @return Whether the information is formatted.
     */
    public boolean isFormatted(){
        mUsername = mUsernameEt.getText().toString().trim();
        mEmail = mEmailEt.getText().toString().trim();
        mPassword = mPasswordEt.getText().toString().trim();
        String passwordConf = mPasswordConfEt.getText().toString().trim();
        if (!usernameIsValid(mUsername)) {
            mUsernameEt.setError("Must begin with letter, no whitespace, and length 6-30.");
            mUsernameEt.requestFocus();
            return false;
        }
        if (!passwordIsValid(mPassword)) {
            mPasswordEt.setError("Must not contain whitespace and length 6-30.");
            mPasswordEt.requestFocus();
            return false;
        }
        if (!mPassword.equals(passwordConf)) {
            mPasswordConfEt.setError("Password does not match.");
            mPasswordConfEt.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            mEmailEt.setError("Provide valid E-Mail.");
            mEmailEt.requestFocus();
            return false;
        }
        mProgress.setVisibility(View.VISIBLE);
        return true;
    }

    /***
     * Creates an account from the information provided in form.
     */
    public void createAccount(){
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = new User(mUsername, mEmail, false);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                .setValue(user).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                startActivity(SignInActivity.intentFactory(SignUpActivity.this));
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(SignUpActivity.this,"User already exists." , Toast.LENGTH_LONG).show();
                    }
                    mProgress.setVisibility(View.GONE);
                });
    }
}