package com.example.rentalapp.tenantfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.rentalapp.ApartmentDetails;
import com.example.rentalapp.ApartmentEditDetails;
import com.example.rentalapp.Model;
import com.example.rentalapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MyadsFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userID;
    List<SlideModel> slideModels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myads, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_view3);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();




        Query query = firebaseFirestore.collection("Myads").document(userID).collection("Selected");

        FirestoreRecyclerOptions<Model> options = new FirestoreRecyclerOptions.Builder<Model>()
                .setQuery(query,Model.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Model, ItemViewHolder>(options) {
            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.renter_myad_recycler_view,parent,false);
                return new ItemViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Model model) {
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ApartmentImages").
                        document(model.getDocumentid());
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Picasso.get().load(documentSnapshot.getString("image0")).into(holder.Image);
                    }
                });
                holder.Name.setText(model.getStreetname());
                holder.Price.setText(String.valueOf(model.getPrice()));
                holder.Description.setText(model.getDescription());
                holder.Place.setText(model.getPlace());
                String docid = model.getDocumentid();

                holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.edit){
                            Intent intent = new Intent(getContext(), ApartmentEditDetails.class);
                            intent.putExtra("Id",model.getDocumentid());
                            intent.putExtra("Price",String.valueOf(model.getPrice()));
                            intent.putExtra("Title",model.getStreetname());
                            intent.putExtra("Place",model.getPlace());
                            intent.putExtra("Description",model.getDescription());
                            intent.putExtra("Type", model.getType());
                            startActivity(intent);
                        }
                        if (id == R.id.delete){

                            DocumentReference eRef = db.collection("Myads").document(userID).collection("Selected").document(docid);
                            eRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            DocumentReference bRef = db.collection("Apartments").document(docid);
                            bRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        }
                        return false;
                    }
                });
                holder.Image.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        Intent intent = new Intent(getContext(), ApartmentEditDetails.class);
                        intent.putExtra("Id",model.getDocumentid());
                        intent.putExtra("Price",String.valueOf(model.getPrice()));
                        intent.putExtra("Title",model.getStreetname());
                        intent.putExtra("Place",model.getPlace());
                        intent.putExtra("Description",model.getDescription());
                        intent.putExtra("Type", model.getType());
                        startActivity(intent);
                    }
                });
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        Intent intent = new Intent(getContext(), ApartmentEditDetails.class);
                        intent.putExtra("Id",model.getDocumentid());
                        intent.putExtra("Price",String.valueOf(model.getPrice()));
                        intent.putExtra("Title",model.getStreetname());
                        intent.putExtra("Place",model.getPlace());
                        intent.putExtra("Description",model.getDescription());
                        intent.putExtra("Type", model.getType());
                        intent.putExtra("classid","renter");
                        startActivity(intent);
                    }
                });

            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(adapter);

        return view;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView Price;
        TextView Name;
        TextView Description;
        TextView Place;
        ImageView Image;
        String id;
        Toolbar toolbar;
        CardView cardView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Price = itemView.findViewById(R.id.txt_pricemyads);
            Name = itemView.findViewById(R.id.txt_titlemyads);
            Place = itemView.findViewById(R.id.txt_placemyads);
            Description = itemView.findViewById(R.id.txt_descriptionmyads);
            toolbar = itemView.findViewById(R.id.toolbar_myads);
            Image = itemView.findViewById(R.id.Img_renter_apartment);
            cardView = itemView.findViewById(R.id.cardview_renter_myad);
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
