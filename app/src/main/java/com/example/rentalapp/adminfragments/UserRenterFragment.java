package com.example.rentalapp.adminfragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentalapp.ApartmentEditDetails;
import com.example.rentalapp.Model;
import com.example.rentalapp.R;
import com.example.rentalapp.User;
import com.example.rentalapp.tenantfragments.MyadsFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;


public class UserRenterFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_renter, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.recyclerView7);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Query query = firebaseFirestore.collection("Renter");

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<User, ItemViewHolder>(options) {
            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenantrenterrecyclerview,parent,false);
                return new ItemViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull User model) {
                holder.fName.setText("First Name: " + model.getFirstname());
                holder.sName.setText("Last Name: " + model.getSecondname());
                holder.ids.setText("Id: " + model.getUserid());
                holder.Email.setText("Email: " + model.getEmail());
                holder.Phone.setText("Phone: " + model.getPhonenumber());

                holder.toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //  String id = model.getUserid();
                        db.collection("Renter").document(model.getUserid())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(adapter);

        return v;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView Email;
        TextView fName, sName;
        TextView Phone;
        TextView ids;
      //  String id;
        Toolbar toolbar;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Email = itemView.findViewById(R.id.txt_adminusersemail);
            fName = itemView.findViewById(R.id.txt_adminusersfname);
            sName = itemView.findViewById(R.id.txt_adminuserssname);
            Phone = itemView.findViewById(R.id.txt_adminusersphone);
            ids = itemView.findViewById(R.id.txt_adminusersid);
            toolbar = itemView.findViewById(R.id.toolbar_adminusers);
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