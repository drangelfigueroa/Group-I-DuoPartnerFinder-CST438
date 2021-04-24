package com.example.duofinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.duofinder.DB.POJO.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static User USER;
    private FirebaseAuth mAuth;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgress = findViewById(R.id.progressBarStart);
        mProgress.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        if (isSignedIn()) {
            getUser(mAuth.getUid());
        } else {
            mProgress.setVisibility(View.GONE);
            startActivity(SignInActivity.intentFactory(this));
            finish();
        }

    }

    /**
     * Disables back Button
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Checks to see if user is signed in.
     *
     * @return Returns true if user is signed in.
     */
    private boolean isSignedIn() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null;
    }

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param ctx the Context to switch from
     * @return the Intent to switch to this activity
     */
    public static Intent intentFactory(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        return intent;
    }

    /**
     * Pulls user from database then goes to Landing page.
     *
     * @param id User id.
     */
    void getUser(final String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    USER = Objects.requireNonNull(task.getResult()).getValue(User.class);
                    mProgress.setVisibility(View.GONE);
                    startActivity(LandingActivity.intentFactory(MainActivity.this));
                    finish();
                } else
                    Log.e("firebase", "error pulling user.");
            }
        });
    }
}