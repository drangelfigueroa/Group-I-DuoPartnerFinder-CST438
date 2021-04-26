package com.example.duofinder;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duofinder.DB.POJO.Plays;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PlayersListActivity extends AppCompatActivity {
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String gameID;
    private HashMap<String, Plays> players;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);
        wireUp();
    }
    void wireUp(){
        Bundle extras = getIntent().getExtras();
        gameID = extras.getString("gameID");
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        //GameID, Username
        players = new HashMap<>();
        mRef.child("Plays").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        if(snap.getValue(Plays.class).userId.equals(mUser.getUid())){
                            continue;
                        }
                        if(snap.getValue(Plays.class).gameId.equals(gameID)){
                            players.put(snap.getKey(), snap.getValue(Plays.class));
                        }
                    }
                    Log.d("Players found: ", String.valueOf(players.size()));
                }
                ListView listView = findViewById(R.id.playerList);
                String[] playerList = getPlayers();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PlayersListActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, playerList);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public String[] getPlayers(){
        String[] playerList = new String[players.size()];
        int i = 0;
        for(Plays play : players.values()){
            playerList[i] = play.username;
            i++;
        }
        return playerList;
    }
}
