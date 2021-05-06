package com.example.duofinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    Button mAddGamesBtn, mDeleteUsersBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        wireUp();
    }

    void wireUp(){
        mAddGamesBtn = findViewById(R.id.adminAddGames);
        mDeleteUsersBtn = findViewById(R.id.adminDeleteUsers);

        mAddGamesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddGameActivity.class);
                startActivity(i);
            }
        });
        mDeleteUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DeleteUserActivity.class);
                startActivity(i);
            }
        });
    }
}
