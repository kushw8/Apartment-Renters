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
import android.widget.ListView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class UserReportsChatsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    RelativeLayout relativeLayout;
    Toolbar toolbar;
    RecyclerView chatRecyclerView;
    MessageAdapter chatRecyclerAdapter;
    Button fab;
    EditText edtmsg;
    TextView textView,chatroomtitle;
    ListView listView;
    String tenantid,rentername,tenantname,title,renterid,chatid,adminid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reports_chats);

        fab = findViewById(R.id.fab5);
        edtmsg = findViewById(R.id.input5);
        chatRecyclerView = findViewById(R.id.list_of_messages5);
        relativeLayout = findViewById(R.id.activity_main5);
        textView = findViewById(R.id.txt_chat_user_report_title);
        chatroomtitle = findViewById(R.id.toolbar_chat_user_report_title);

        Intent intent = getIntent();
        tenantid = intent.getExtras().getString("tenantid");
        title = intent.getExtras().getString("title");
        renterid = intent.getExtras().getString("renterid");
        chatid = intent.getExtras().getString("chatid");
        rentername = intent.getExtras().getString("rentername");
        tenantname = intent.getExtras().getString("tenantname");
        adminid = intent.getExtras().getString("adminid");

        displayChatMessages();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(tenantid)){
                    String message = edtmsg.getText().toString();

                    if(!message.equals("")){
                        ChatMessage chat = new ChatMessage(edtmsg.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),adminid,new Date().getTime(),"1","0",tenantname,"admin");



                        db.collection("Adminchatroom").document(chatid).collection("chats")
                                .add(chat)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("SUCCESS", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        edtmsg.setText("");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("FAILED", "Error adding document", e);
                                    }
                                });}


                }
                else if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(adminid)){
                    String message = edtmsg.getText().toString();

                    if(!message.equals("")){ChatMessage chat = new ChatMessage(edtmsg.getText().toString(),
                            adminid,tenantid,new Date().getTime(),"0","1",tenantname,"admin");



                        db.collection("Adminchatroom").document(chatid).collection("chats")
                                .add(chat)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("SUCCESS", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        edtmsg.setText("");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("FAILED", "Error adding document", e);
                                    }
                                });
                    }

                }


            }
        });

    }

    private void displayChatMessages() {

        Query query = FirebaseFirestore.getInstance().collection("Adminchatroom").document(chatid)
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
                chatRecyclerAdapter = new MessageAdapter(UserReportsChatsActivity.this,chats);
                chatRecyclerView.setAdapter(chatRecyclerAdapter);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}