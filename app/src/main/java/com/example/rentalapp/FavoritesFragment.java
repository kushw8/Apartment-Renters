package com.example.rentalapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FavoritesFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;
    String userID,rentername,tenantname;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    List<SlideModel> slideModels = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  v = inflater.inflate(R.layout.fragment_favorites, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.recyclerView3);
        db = FirebaseFirestore.getInstance();
        mAuth       = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        Query query = firebaseFirestore.collection("Favorites").document(userID).
                collection("Selected");
        fAuth = FirebaseAuth.getInstance();
        FirestoreRecyclerOptions<Model> options = new FirestoreRecyclerOptions.Builder<Model>()
                .setQuery(query,Model.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Model, ItemViewHolder>(options) {
            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_recyclerviewlist,parent,false);
                return new ItemViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Model model) {
                holder.Favor.setBackgroundResource(R.drawable.ic_baseline_favorite_24);

                    holder.Title.setText(model.getStreetname());
                    holder.Price.setText(String.valueOf(model.getPrice())+" CAD");
                    holder.Description.setText(model.getDescription());

                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ApartmentImages").
                        document(model.getDocumentid());
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Picasso.get().load(documentSnapshot.getString("image0")).into(holder.Image);
                    }
                });

                    holder.Favor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            holder.Favor.setBackgroundResource(R.drawable.ic_baseline_favorite_shadow_24);
                            DocumentReference eRef = db.collection("Favorites").document(userID).collection("Selected")
                                    .document(model.getDocumentid());
                            eRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(), "removed", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                    holder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(),TenantApartmentDetails.class);

                            intent.putExtra("Price",model.getPrice());
                            intent.putExtra("Title",model.getStreetname());
                            intent.putExtra("UserID",userID);
                            intent.putExtra("Place",model.getPlace());
                            intent.putExtra("DocumentID",model.getDocumentid());
                            intent.putExtra("Description",model.getDescription());
                            startActivity(intent);
                        }
                    });

                    holder.message.setOnClickListener(new View.OnClickListener() {
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



                            DocumentReference documentReference2 = FirebaseFirestore.getInstance().collection("Chatroom").
                                    document(FirebaseAuth.getInstance().getCurrentUser().getUid()+model.getDocumentid());
                            documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.getResult().exists()){
                                        Intent intent = new Intent(getActivity(), ChatroomActivity.class);
                                        intent.putExtra("tenantid",FirebaseAuth.getInstance().getUid());
                                        intent.putExtra("documentid",model.getDocumentid());
                                        intent.putExtra("renter",model.getRenterid());
                                        intent.putExtra("chatid",FirebaseAuth.getInstance().getCurrentUser().getUid()+model.getDocumentid());
                                        intent.putExtra("chatroomname",model.getStreetname());
                                        intent.putExtra("isuser","2");
                                        startActivity(intent);
                                    }
                                    else{
                                        DocumentReference documentReference3 = FirebaseFirestore.getInstance().collection("Chatroom").
                                                document(FirebaseAuth.getInstance().getCurrentUser().getUid()+model.getDocumentid());
                                        Map<String, Object> userInfo = new HashMap<>();
                                        userInfo.put("tenantid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        userInfo.put("documentid", model.getDocumentid());
                                        userInfo.put("renterid", model.getRenterid());
                                        userInfo.put("chatid", FirebaseAuth.getInstance().getCurrentUser().getUid()+model.getDocumentid());
                                        userInfo.put("chatroomname", model.getStreetname());
                                        userInfo.put("rentername", rentername);
                                        userInfo.put("tenantname", tenantname);
                                        documentReference3.set(userInfo);
                                        Intent intent = new Intent(getActivity(), ChatroomActivity.class);
                                        intent.putExtra("tenantid",FirebaseAuth.getInstance().getUid());
                                        intent.putExtra("documentid",model.getDocumentid());
                                        intent.putExtra("renter",model.getRenterid());
                                        intent.putExtra("chatid",FirebaseAuth.getInstance().getCurrentUser().getUid()+model.getDocumentid());
                                        intent.putExtra("chatroomname",model.getStreetname());
                                        intent.putExtra("rentername",rentername);
                                        intent.putExtra("tenantname",tenantname);
                                        intent.putExtra("isuser","2");
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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(adapter);


        return v;

    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView Price;
        TextView Title;
        TextView Description;
        ImageView Image;
        TextView Place;
        Button Favor,message;
        CardView cardView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Price = itemView.findViewById(R.id.txt_tenant_price);
            Image = itemView.findViewById(R.id.Img_tenant_apartment);
            Place = itemView.findViewById(R.id.txt_tenant_place);
            Title = itemView.findViewById(R.id.txt_tenant_title);
            Description=itemView.findViewById(R.id.txt_tenant_description);
            Favor = itemView.findViewById(R.id.btn_tenant_favor2);
            cardView = itemView.findViewById(R.id.card_view_tenant_recyclerview);
            message = itemView.findViewById(R.id.btn_tenant_message2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();


        userID= mAuth.getCurrentUser().getUid();


        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}