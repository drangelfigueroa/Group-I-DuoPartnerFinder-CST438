package com.example.duofinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;

import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    private EditText mName, mNewPass, mPassConfirm, mOldPass, mEmail;
    private Button mConfirmBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        wireUp();
    }
    void wireUp(){
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference();

        mName = findViewById(R.id.editName);
        mNewPass = findViewById(R.id.editChangePassword);
        mPassConfirm = findViewById(R.id.editConfirmNewPassword);
        mOldPass = findViewById(R.id.editOldPassword);
        mConfirmBtn = findViewById(R.id.editConfirmBtn);
        mEmail = findViewById(R.id.editEmail);

        String name = mUser.getDisplayName();
        String email = mUser.getEmail();
        mName.setText(MainActivity.USER.username);
        mEmail.setText(email);

        mConfirmBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String newPass = mNewPass.getText().toString();
                String passConfirm = mPassConfirm.getText().toString();
                String oldPass = mOldPass.getText().toString();
                if(oldPass.isEmpty()){
                    mOldPass.setError("You need to enter your Old Password before going through with these changes!");
                }
                if(passwordIsValid(newPass) && !newPass.equals(passConfirm)){
                    mPassConfirm.setError("New Password Does not match with the confirmation!");
                    mNewPass.setError("New Password Does not match with the confirmation!");
                }
                if(passwordIsValid(newPass) && newPass.equals(passConfirm)){
                    updateProfile();
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(i);
                    Toast.makeText(EditProfileActivity.this, "Updated Profile!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    protected void updateProfile(){
        mName = findViewById(R.id.editName);
        mNewPass = findViewById(R.id.editChangePassword);
        mPassConfirm = findViewById(R.id.editConfirmNewPassword);
        mOldPass = findViewById(R.id.editOldPassword);
        mEmail = findViewById(R.id.editEmail);
        mRef = FirebaseDatabase.getInstance().getReference();

        String name = mName.getText().toString();
        String newPass = mNewPass.getText().toString();
        String newEmail = mEmail.getText().toString();

        //Updating User info in User OBJ
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uID = user.getUid();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRef.child("Users").child(uID).child("username").setValue(name);
                        Toast.makeText(EditProfileActivity.this, "Updated Profile!", Toast.LENGTH_SHORT).show();
                    }
                });

        user.updatePassword(newPass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "User password updated.");
                    }
                });
        user.updateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRef.child("Users").child(uID).child("email").setValue(newEmail);
                        Log.d("TAG", "User email updated.");
                    }
                });



    }
    public static Intent intentFactory(Context ctx) {
        Intent intent = new Intent(ctx, ProfileActivity.class);
        return intent;
    }
    /**
     * Checks if a password's format is valid.
     * @param password desired password.
     * @return         True if password is valid.
     */
    private boolean passwordIsValid(String password) {
        Pattern passwordRegex = Pattern.compile(
                "^*" +       // start of string
                        "[^\\s]" +   // contains no whitespace
                        "{5,29}" +   // between 6 and 30 characters long
                        "$");        // end of string
        return passwordRegex.matcher(password).matches();
    }
}
