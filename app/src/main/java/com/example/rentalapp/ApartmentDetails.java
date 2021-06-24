package com.example.rentalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.rentalapp.tenantfragments.AllAdsFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ApartmentDetails extends AppCompatActivity {

    private TextView Price,Description,Title,Place;
    ImageSlider Image;
    Toolbar toolbar;
    int count;
    List<SlideModel> slideModels = new ArrayList<>();
    Button btn_showMap,share,report;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment_details);

        Image = (ImageSlider) findViewById(R.id.img_apt_details);
        Price= (TextView) findViewById(R.id.txt_view_price);
        Description= (TextView) findViewById(R.id.txt_view_description);
        Title = (TextView) findViewById(R.id.txt_view_title);
        Place = (TextView) findViewById(R.id.txt_view_place);
        share = (Button) findViewById(R.id.btn_share_renter);
        btn_showMap = (Button) findViewById(R.id.btn_renter_apartment_details);


        toolbar = findViewById(R.id.renter_apartment_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ApartmentDetails.this, RenterHomePage.class));
                finish();
            }
        });

        Intent intent = getIntent();
       // Picasso.get().load(intent.getStringExtra("Image")).into(Image);
        String docID = intent.getExtras().getString("Documentid");
        String price = intent.getExtras().getString("Price");
        String title = intent.getExtras().getString("Title");
        String place = intent.getExtras().getString("Place");
        String description = intent.getExtras().getString("Description");
        Price.setText(price);
        Title.setText(title);
        Place.setText(place);
        Description.setText(description);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ApartmentImages").
                document(docID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                count = Integer.valueOf(documentSnapshot.getString("count"));
                for (int i=0; i<count;i++){
                    slideModels.add(new SlideModel(documentSnapshot.getString("image"+i)));
                }
                Image.setImageList(slideModels,true);

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,"Check out this place at the price "
                        + price + "\n located in " + place);

                startActivity(Intent.createChooser(shareIntent,"Share to "));

            }
        });


        btn_showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApartmentDetails.this,MapsActivity.class);
                intent.putExtra("Address",title+" "+place);
                startActivity(intent);
            }
        });



    }
}