package com.example.duofinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.duofinder.DB.POJO.Game;
import com.example.duofinder.DB.POJO.Plays;
import com.example.duofinder.DB.POJO.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditGamesActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private User mUser;

    //Key is mGameId
    private HashMap<String, Game> allGames;
    //Key is mGameId
    private HashMap<String, Plays> addedGames;
    //Key is mGamID values are DB keys
    private HashMap<String, String> playsKeys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_games);
        mProgressBar = findViewById(R.id.progressBarAddGame);
        mProgressBar.setVisibility(View.VISIBLE);
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        allGames = new HashMap<>();
        addedGames = new HashMap<>();
        playsKeys = new HashMap<>();

        //Loads HashMap allGames
        mRef.child("Game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap: snapshot.getChildren()){
                        allGames.put(snap.getKey(), snap.getValue(Game.class));
                    }
                    Query getPlays = mRef.child("Plays").orderByChild("userId").equalTo(mAuth.getUid());
                    //Loads HashMap addedGames
                    getPlays.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Plays plays = snap.getValue(Plays.class);
                                    addedGames.put(plays.gameId, plays);
                                    playsKeys.put(plays.gameId, snap.getKey());
                                }
                                Log.d("Entries found: ", String.valueOf(addedGames.size()));
                            }
                            mProgressBar.setVisibility(View.GONE);
                            //INITIALIZE LIST VIEW
                            mRef.child("Users").child(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        mUser = task.getResult().getValue(User.class);
                                    }else{
                                        Log.e("Firebase: ", "Error pulling user");
                                    }
                                }
                            });
                            ListView listView = findViewById(R.id.list_games);
                            String[] gameTitles = getTitles();
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditGamesActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, gameTitles);
                            listView.setAdapter(arrayAdapter);
                            listView.setOnItemClickListener((parent, view, position, id) -> {
                                String selection = parent.getItemAtPosition(position).toString();
                                boolean remove = false;
                                String GAME_ID = null;
                                for(String gameId: allGames.keySet()){
                                    if(allGames.get(gameId).title.equals(selection)){
                                        GAME_ID = gameId;
                                        if(addedGames.containsKey(gameId)) {
                                            remove = true;
                                            break;
                                        }
                                    }
                                }
                                String action;
                                if(remove)
                                    action = "Remove";
                                else
                                    action = "Add";

                                AlertDialog.Builder builder = new AlertDialog.Builder(EditGamesActivity.this);
                                String finalGAME_ID = GAME_ID;
                                boolean finalRemove = remove;
                                builder.setPositiveButton(action, (dialog, which) -> {
                                            dialog.cancel();
                                            if(finalRemove){
                                                mRef.child("Plays").child(playsKeys.get(finalGAME_ID)).removeValue().addOnCompleteListener(task -> {
                                                    if(task.isSuccessful()) {
                                                        playsKeys.remove(finalGAME_ID);
                                                        addedGames.remove(finalGAME_ID);
                                                        Log.d("firebase removed ", finalGAME_ID);
                                                        Toast.makeText(EditGamesActivity.this, finalGAME_ID+" Removed", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Log.e("firebase", "deletion failed");
                                                    }

                                                });
                                            }else{
                                                Plays plays = new Plays(mAuth.getUid(), mUser.username, finalGAME_ID);
                                                String pushKey = mRef.child("Plays").push().getKey();
                                                mRef.child("Plays").child(pushKey).setValue(plays).addOnCompleteListener(task -> {
                                                    if(task.isSuccessful()) {
                                                        Toast.makeText(EditGamesActivity.this, finalGAME_ID+" Added", Toast.LENGTH_SHORT).show();
                                                        playsKeys.put(finalGAME_ID, pushKey);
                                                        addedGames.put(finalGAME_ID, plays);
                                                        Log.d("firebase added ", finalGAME_ID);
                                                    }else{
                                                        Log.e("firebase", "push failed");
                                                    }
                                                });
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.setTitle(selection);
                                alert.show();
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

    /**
     * Factory pattern provided Intent to switch to this activity.
     *
     * @param ctx the Context to switch from
     * @return the Intent to switch to this activity
     */
    public static Intent intentFactory(Context ctx) {
        return new Intent(ctx, EditGamesActivity.class);
    }

    public String[] getTitles(){
        String[] titles = new String[allGames.size()];
        int i = 0;
        for(Game game : allGames.values()){
            titles[i] = game.title;
            i++;
        }
        return titles;
    }
}