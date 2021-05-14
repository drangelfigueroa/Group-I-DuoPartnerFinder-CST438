package com.example.duofinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duofinder.DB.POJO.Game;
import com.example.duofinder.DB.POJO.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class AddGameActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDB;
    private DatabaseReference mRef;
    private TextView mAddGameName, mAddGameTitle;
    private Button mConfirmBtn, mCancelBtn;
    private HashMap <String, User> user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_games);
        wireUp();
    }

    void wireUp(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDB = FirebaseDatabase.getInstance();
        mRef = mDB.getReference();

        mAddGameTitle = findViewById(R.id.addGameTitle);
        mAddGameName = findViewById(R.id.addGameName);
        mConfirmBtn = findViewById(R.id.addConfirmBtn);
        mCancelBtn = findViewById(R.id.addCancelBtn);

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gameTitle = mAddGameTitle.getText().toString();
                String gameName = mAddGameName.getText().toString();

                if(gameName.isEmpty()) {
                    mRef.child("Game").child(gameTitle).setValue(new Game(gameTitle)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                                startActivity(i);
                                Toast.makeText(AddGameActivity.this, "Game Added!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    mRef.child("Game").child(gameName).setValue(new Game(gameTitle)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                                startActivity(i);
                                Toast.makeText(AddGameActivity.this, "Game Added!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(i);
            }
        });

    }
}
