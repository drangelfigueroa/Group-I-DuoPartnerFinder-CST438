package com.example.duofinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.duofinder.DB.POJO.Chat;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

public class ChatActivity extends AppCompatActivity {
    private ListView mListView;
    Query mQuery;
    private FirebaseListAdapter<Chat> mAdapter;
    private FloatingActionButton mSendBtn;
    private EditText mMsgET;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mListView = findViewById(R.id.msgListView);
        mAuth = FirebaseAuth.getInstance();
        mMsgET = findViewById(R.id.msgEditText);
        mSendBtn = findViewById(R.id.sendButton);
        mRef = FirebaseDatabase.getInstance().getReference();
        mQuery = mRef.child("Conversations").child(mAuth.getUid()).child(PlayersListActivity.DUO_ID);

        FirebaseListOptions<Chat> options = new FirebaseListOptions.Builder<Chat>()
                .setQuery(mQuery, Chat.class)
                .setLayout(R.layout.message)
                .build();

        mAdapter = new FirebaseListAdapter<Chat>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Chat model, int position) {
                TextView message = (TextView) v.findViewById(R.id.messageTextView);
                TextView username = (TextView) v.findViewById(R.id.usernameTextView);
                TextView time = (TextView) v.findViewById(R.id.timeTextView);

                message.setText(model.message);
                username.setText(model.username);
                time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.time));
            }
        };

        mListView.setAdapter(mAdapter);


        mSendBtn.setOnClickListener(v -> {
            String message = mMsgET.getText().toString();
            if (!message.isEmpty()) {
                // Adds to signed-in user's list of messages
                mRef.child("Conversations").child(mAuth.getUid())
                        .child(PlayersListActivity.DUO_ID).push().setValue(new Chat("Me", mAuth.getUid(), message));
                // Adds to the other user's list of messages
                mRef.child("Conversations").child(PlayersListActivity.DUO_ID)
                        .child(mAuth.getUid()).push().setValue(new Chat(MainActivity.USER.username, mAuth.getUid(), message));
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
        Intent intent = new Intent(ctx, ChatActivity.class);
        return intent;
    }
}

























