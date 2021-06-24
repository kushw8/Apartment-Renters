package com.example.rentalapp.tenantfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.rentalapp.ApartmentDetails;
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


public class AllAdsFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    List<SlideModel> slideModels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_allads, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("Apartments");

        FirestoreRecyclerOptions<Model> options = new FirestoreRecyclerOptions.Builder<Model>()
                .setQuery(query,Model.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Model, ItemViewHolder>(options) {
            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewlist,parent,false);
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



                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), ApartmentDetails.class);
                        intent.putExtra("Documentid",model.getDocumentid());
                        intent.putExtra("Price",String.valueOf(model.getPrice()));
                        intent.putExtra("Title",model.getStreetname());
                        intent.putExtra("Place",model.getPlace());
                        intent.putExtra("Description",model.getDescription());
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
        CardView cardView;
    //    String id;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Price = itemView.findViewById(R.id.txt_price);
            Image = itemView.findViewById(R.id.Img_apartment);
            Name = itemView.findViewById(R.id.txt_title);
            Place = itemView.findViewById(R.id.txt_place);
            Description = itemView.findViewById(R.id.txt_description);
            cardView = itemView.findViewById(R.id.cardViewallads);

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
