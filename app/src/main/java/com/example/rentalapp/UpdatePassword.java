package com.example.rentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdatePassword extends AppCompatActivity {

    EditText currentPass, newPass, renewPass;
    Button update;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        currentPass = findViewById(R.id.edt_Txt_Current_password);
        newPass = findViewById(R.id.edt_txt_new_password);
        renewPass = findViewById(R.id.edt_txt_reenter_password);
        update = findViewById(R.id.btn_update_password);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentPassword = currentPass.getText().toString().trim();
                String newPassword = newPass.getText().toString().trim();
                String renewPassword = renewPass.getText().toString().trim();

                Log.d("TAG","Value " + currentPassword);
                Log.d("TAG","Value " + newPassword);
                Log.d("TAG","Value " + renewPassword);

                if(currentPassword.isEmpty()){
                    currentPass.setError("Enter your current password");
                    currentPass.requestFocus();
                    return;
                }
                if(newPassword.isEmpty()){
                    newPass.setError("Enter your new password");
                    newPass.requestFocus();
                    return;
                }
                if(renewPassword.isEmpty()){
                    renewPass.setError("Re-enter your new password");
                    renewPass.requestFocus();
                    return;
                }
                if(newPassword.length()<6){
                    newPass.setError("password length should be more than six ");
                    newPass.requestFocus();
                    return;
                }
                if(newPassword != renewPassword){
                    renewPass.setError("password does-not match ");
                    renewPass.requestFocus();
                    return;
                }



                updatePassword(currentPassword,newPassword);
            }
        });


    }

    private void updatePassword(String current, String newpass) {
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), current);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user.updatePassword(newpass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG","Update Password complete");
                        Toast.makeText(UpdatePassword.this, "Password updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UpdatePassword.this, FourthFragment.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG","Update Password failed");
                        Toast.makeText(UpdatePassword.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","Enter wrong password");
                Toast.makeText(UpdatePassword.this, "please enter the correct password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}