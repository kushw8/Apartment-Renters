package com.example.rentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ViewUserReports extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String rentername,tenantname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_reports);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewviewuserreports);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("Userreports");
        FirestoreRecyclerOptions<ViewReportsModel> options = new FirestoreRecyclerOptions.Builder<ViewReportsModel>()
                .setQuery(query,ViewReportsModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<ViewReportsModel, ItemViewHolder>(options) {
            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewuserreports,parent,false);
                return new ItemViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull ViewReportsModel model) {

                holder.date.setText("Date: " +model.getDate());
                holder.name.setText("UersID: " +model.getUserid());
                holder.title.setText("Title: " +model.getTitle());
                holder.descritpion.setText("Description: " +model.getDescription());
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentReference documentReference4 = FirebaseFirestore.getInstance().collection("Renter").
                                document(model.getRenterid());
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



                        DocumentReference documentReference2 = FirebaseFirestore.getInstance().collection("Adminchatroom").
                                document(model.getTitle());
                        documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult().exists()){
                                    Intent intent = new Intent(ViewUserReports.this, UserReportsChatsActivity.class);
                                    intent.putExtra("tenantid",model.getUserid());
                                    intent.putExtra("title",model.getTitle());
                                    intent.putExtra("renterid",model.getRenterid());
                                    intent.putExtra("rentername",rentername);
                                    intent.putExtra("tenantname",tenantname);
                                    intent.putExtra("admnid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    intent.putExtra("chatid",model.getTitle());
                                    startActivity(intent);
                                }
                                else{
                                    DocumentReference documentReference3 = FirebaseFirestore.getInstance().collection("Adminchatroom").
                                            document(model.getTitle());
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("tenantid",model.getUserid());
                                    userInfo.put("title",model.getTitle());
                                    userInfo.put("chatid",model.getTitle());
                                    userInfo.put("renterid",model.getRenterid());
                                    userInfo.put("rentername",rentername);
                                    userInfo.put("tenantname",tenantname);
                                    userInfo.put("adminid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    documentReference3.set(userInfo);
                                    Intent intent = new Intent(ViewUserReports.this, UserReportsChatsActivity.class);
                                    intent.putExtra("tenantid",model.getUserid());
                                    intent.putExtra("title",model.getTitle());
                                    intent.putExtra("renterid",model.getRenterid());
                                    intent.putExtra("rentername",rentername);
                                    intent.putExtra("tenantname",tenantname);

                                    intent.putExtra("admnid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    intent.putExtra("chatid",model.getTitle());
                                    startActivity(intent);

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        recyclerView.setAdapter(adapter);

    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView name;
        TextView title;
        TextView descritpion;
        Button button;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txt_view_user_reports_date);
            name = itemView.findViewById(R.id.txt_view_user_reports_username);
            title = itemView.findViewById(R.id.txt_view_user_reports_title);
            descritpion = itemView.findViewById(R.id.txt_view_user_reports_description);
            button = itemView.findViewById(R.id.btn_view_user_chat);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
