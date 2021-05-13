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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Objects;

/**
 * This Activity is designed to allow a user to add or remove games from their list.
 * @author Daniel Rangel Figueroa
 */
public class EditGamesActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private ListView mListView;
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
        mListView = findViewById(R.id.list_games);
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
                            getAddedGames(snapshot);
                            mProgressBar.setVisibility(View.GONE);
                            initListView();
                            listViewClick();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Retrieve 'Plays' cancelled." );
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Retrieve 'Games' cancelled." );
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

    /**
     * Used on adapter for ListView.
     * @return All the game titles from the database.
     */
    public String[] getTitles(){
        String[] titles = new String[allGames.size()];
        int i = 0;
        for(Game game : allGames.values()){
            titles[i] = game.title;
            i++;
        }
        return titles;
    }

    /**
     * Populates the addedGames and playsKeys HashMap.
     * @param snapshot firebase snapshot.
     */
    public void getAddedGames(@NonNull DataSnapshot snapshot){
        if(snapshot.exists()) {
            for (DataSnapshot snap : snapshot.getChildren()) {
                Plays plays = snap.getValue(Plays.class);
                assert plays != null;
                addedGames.put(plays.gameId, plays);
                playsKeys.put(plays.gameId, snap.getKey());
            }
            Log.d("Entries found: ", String.valueOf(addedGames.size()));
        }
    }

    /**
     * Initializes ListView.
     */
    public void initListView(){
        mRef.child("Users").child(Objects.requireNonNull(mAuth.getUid())).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                mUser = Objects.requireNonNull(task.getResult()).getValue(User.class);
            }else{
                Log.e("Firebase: ", "Error pulling user");
            }
        });

        String[] gameTitles = getTitles();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EditGamesActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, gameTitles);
        mListView.setAdapter(arrayAdapter);
    }

    /**
     * ListView onClick setup.
     */
    public void listViewClick(){
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            String selection = parent.getItemAtPosition(position).toString();
            StringBuilder GAME_ID = new StringBuilder();
            boolean isAdded = isAdded(selection, GAME_ID);


            alertDialogueSetup(GAME_ID.toString(), isAdded, selection);
        });
    }

    /**
     * Determines if a game is added by user.
     * @return Whether or not a game is added.
     * @param selection String title of game clicked.
     * @param GAME_ID StringBuilder to retrieve game id.
     */
    public boolean isAdded(String selection, StringBuilder GAME_ID){
        for(String gameId: allGames.keySet()){
            if(Objects.requireNonNull(allGames.get(gameId)).title.equals(selection)){
                GAME_ID.append(gameId);
                if(addedGames.containsKey(gameId))
                    return true;
            }
        }
        return false;
    }
    /**
     * Sets up alertDialogue of click.
     * @param gameId The gameId of game that was clicked on.
     * @param isAdded True if game is in user's game list.
     * @param selection The title of alertDialogue (title of what was clicked on).
     */
    public void alertDialogueSetup(final String gameId, final boolean isAdded,  final String selection){
        String text;
        if(isAdded)
            text = "Remove";
        else
            text = "Add";
        AlertDialog.Builder builder = new AlertDialog.Builder(EditGamesActivity.this);
        builder.setPositiveButton(text, (dialog, which) -> {
            dialog.cancel();
            if(isAdded){
                mRef.child("Plays").child(Objects.requireNonNull(playsKeys.get(gameId))).removeValue().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        playsKeys.remove(gameId);
                        addedGames.remove(gameId);
                        Log.d("firebase removed ", gameId);
                        Toast.makeText(EditGamesActivity.this, gameId+" Removed", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.e("firebase", "deletion failed");
                    }

                });
            }else{
                Plays plays = new Plays(mAuth.getUid(), mUser.username, gameId);
                String pushKey = mRef.child("Plays").push().getKey();
                assert pushKey != null;
                mRef.child("Plays").child(pushKey).setValue(plays).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(EditGamesActivity.this, gameId+" Added", Toast.LENGTH_SHORT).show();
                        playsKeys.put(gameId, pushKey);
                        addedGames.put(gameId, plays);
                        Log.d("firebase added ", gameId);
                    }else{
                        Log.e("firebase", "push failed");
                    }
                });
            }
        });
        AlertDialog alert = builder.create();
        alert.setTitle(selection);
        alert.show();
    }
}