package com.example.duofinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    Button mAddGamesBtn, mGameListBtn, mUserListBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        wireUp();
    }

    void wireUp(){
        mAddGamesBtn = findViewById(R.id.adminAddGames);
        mGameListBtn = findViewById(R.id.adminGameList);
        mUserListBtn = findViewById(R.id.adminUserList);

        mAddGamesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddGameActivity.class);
                startActivity(i);
            }
        });
        mGameListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GameListActivity.class);
                startActivity(i);
            }
        });
        mUserListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), UserListActivity.class);
                startActivity(i);
            }
        });
    }
}
