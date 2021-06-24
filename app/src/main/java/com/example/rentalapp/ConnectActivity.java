package com.example.rentalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ConnectActivity extends AppCompatActivity {
    private ImageView ProfileImage;
    private EditText UserName, FullName, Country;
    private Button Save;
    Boolean valid = true;
    private FirebaseAuth FAuth;
    private ProgressDialog LoadingBar;
    private StorageReference userProfileImage;

    final static int gallery_pick = 1;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ProfileImage = findViewById(R.id.setup_profile_image);
        UserName = findViewById(R.id.setup_user_name);
        FullName = findViewById(R.id.setup_name);
        Country = findViewById(R.id.setup_country);
        Save = findViewById(R.id.setup_save_button);

        LoadingBar = new ProgressDialog(this);

        FAuth = FirebaseAuth.getInstance();
        currentUserId = FAuth.getCurrentUser().getUid();
        // DRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

     //   userProfileImage = FirebaseStorage.getInstance().getReference().child("Profile Images");


        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(UserName);
                checkField(FullName);
                checkField(Country);
                if (valid) {
                    saveAccountInformation();
                }
            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, gallery_pick);

            }


        });
//
//        DRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
////             //       String image = snapshot.child("profileImage").getValue().toString();String image2 = "https://cfwww.hgregoire.com/photos/by-size/site/480x320/file/2019/10/comme-neuf-1.jpg";
////                    Log.d("TAG","Profile Image " + image);
////                    Picasso.get().load(image).into(ProfileImage);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG","Error");

        if (requestCode == gallery_pick && resultCode == RESULT_OK && data != null) {
            Uri imageURI = data.getData();

//            // start picker to get image for cropping and then use the image in cropping activity
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1,1)
//                    .start(ProfileSetup.this);
//
//
//        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//
            Uri resultUri = null;
            StorageReference filePath = userProfileImage.child(currentUserId + ".jpg");
            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(ConnectActivity.this, "Profile Image Stored", Toast.LENGTH_SHORT).show();

                    Log.d("TAG","Error");

                    final String downloadUrl = task.getResult().getUploadSessionUri().toString();


                }
            });

        } else if (resultCode == 0) {
            Exception error;
            Toast.makeText(this, "Error ", Toast.LENGTH_SHORT).show();
        }


    }
    private void upoad(){



        Log.d("TAG","Error");
        LoadingBar.setTitle("Saving Data");
        LoadingBar.setMessage("Please wait, data is being saved");
        LoadingBar.show();
        LoadingBar.setCanceledOnTouchOutside(true);

  //      DRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
////             //       String image = snapshot.child("profileImage").getValue().toString();String image2 = "https://cfwww.hgregoire.com/photos/by-size/site/480x320/file/2019/10/comme-neuf-1.jpg";
////                    Log.d("TAG","Profile Image " + image);
////                    Picasso.get().load(image).into(ProfileImage);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



        checkField(UserName);
        checkField(FullName);
        checkField(Country);
        if (valid) {
            saveAccountInformation();


        }


    }


    private void saveAccountInformation() {

        String userName = UserName.getText().toString();
        String fullName = FullName.getText().toString();
        String country = Country.getText().toString();

        Log.d("TAG","Error");
        LoadingBar.setTitle("Saving Data");
        LoadingBar.setMessage("Please wait, data is being saved");
        LoadingBar.show();
        LoadingBar.setCanceledOnTouchOutside(true);


        HashMap userMap = new HashMap();
        userMap.put("UserName",userName);
        userMap.put("FullName",fullName);
        userMap.put("Country",country);
        userMap.put("Status","Need To Change");
        userMap.put("Gander","Not Avalable");
        userMap.put("DOB","77777");
        userMap.put("RelationStatus","Not");
        userMap.put("FullName",fullName);
        userMap.put("Country",country);
        userMap.put("FullName",fullName);
        userMap.put("Country",country);


    }


    private void sendUserToUpdatepassword() {
        Log.d("TAG","Error");

        Intent i = new Intent(ConnectActivity.this, UpdatePassword.class);
        startActivity(i);
        finish();
    }


    private void sendUserToregister() {

        Intent i = new Intent(ConnectActivity.this, RegisterActivity.class);
        startActivity(i);
        finish();
    }



    private void sendUserToMainActiviy() {

        Intent i = new Intent(ConnectActivity.this, MainActivity.class);
        startActivity(i);
        finish();

    }
    private void sendUserToMainActivity() {

        Intent i = new Intent(ConnectActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


    private Boolean checkField(EditText TextField) {


        if(TextField.getText().toString().isEmpty()){
            TextField.setError("Enter Text");
            valid = false;

            Log.d("TAG","Error");

        }

        else
            {

            valid = true;
        }

        return valid;
    }

}