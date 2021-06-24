package com.example.rentalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApartmentEditDetails extends AppCompatActivity {
    private TextView Price,Description,Title,Place,Type;
    ImageView edit,add;
    ImageSlider image;
    Button btn_save;
    FirebaseAuth fAuth;
    Toolbar toolbar;
    FirebaseFirestore db;
    String userID;
    int count;
    int counts;
    List<SlideModel> slideModels = new ArrayList<>();
    private ArrayList<Uri> ImageUris;
    private String aname, aprice, aplace, adescription;
    private static final int gallerypic = 1;
    private static final int PICK_IMAGES_CODE = 0;
    private int position = 0;
    private StorageReference AptImagesRef;
    private String[] downloadimageurls = new String[10];
    private String productRandomKey, saveCurrentDate, saveCurrentTime, downloadImageUrl;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment_edit_details);

        image = (ImageSlider) findViewById(R.id.img_apt_edit_details);
        edit = (ImageView) findViewById(R.id.img_edit);
        add = (ImageView) findViewById(R.id.img_add_image);
        Price= (TextView) findViewById(R.id.txt_view_edit_price);
        Description= (TextView) findViewById(R.id.txt_view_edit_description);
        Title = (TextView) findViewById(R.id.txt_view_edit_title);
        Place = (TextView) findViewById(R.id.txt_view_edit_place);
        Type = (TextView) findViewById(R.id.txt_view_edit_type);

        toolbar = findViewById(R.id.renter_apartment_edit_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_save = (Button) findViewById(R.id.btn_apartment_edit_details);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();

        id = intent.getExtras().getString("Id");
        String classid = intent.getExtras().getString("classid");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!classid.equals("renter")){startActivity(new Intent(ApartmentEditDetails.this,AdminHomePage.class));
                    finish();}
                else{startActivity(new Intent(ApartmentEditDetails.this,RenterHomePage.class));
                    finish();}
            }
        });

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ApartmentImages").
                document(id);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                count = Integer.valueOf(documentSnapshot.getString("count"));
                for (int i=0; i<count;i++){
                    slideModels.add(new SlideModel(documentSnapshot.getString("image"+i)));
                }
                image.setImageList(slideModels,true);
                image.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onItemSelected(int position) {
                        // You can listen here.
                        for (int j=0;j<count;j++){
                            if (position == j) {
                                new AlertDialog.Builder(ApartmentEditDetails.this).setMessage("Do you want to delete the iamge")
                                        .setTitle("Delete")
                                        .setCancelable(true)
                                        .setPositiveButton(android.R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        DocumentReference docRef = db.collection("ApartmentImages")
                                                                .document(id);
                                                        Map<String,Object> updates = new HashMap<>();
                                                        updates.put("image"+position, FieldValue.delete());

                                                        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(ApartmentEditDetails.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                                count = count-1;
                                                                DocumentReference docRef2 = db.collection("ApartmentImages")
                                                                        .document(id);
                                                                docRef2.update("count", String.valueOf(count)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        DocumentReference docRef3 = db.collection("Apartments")
                                                                                .document(id);
                                                                        docRef3.update("count", String.valueOf(count)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                onRestart();
                                                                            }
                                                                        });
                                                                    }
                                                                });

                                                            }
                                                        });
                                                    }
                                                }).setNegativeButton(android.R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                    }

                    }
                });
            }
        });

        String type = intent.getExtras().getString("Type");
        String price = intent.getExtras().getString("Price");
        String title = intent.getExtras().getString("Title");
        String place = intent.getExtras().getString("Place");
        String description = intent.getExtras().getString("Description");

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Apartments").document(id)
                        .update(
                                "streetname", Title.getText().toString(),
                                "price", Price.getText().toString(),
                                "place", Place.getText().toString(),
                                "description", Description.getText().toString(),
                                "type", Type.getText().toString()
                        );

                db.collection("Myads").document(userID).collection("Selected").document(id)
                        .update(
                                "streetname", Title.getText().toString(),
                                "price", Price.getText().toString(),
                                "place", Place.getText().toString(),
                                "description", Description.getText().toString(),
                                "type", Type.getText().toString()
                        );

                Price.setEnabled(false);
                Title.setEnabled(false);
                Place.setEnabled(false);
                Description.setEnabled(false);
                Type.setEnabled(false);
                btn_save.setVisibility(View.INVISIBLE);
            }


        });

        Type.setText(type);
        Price.setText(price);
        Title.setText(title);
        Place.setText(place);
        Description.setText(description);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add.setVisibility(View.VISIBLE);
                Price.setEnabled(true);
                Title.setEnabled(true);
                Place.setEnabled(true);
                Description.setEnabled(true);
                btn_save.setVisibility(View.VISIBLE);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
                uploadImage();
            }
        });

    }

    private void OpenGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select images"), gallerypic);
    }
    private void uploadImage() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM,dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        for (int i = 0; i < ImageUris.size(); i++) {

            final StorageReference filePath = AptImagesRef.child(ImageUris.get(i).getLastPathSegment() + productRandomKey + ".jpg");
            final UploadTask uploadTask = filePath.putFile(ImageUris.get(i));
            int finalI = i;
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(ApartmentEditDetails.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ApartmentEditDetails.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            downloadimageurls[finalI] = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadimageurls[finalI] = task.getResult().toString();

                                Toast.makeText(ApartmentEditDetails.this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();
                                uploadData(aname, aprice, aplace, adescription);

                            }
                        }
                    });
                }
            });
        }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == gallerypic && resultCode == RESULT_OK){
            if(data.getClipData()!=null){

                int count = data.getClipData().getItemCount();
                counts = count;
                for(int i=0;i<count;i++){
                    Uri imageuri = data.getClipData().getItemAt(i).getUri();
                    ImageUris.add(imageuri);
                }
            }
            else{
                Uri imageuri = data.getData();
                ImageUris.add(imageuri);

            }

        }
    }

    private void uploadData(String aname, String aprice, String aplace, String adescription) {

        Map<String, Object> doc = new HashMap<>();
        Map<String, Object> img = new HashMap<>();
        doc.put("count",counts);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        img.put("count",counts);
        for(int i =0;i<counts;i++){
            img.put("image"+i,downloadimageurls[i]);
        }


        db.collection("Myads").document(userID).collection("Selected").document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ApartmentEditDetails.this, "Uploaded", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ApartmentEditDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        db.collection("Apartments").document(id).set(doc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ApartmentEditDetails.this, "Uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ApartmentEditDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        db.collection("ApartmentImages").document(id).set(img).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

}