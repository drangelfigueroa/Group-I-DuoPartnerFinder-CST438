package com.example.duofinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class LandingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        wireUp();
    }

    /**
     * Wires displays.
     */
    public void wireUp() {
        mAuth = FirebaseAuth.getInstance();
        TextView mUsernameTV = findViewById(R.id.textViewUsername);
        mUsernameTV.setText(MainActivity.USER.username);

        TextView mLogoutTV = findViewById(R.id.textViewLogout);
        mLogoutTV.setOnClickListener(v -> logout());

        Button mChatBtn = findViewById(R.id.buttonChat);
        mChatBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(i);
        });

        Button mProfileBtn = findViewById(R.id.buttonProfile);
        mProfileBtn.setOnClickListener(v -> startActivity(ProfileActivity.intentFactory(this)));

        if (MainActivity.USER.isAdmin) {
            Button mAdminBtn = findViewById(R.id.buttonAdmin);
            mAdminBtn.setVisibility(View.VISIBLE);
            mAdminBtn.setOnClickListener(v -> {
                Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(i);
            });
        }

    }

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param ctx the Context to switch from
     * @return the Intent to switch to this activity
     */
    public static Intent intentFactory(Context ctx) {
        return new Intent(ctx, LandingActivity.class);
    }

    /**
     * Logouts user
     */
    public void logout() {
        mAuth.signOut();
        startActivity(MainActivity.intentFactory(LandingActivity.this));
        finish();
    }

    /**
     * Disables back Button
     */
    @Override
    public void onBackPressed() {
    }
}