package com.example.rentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TenantApartmentDetails extends AppCompatActivity {
    private TextView Price,Description,Title,Place;
    ImageSlider Image;
    Button btn_showMap,share,report;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    public String lang = "en";
    int count;
    String currentLanguage = Locale.getDefault().getLanguage();
    List<SlideModel> slideModels = new ArrayList<>();
    Toolbar toolbar;
    String rentername,tenantname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_apartment_details);

        Image = (ImageSlider) findViewById(R.id.img_apt_details_tenant);

        Price= (TextView) findViewById(R.id.txt_view_price_tenant);
        Description= (TextView) findViewById(R.id.txt_view_description_tenant);
        Title = (TextView) findViewById(R.id.txt_view_title_tenant);
        Place = (TextView) findViewById(R.id.txt_view_place_tenant);
        btn_showMap = (Button) findViewById(R.id.btn_tenant_apartment_details);
        share = (Button) findViewById(R.id.btn_share);
        report = (Button) findViewById(R.id.btn_report);
        fAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.tenant_apartment_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TenantApartmentDetails.this,TenantHomePage.class));
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String price = intent.getExtras().getString("Price");
        String title = intent.getExtras().getString("Title");
        String place = intent.getExtras().getString("Place");
        String id = intent.getExtras().getString("DocumentID");
        String description = intent.getExtras().getString("Description");
        String renterid = intent.getExtras().getString("RenterID");


        DocumentReference documentReference4 = FirebaseFirestore.getInstance().collection("Renter").
                document(renterid);
        documentReference4.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String firstname = documentSnapshot.getString("firstname");
                String secondname = documentSnapshot.getString("secondname");
                rentername = firstname+ " " +secondname;

            }
        });

        DocumentReference documentReference5 = FirebaseFirestore.getInstance().collection("Tenant").
                document(fAuth.getCurrentUser().getUid());
        documentReference5.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String firstname = documentSnapshot.getString("firstname");
                String secondname = documentSnapshot.getString("secondname");
                tenantname = firstname+ " " +secondname;

            }
        });

        Price.setText(price);
        Title.setText(title);
        Place.setText(place);
        Description.setText(description);
        if (!currentLanguage.toLowerCase().contains("English")) {
            trans(description);
        }

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ApartmentImages").
                document(id);
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



        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TenantApartmentDetails.this);
                builder.setTitle("Submit your report");

                final EditText input1 = new EditText(TenantApartmentDetails.this);
                input1.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input1);



                builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!input1.getText().toString().equals("")){
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("MM,dd,yyyy");
                            String saveCurrentDate = currentDate.format(calendar.getTime());
                            Map<String, Object> doc = new HashMap<>();
                            doc.put("title", FirebaseAuth.getInstance().getCurrentUser().getUid()+title);
                            doc.put("date",saveCurrentDate);
                            doc.put("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                            doc.put("tenantname",tenantname);
                            doc.put("renterid",renterid);
                            doc.put("rentername",rentername);
                            doc.put("description", input1.getText().toString());

                            db.collection("Userreports").document(FirebaseAuth.getInstance().getCurrentUser().getUid()+title)
                                    .set(doc)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(TenantApartmentDetails.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(TenantApartmentDetails.this, "ADDRESS YOUR REPORT", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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
                Intent intent = new Intent(TenantApartmentDetails.this,MapsActivity.class);
                intent.putExtra("Address",title+" "+place);
                startActivity(intent);
            }
        });

    }
    private void trans(String description) {
        String des = description;
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.FRENCH)
                        .build();
        final Translator englishFrenchTranslator =
                Translation.getClient(options);


        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        englishFrenchTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        englishFrenchTranslator.translate(des).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Description.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbarmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_bar_EN:
                translate("en");
                lang = "en";
                recreate();
                break;
            case R.id.action_bar_FR:
                translate("fr");
                lang = "fr";
                recreate();
                break;
            case R.id.action_bar_About_US:
                startActivity(new Intent(TenantApartmentDetails.this,ContactUs.class));

                break;
            case R.id.action_bar_About_Signout:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void translate(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();

    }

    private void loadlocale(){

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        translate(language);
    }

    public String language(){
        String langs = lang;
        return langs;
    }

}