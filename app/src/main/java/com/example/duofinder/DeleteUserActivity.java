package com.example.duofinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.duofinder.DB.POJO.Game;
import com.example.duofinder.DB.POJO.Plays;
import com.example.duofinder.DB.POJO.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DeleteUserActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private User mUser;
    private HashMap<String, User> user;
    private HashMap<String, String> userKeys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = new HashMap<>();
        userKeys = new HashMap<>();

        mRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        user.put(snap.getKey(), snap.getValue(User.class));
                        User users = snap.getValue(User.class);
                        userKeys.put(users.username, snap.getKey());
                    }
                }
                ListView listView = findViewById(R.id.usersList);
                String[] userNames = getUsernames();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DeleteUserActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, userNames);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    String selection = parent.getItemAtPosition(position).toString();
                    String USER_ID = null;
                    for (String userID : user.keySet()) {
                        if (user.get(userID).username.equals(selection)) {
                            USER_ID = userID;
                            break;
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(DeleteUserActivity.this);
                    String finalUSER_ID = USER_ID;
                    builder.setPositiveButton("Remove", (dialog, which) -> {
                        dialog.cancel();
                            mRef.child("Users").child(userKeys.get(finalUSER_ID)).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    userKeys.remove(finalUSER_ID);
                                    Log.d("firebase removed ", finalUSER_ID);
                                    Toast.makeText(DeleteUserActivity.this, finalUSER_ID + " Removed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("firebase", "deletion failed");
                                }
                            });
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

    public String[] getUsernames(){
        String[] usernames = new String[user.size()];
        int i = 0;
        for(User user : user.values()){
            usernames[i] = user.username;
            i++;
        }
        return usernames;
    }

    public static Intent intentFactory(Context ctx) {
        return new Intent(ctx, DeleteUserActivity.class);
    }
}