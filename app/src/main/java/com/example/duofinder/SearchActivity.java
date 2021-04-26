package com.example.duofinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duofinder.DB.POJO.Game;
import com.example.duofinder.DB.POJO.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    private DatabaseReference mRef;
    private User mUser;
    private FirebaseAuth mAuth;

    private HashMap<String, Game> games;
    private HashMap<String, String> players;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        wireUp();
    }
    void wireUp(){
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        games = new HashMap<>();
        players = new HashMap<>();

        mRef.child("Game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap: snapshot.getChildren()){
                        games.put(snap.getKey(), snap.getValue(Game.class));
                    }
                    Log.d("Games shown: ", String.valueOf(games.size()));
                }
                ListView listView = findViewById(R.id.gamesList);
                String[] gameTitles = getTitles();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, gameTitles);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener((parent, view, position, id) ->{
                    String choice = parent.getItemAtPosition(position).toString();
                    String GAME_ID = null;
                    for(String gameID: games.keySet()){
                        if(games.get(gameID).title.equals(choice)){
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
    public String[] getTitles(){
        String[] titles = new String[games.size()];
        int i = 0;
        for(Game game : games.values()){
            titles[i] = game.title;
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
