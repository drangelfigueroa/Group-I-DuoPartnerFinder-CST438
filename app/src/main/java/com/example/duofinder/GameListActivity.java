package com.example.duofinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.duofinder.DB.POJO.Game;
import com.example.duofinder.DB.POJO.Plays;
import com.example.duofinder.DB.POJO.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GameListActivity extends AppCompatActivity {
    private DatabaseReference mRef;
    private HashMap<String, Game> game;
    private HashMap<String, Plays> plays;
    private String[] gameDescs;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        mRef = FirebaseDatabase.getInstance().getReference();
        game = new HashMap<>();
        plays = new HashMap<>();
        listView = findViewById(R.id.gameDescsList);
        wireup();
    }

    public void wireup() {
        mRef.child("Game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        game.put(snap.getKey(), snap.getValue(Game.class));
                    }
                }

                populateGameTable();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }

        });
        mRef.child("Plays").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        plays.put(snap.getKey(), snap.getValue(Plays.class));
                    }

                    populateGameTable();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }

        });

    }

    public void populateGameTable() {
        gameDescs = generateGameDescs();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(GameListActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, gameDescs);
        listView.setAdapter(arrayAdapter);

    }

    private String[] generateGameDescs() {
        String[] newGameDescs = new String[game.size()];
        int i = 0;
        int currentGamePlayCount = 0;
        // Iterates through both the Game and Plays hashmaps
        for (Map.Entry<String, Game> gameIter : game.entrySet()){
            for (Map.Entry<String, Plays> playsIter : plays.entrySet()){
                if (gameIter.getKey().equals(playsIter.getValue().gameId)) {
                    // For every play that has the same id as the current game, the counter goes up
                    currentGamePlayCount++;
                }
            }
            newGameDescs[i] = gameIter.getValue().title + "\nCurrnet Players: " + currentGamePlayCount;
            currentGamePlayCount = 0;
            i++;
        }

        return newGameDescs;
    }
}