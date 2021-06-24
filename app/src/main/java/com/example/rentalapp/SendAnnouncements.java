package com.example.rentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SendAnnouncements extends AppCompatActivity {
    EditText title,description;
    Button send;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_announcements);

        title = findViewById(R.id.edt_txt_title_announcement);
        description = findViewById(R.id.edt_txt_description_announcement);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        send = findViewById(R.id.btn_send_announcment);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titled = title.getText().toString().trim();
                String descriptiond = description.getText().toString().trim();
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String id = UUID.randomUUID().toString();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MM,dd,yyyy");
                String saveCurrentDate = currentDate.format(calendar.getTime());
                Map<String, Object> doc = new HashMap<>();
                doc.put("id", id);
                doc.put("title", titled);
                doc.put("date",saveCurrentDate);
                doc.put("description", descriptiond);

                db.collection("Announcement").document(id).set(doc)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(SendAnnouncements.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });


    }
}