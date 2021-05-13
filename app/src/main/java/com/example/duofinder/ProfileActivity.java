package com.example.duofinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView mName;
    private Button mEditBtn, mAddGamesBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mName = findViewById(R.id.profileName);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mEditBtn = findViewById(R.id.profileEditBtn);
        mAddGamesBtn = findViewById(R.id.profileAddGames);

        mName.setText(MainActivity.USER.username);

        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(i);
            }
        });
        mAddGamesBtn.setOnClickListener(view -> startActivity(EditGamesActivity.intentFactory(ProfileActivity.this)));
    }

    public static Intent intentFactory(Context ctx) {
        Intent intent = new Intent(ctx, ProfileActivity.class);
        return intent;
    }
}
