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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatroomActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    RelativeLayout relativeLayout;
    Toolbar toolbar;
    RecyclerView chatRecyclerView;
    MessageAdapter chatRecyclerAdapter;
    Button fab;
    EditText edtmsg;
    TextView textView,chatroomtitle;
    RequestQueue mQueue;
    ListView listView;
    public static boolean isOpen = true;
    public static final String URL = "https://fcm.googleapis.com/fcm/send";
    String tenantid,documentid,renter,chatid,chatroomname,rentername,tenantname,isuser,activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        toolbar = findViewById(R.id.chatroom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        isOpen = true;
        fab = findViewById(R.id.fab1);
        edtmsg = findViewById(R.id.input1);
        chatRecyclerView = findViewById(R.id.list_of_messages1);
        relativeLayout = findViewById(R.id.activity_main1);
        textView = findViewById(R.id.txt_chatactivity_title);
        chatroomtitle = findViewById(R.id.toolbar_chatroom_title);
        mQueue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        tenantid = intent.getExtras().getString("tenantid");
        documentid = intent.getExtras().getString("documentid");
        renter = intent.getExtras().getString("renter");
        chatid = intent.getExtras().getString("chatid");
        rentername = intent.getExtras().getString("rentername");
        tenantname = intent.getExtras().getString("tenantname");
        chatroomname = intent.getExtras().getString("chatroomname");
        isuser = intent.getExtras().getString("isuser");
        activity = intent.getExtras().getString("activity");
        if(isuser.equals("2")){
            chatroomtitle.setText(rentername);
        }
        else if(isuser.equals("1")){
            chatroomtitle.setText(tenantname);
        }
        textView.setText(chatroomname);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.equals("third")){
                    startActivity(new Intent(ChatroomActivity.this,RenterHomePage.class));
                    finish();
                }
                else if(activity.equals("alerts")){
                    startActivity(new Intent(ChatroomActivity.this,TenantHomePage.class));
                    finish();
                }

            }
        });

        displayChatMessages();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(tenantid)){
                    String message = edtmsg.getText().toString();

                    if(!message.equals("")){
                        ChatMessage chat = new ChatMessage(edtmsg.getText().toString(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),renter,new Date().getTime(),"1","0",tenantname,rentername);



                        db.collection("Chatroom").document(chatid).collection("chats")
                                .add(chat)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        JSONObject object = new JSONObject();
                                        JSONObject innerObject = new JSONObject();
                                        try {
                                            innerObject.put("user_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            innerObject.put("user",tenantname);
                                            innerObject.put("message",message);
                                            innerObject.put("sender_fcm",tenantid);
                                            object.put("data",innerObject);
                                            object.put("to",renter);
                                            sendPushNotification(object);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
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
                else if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(renter)){
                    String message = edtmsg.getText().toString();

                    if(!message.equals("")){ChatMessage chat = new ChatMessage(edtmsg.getText().toString(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),tenantid,new Date().getTime(),"0","1",tenantname,rentername);



                        db.collection("Chatroom").document(chatid).collection("chats")
                                .add(chat)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        JSONObject object = new JSONObject();
                                        JSONObject innerObject = new JSONObject();
                                        try {
                                            innerObject.put("user_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            innerObject.put("user",rentername);
                                            innerObject.put("message",message);
                                            innerObject.put("sender_fcm",renter);
                                            object.put("data",innerObject);
                                            object.put("to",tenantid);
                                            sendPushNotification(object);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
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
                chatRecyclerAdapter = new MessageAdapter(ChatroomActivity.this,chats);
                chatRecyclerView.setAdapter(chatRecyclerAdapter);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void sendPushNotification(JSONObject object) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, URL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String ,String> map = new HashMap<>();
                map.put("authorization","key=AAAAvxHvdOY:APA91bG_TOL-Z-zpqeYErf4mdTjdN1nolEhLGQKswLC6oTP_myz-YgaMUnv-83RG8lN8uNoMGGXs42O-r-_WgLdXqabu71wbyG2uyAAb278Kh8rBgj3phPXtp1L3ataTOYOSMdRq9O_v");
                map.put("Content-Type","application/json");
                return map;
            }
        };
        mQueue.add(objectRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpen = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOpen = false;
    }

}