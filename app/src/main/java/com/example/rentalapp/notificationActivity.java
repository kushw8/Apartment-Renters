package com.example.rentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentalapp.tenantfragments.MyadsFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class notificationActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    Toolbar toolbar;
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String classid = intent.getExtras().getString("classid");
        recyclerView = findViewById(R.id.recyclerView_notification);
        toolbar = findViewById(R.id.toolbar_tenant_notification);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!classid.equals("renter")){startActivity(new Intent(notificationActivity.this,TenantHomePage.class));
                    finish();}
                else{startActivity(new Intent(notificationActivity.this,RenterHomePage.class));
                    finish();}

            }
        });


        Query query = firebaseFirestore.collection("Announcement");

        FirestoreRecyclerOptions<Alerts> options = new FirestoreRecyclerOptions.Builder<Alerts>()
                .setQuery(query,Alerts.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Alerts, ItemViewHolder>(options) {
            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alertsrecyclerview,parent,false);
                return new ItemViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Alerts model) {

                holder.date.setText(model.getDate());
                holder.title.setText(String.valueOf(model.getTitle()));
                holder.description.setText(model.getDescription());

            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        recyclerView.setAdapter(adapter);

    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView date,title,description;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txt_alerts_date_recycler);
            title = itemView.findViewById(R.id.txt_alerts_title_recycler);
            description = itemView.findViewById(R.id.txt_alerts_description_recycler);
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
