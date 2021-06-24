package com.example.rentalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText lEmail, lPassword;
    TextView lForgotPassword, English,French;
    FirebaseFirestore lStore;
    FirebaseAuth lAuth,mAuth;
    Button lLogin,lRegister, lAdminLogin;
    Boolean valid = true;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadlocale();
        setContentView(R.layout.activity_main);

        lEmail = findViewById(R.id.editTxt_Email);
        lPassword = findViewById(R.id.editTxt_Password);
        lLogin = findViewById(R.id.btn_login);
        lRegister = findViewById(R.id.btn_register);
        lForgotPassword = findViewById(R.id.txt_ForgotPassword);
        English = findViewById(R.id.txt_english);
        French = findViewById(R.id.txt_french);
        lStore = FirebaseFirestore.getInstance();
        lAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        English.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate("en");
                recreate();
            }
        });

        French.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate("fr");
                recreate();

            }
        });




        /**
         * take back to register page
         */

        lRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();

            }
        });

        /**
         * performing login activity
         */

        //if(valid){


            lLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkField(lEmail);
                    checkField(lPassword);

                    if (valid) {
                        lAuth.signInWithEmailAndPassword(lEmail.getText().toString(), lPassword.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        checkAccessLevel(authResult.getUser().getUid());
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", "Error Login " + e.getMessage());
                                Toast.makeText(MainActivity.this, "fail login", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                }
            });

     //   }


        lForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText restmail = new EditText(v.getContext());
                 AlertDialog.Builder passwordRestDialog = new AlertDialog.Builder(v.getContext());
                passwordRestDialog.setTitle("Reset Password");
                passwordRestDialog.setMessage("Enter your email to receive reset link" );
                passwordRestDialog.setView(restmail);

                passwordRestDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String mail = restmail.getText().toString();

                        lAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(MainActivity.this, "Link is sent to your email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(MainActivity.this, "Error!..Link is not sent", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });

                passwordRestDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordRestDialog.create().show();
            }
        });



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

    /**
     * Checking access levels for users
     * @param uid String
     */

    private void checkAccessLevel(String uid) {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Renter").
                document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    startActivity(new Intent(getApplicationContext(),RenterHomePage.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("Renter","fail " + e.getMessage());

            }
        });

        DocumentReference documentReference2 = FirebaseFirestore.getInstance().collection("Tenant").
                document(uid);
        documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    startActivity(new Intent(getApplicationContext(),TenantHomePage.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("Tenant","fail " + e.getMessage());

            }
        });

        DocumentReference documentReference3 = FirebaseFirestore.getInstance().collection("Admin").
                document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    startActivity(new Intent(MainActivity.this, AdminHomePage.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("Admin","fail " + e.getMessage());

            }
        });

    }


    /**
     * Checking text field is not empty
     * @param TextField
     * @return Boolean
     */

    private Boolean checkField(EditText TextField) {

        if(TextField.getText().toString().isEmpty()){
            TextField.setError("Error");
            valid = false;
        }else{
            valid = true;
        }
        return valid;

    }

    /**
     * Checking user previously logged-n or not
     */
    @Override
    protected void onStart() {

        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){

            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Renter").
                    document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists()){
                        startActivity(new Intent(getApplicationContext(),RenterHomePage.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            DocumentReference documentReference2 = FirebaseFirestore.getInstance().collection("Tenant").
                    document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists()){
                        startActivity(new Intent(getApplicationContext(),TenantHomePage.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            DocumentReference documentReference3 = FirebaseFirestore.getInstance().collection("Admin").
                    document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            documentReference3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists()){
                        startActivity(new Intent(MainActivity.this, AdminHomePage.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });



        }
    }

}