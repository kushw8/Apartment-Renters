package com.example.rentalapp;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class SecondFragment extends Fragment {
    EditText name, price, place, description,type;
    ImageView mImage;
    Button btnSubmit;
    String email,userID,aname, aprice, aplace ,atype, adescription, productRandomKey, saveCurrentDate, saveCurrentTime;
    ProgressDialog pd;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    private Uri imageUri;
    ImageView imageView;
    private ArrayList<Uri> ImageUris;
    private static final int gallerypic = 1;
    private static final int PICK_IMAGES_CODE = 0;
    int position =0;
    private StorageReference AptImagesRef;
    private String downloadimageurls;
    int counts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_second, container, false);


        name = v.findViewById(R.id.edittxt_name);
        price = v.findViewById(R.id.edittxt_price);
        place = v.findViewById(R.id.edittxt_place);
        type = v.findViewById(R.id.edittxt_type);
        description  = v.findViewById(R.id.edittxt_description);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(getContext());
        imageView = (ImageView) v.findViewById(R.id.imgSelect);
        ImageUris = new ArrayList<>();
        AptImagesRef = FirebaseStorage.getInstance().getReference().child("ApartmentImages");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        btnSubmit = (Button) v.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aname = name.getText().toString().trim();
                aprice = price.getText().toString().trim();
                aplace = place.getText().toString().trim();
                adescription = description.getText().toString().trim();
                atype = type.getText().toString().trim();
                email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                if(ImageUris == null){
                    Toast.makeText(getContext(), "Image is mandatory.", Toast.LENGTH_SHORT).show();
                    imageView.requestFocus();
                }

                uploadImage();

            }
        });
        return v;
    }

    private void uploadImage() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM,dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;



            final StorageReference filePath = AptImagesRef.child(ImageUris.get(0).getLastPathSegment() + productRandomKey + ".jpg");
            final UploadTask uploadTask = filePath.putFile(ImageUris.get(0));

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful())
                            {
                                throw task.getException();
                            }

                            downloadimageurls= filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful())
                            {
                                downloadimageurls= task.getResult().toString();

                                uploadData(aname, aprice, aplace, adescription);
                            }
                        }
                    });
                }
            });



    }

    private void OpenGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery,"Select images"), gallerypic);
    }

    private void uploadData(String aname, String aprice, String aplace, String adescription) {

        String id = UUID.randomUUID().toString();
        Map<String, Object> doc = new HashMap<>();
        Map<String, Object> img = new HashMap<>();
        doc.put("documentid", id);
        doc.put("renterid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        doc.put("email", email);
        doc.put("streetname", aname);
        doc.put("price", Integer.parseInt(aprice));
        doc.put("place", aplace);
        doc.put("type", atype);
        doc.put("description", adescription);
        doc.put("count","1");

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        img.put("count","1");
        img.put("userid",userID);
        img.put("image0",downloadimageurls);


        db.collection("Myads").document(userID).collection("Selected").document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        db.collection("Apartments").document(id).set(doc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        db.collection("ApartmentImages").document(id).set(img).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(getContext(),RenterHomePage.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == gallerypic && resultCode == RESULT_OK){

                Uri imageuri = data.getData();
                ImageUris.add(imageuri);
                imageView.setImageURI(ImageUris.get(0));



        }
    }
}