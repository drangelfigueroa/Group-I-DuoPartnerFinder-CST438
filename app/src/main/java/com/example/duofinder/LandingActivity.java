package com.example.duofinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends AppCompatActivity {
    private Button mProfileBtn;
    private Button mLogoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mProfileBtn = findViewById(R.id.buttonProfile);
        mLogoutBtn = findViewById(R.id.buttonLogout);

        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ProfileActivity.intentFactory(getApplicationContext());
                finish();
                startActivity(intent);
            }
        });

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = SignUpActivity.intentFactory(getApplicationContext());
                finish();
                startActivity(intent);
            }
        });
    }

    public static Intent intentFactory(Context ctx) {
        Intent intent = new Intent(ctx, LandingActivity.class);
        return intent;
    }
}