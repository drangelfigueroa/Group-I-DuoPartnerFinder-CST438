package com.example.duofinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duofinder.DB.POJO.Game;
import com.example.duofinder.DB.POJO.Plays;
import com.example.duofinder.DB.POJO.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    //Contains all the games
    private HashMap<String, Game> allGames;
    //Contains the games that the user has added to their profile
    private HashMap<String, Plays> addedGames;
    //Gets the keys of the addedGames
    private HashMap<String, String> keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        wireUp();
    }

    void wireUp() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        allGames = new HashMap<>();
        addedGames = new HashMap<>();
        keys = new HashMap<>();

        //Creates a ListView for the games that the User has added to their profile
        mRef.child("Game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        allGames.put(snap.getKey(), snap.getValue(Game.class));
                    }
                    Query getPlays = mRef.child("Plays").orderByChild("userId").equalTo(mAuth.getUid());

                    getPlays.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Plays p = snap.getValue(Plays.class);
                                    addedGames.put(p.gameId, p);
                                    keys.put(p.gameId, snap.getKey());
                                }
                                Log.d("Entries found: ", String.valueOf(allGames.size()));
                            }
                            ListView listView = findViewById(R.id.gamesList);
                            String[] gameTitles = getTitles();
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, gameTitles);
                            listView.setAdapter(arrayAdapter);
                            listView.setOnItemClickListener((parent, view, position, id) -> {
                                String choice = parent.getItemAtPosition(position).toString();
                                String GAME_ID = null;
                                for (String gameID : allGames.keySet()) {
                                    if (allGames.get(gameID).title.equals(choice)) {
                                        GAME_ID = gameID;
                                        changeToPlayersList(GAME_ID);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String[] getTitles(){
        String[] titles = new String[addedGames.size()];
        int i = 0;
        for(Plays g : addedGames.values()){
            String gameName = allGames.get(g.gameId).title;
            titles[i] = gameName;
            i++;
        }
        return titles;
    }
    void changeToPlayersList(String gameName){
        Intent i = new Intent(getApplicationContext(), PlayersListActivity.class);
        i.putExtra("gameID", gameName);
        startActivity(i);
    }
}
