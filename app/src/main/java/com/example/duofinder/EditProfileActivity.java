package com.example.duofinder;

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
    private Button mConfirmBtn, mCancelBtn;
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

//        display();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        String email = user.getEmail();
        mName.setText(name);
        mEmail.setText(email);
    }

    protected void display(){
        mName = findViewById(R.id.editName);
        mNewPass = findViewById(R.id.editChangePassword);
        mPassConfirm = findViewById(R.id.editConfirmNewPassword);
        mOldPass = findViewById(R.id.editOldPassword);
        mConfirmBtn = findViewById(R.id.editConfirmBtn);
        mCancelBtn = findViewById(R.id.editCancelBtn);
        mEmail = findViewById(R.id.editEmail);

        mConfirmBtn.setOnClickListener(new View.OnClickListener(){
            String newPass = mNewPass.getText().toString();
            String passConfirm = mPassConfirm.getText().toString();
            String oldPass = mOldPass.getText().toString();
            @Override
            public void onClick(View view) {
                if(oldPass.isEmpty()){
                    mName.setError("Your old Password is required to confirm changes!");
                }
                else if(passwordIsValid(newPass) && passConfirm.isEmpty()){
                    mPassConfirm.setError("You need to confirm the new password in order to set it!");
                }
                else if(passwordIsValid(newPass) || newPass != passConfirm){
                    mPassConfirm.setError("Confirm Password does not match with the new Password you're trying to change");
                }
                else{
                    updateProfile();
                      //moving back to Profile page
//                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
//                    startActivity(i);
                }
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //moving back to Profile page
//                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
//                    startActivity(i);
            }
        });
    }
    protected void updateProfile(){
        mName = findViewById(R.id.editName);
        mNewPass = findViewById(R.id.editChangePassword);
        mPassConfirm = findViewById(R.id.editConfirmNewPassword);
        mOldPass = findViewById(R.id.editOldPassword);
        mEmail = findViewById(R.id.editEmail);
        String name = mName.getText().toString();
        String newPass = mNewPass.getText().toString();
        String newEmail = mEmail.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
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
                        Log.d("TAG", "User email updated.");
                    }
                });



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
