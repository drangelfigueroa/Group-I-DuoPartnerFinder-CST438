package com.example.duofinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    String userId;
    private FirebaseAuth mAuth;
    private TextView mWelcomeTV;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWelcomeTV = findViewById(R.id.textViewUsername);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();


        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    HashMap info = (HashMap) task.getResult().getValue();
                    mWelcomeTV.setText("Email: " + info.get("mEmail") + "\n" + "Username: " + info.get("mUsername"));
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                }
            }
        });


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

//    /**
//     * Disables back button.
//     */
//    @Override
//    public void onBackPressed() {}
}