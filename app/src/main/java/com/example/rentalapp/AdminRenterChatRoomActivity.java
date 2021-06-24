package com.example.rentalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

public class AdminRenterChatRoomActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    RelativeLayout relativeLayout;
    RecyclerView chatRecyclerView;
    AdminMessageAdapter chatRecyclerAdapter;
    Toolbar toolbar;
    TextView chatroomtitle;
String tenantid,documentid,renter,chatid,chatroomname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_renter_chat_room);
        toolbar = findViewById(R.id.admin_renter_chatroom_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminRenterChatRoomActivity.this,AdminRenterChtatroom.class));
                finish();
            }
        });
        chatRecyclerView = findViewById(R.id.list_of_messages2);
        relativeLayout = findViewById(R.id.activity_main2);
        chatroomtitle = findViewById(R.id.txt_chatactivity_title2);
        Intent intent = getIntent();
        tenantid = intent.getExtras().getString("tenantid");
        documentid = intent.getExtras().getString("documentid");
        renter = intent.getExtras().getString("renter");
        chatid = intent.getExtras().getString("chatid");
        chatroomname = intent.getExtras().getString("chatroomname");
        chatroomtitle.setText(chatroomname);
        displayChatMessages();


    }

    private void displayChatMessages() {
        Query query = FirebaseFirestore.getInstance().collection("Chatroom").document(chatid)
                .collection("chats")
                .orderBy("messageTime")
                .limit(20);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    //...
                    return;
                }

                // Convert query snapshot to a list of chats
                List<ChatMessage> chats = snapshot.toObjects(ChatMessage.class);

                // Update UI
                chatRecyclerView.setHasFixedSize(true);
                chatRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                chatRecyclerAdapter = new AdminMessageAdapter(AdminRenterChatRoomActivity.this,chats);
                chatRecyclerView.setAdapter(chatRecyclerAdapter);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}