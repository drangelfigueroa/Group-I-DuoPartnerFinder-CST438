package com.example.duofinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.duofinder.DB.POJO.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserListActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private HashMap<String, User> user;
    private HashMap<String, String> userKeys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
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
                String[] userNames = getUserData();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UserListActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, userNames);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    public String[] getUserData(){
        String[] userdata = new String[user.size()];
        int i = 0;
        for(User user : user.values()){
            userdata[i] = user.username + "\n" + user.email;
            i++;
        }
        return userdata;
    }

    public static Intent intentFactory(Context ctx) {
        return new Intent(ctx, UserListActivity.class);
    }
}