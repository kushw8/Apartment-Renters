package com.example.rentalapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.rentalapp.TenantHomePage;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;


public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    private BottomSheetBehavior bottomSheetBehavior;
    FirebaseAuth fAuth;
    FloatingActionButton filter;
    String userID, searchview,rentername,tenantname;
    FirebaseFirestore db,db2;
    SearchView searchView;
    Boolean isClicked = false;
    private FirebaseFirestore mDb;
    FirestoreRecyclerOptions<Model> options;
    private GoogleMap mMap;
    MapView mapView;
    String address;
    String tenantHomePage;
    List<SlideModel> slideModels = new ArrayList<>();
    ArrayList<String> markersArray = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.home_map);
        mapFragment.getMapAsync(this);
        tenantHomePage = new TenantHomePage().language();
        mDb = FirebaseFirestore.getInstance();
        Button setlimit = v.findViewById(R.id.btn_set_limit);
        Button set = v.findViewById(R.id.btn_set);
        CardView setlimitcardview = v.findViewById(R.id.card_view_set_limit);
        EditText setlimit1 = v.findViewById(R.id.edt_setlimit0);
        EditText setlimit3000 = v.findViewById(R.id.edt_setlimit3000);
        setlimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setlimitcardview.setVisibility(View.VISIBLE);
            }


        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String l1 = setlimit1.getText().toString().trim();
                String l2 =setlimit3000.getText().toString().trim();


                Query query = firebaseFirestore.collection("Apartments").whereGreaterThan("price",Integer.valueOf(l1)).whereLessThan("price",Integer.valueOf(l2));
                options = new FirestoreRecyclerOptions.Builder<Model>()
                        .setQuery(query,Model.class)
                        .build();

                adapter = new FirestoreRecyclerAdapter<Model,ItemViewHolder>(options) {
                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_recyclerviewlist,parent,false);
                        return new ItemViewHolder(v);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Model model) {

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


                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ApartmentImages").
                                document(model.getDocumentid());
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                Picasso.get().load(documentSnapshot.getString("image0")).into(holder.imageSlider);
                            }
                        });


                        holder.Title.setText(model.getStreetname());
                        holder.Price.setText(String.valueOf(model.getPrice())+" CAD");
                        holder.Description.setText(model.getDescription());

                        holder.message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });


                        holder.Favor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                db.collection("Favorites").document(userID).collection("Selected").
                                        document(model.getDocumentid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot result = task.getResult();
                                            if(!result.exists()){
                                                holder.Favor.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                                                Map<String, Object> doc = new HashMap<>();
                                                doc.put("documentid", model.getDocumentid());
                                                doc.put("email", model.getEmail());
                                                doc.put("streetname", model.getStreetname());
                                                doc.put("price", String.valueOf(model.getPrice()));
                                                doc.put("place", model.getPlace());
                                                doc.put("description", model.getDescription());
                                                doc.put("tenantid", userID);
                                                doc.put("type", model.getType());
                                                doc.put("renterid", model.getRenterid());


                                                db.collection("Favorites").document(userID).collection("Selected").
                                                        document(model.getDocumentid()).set(doc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else{
                                                holder.Favor.setBackgroundResource(R.drawable.ic_baseline_favorite_shadow_24);
                                                DocumentReference eRef = db.collection("Favorites").document(userID).
                                                        collection("Selected")
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
                                        }
                                    }
                                });


                            }
                        });

                        holder.cardView.setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(),TenantApartmentDetails.class);
                                intent.putExtra("Price",String.valueOf(model.getPrice()));
                                intent.putExtra("Title",model.getStreetname());
                                intent.putExtra("UserID",userID);
                                intent.putExtra("Place",model.getPlace());
                                intent.putExtra("DocumentID",model.getDocumentid());
                                intent.putExtra("Description",model.getDescription());
                                intent.putExtra("RenterID",model.getRenterid());
                                startActivity(intent);
                            }
                        });

                    }
                };

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
                recyclerView.setAdapter(adapter);
                onStart();


            }
        });
        Toolbar toolbar = v.findViewById(R.id.toolbar_price_condition);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.price_ascending){
                    Query query = firebaseFirestore.collection("Apartments").orderBy("price", Query.Direction.ASCENDING);
                    options = new FirestoreRecyclerOptions.Builder<Model>()
                            .setQuery(query,Model.class)
                            .build();

                    adapter = new FirestoreRecyclerAdapter<Model,ItemViewHolder>(options) {
                        @NonNull
                        @Override
                        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_recyclerviewlist,parent,false);
                            return new ItemViewHolder(v);
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Model model) {


                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ApartmentImages").
                                    document(model.getDocumentid());
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    Picasso.get().load(documentSnapshot.getString("image0")).into(holder.imageSlider);
                                }
                            });

                            holder.Title.setText(model.getStreetname());
                            holder.Price.setText(String.valueOf(model.getPrice())+" CAD");
                            holder.Description.setText(model.getDescription());

                            holder.message.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });


                            holder.Favor.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    db.collection("Favorites").document(userID).collection("Selected").
                                            document(model.getDocumentid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot result = task.getResult();
                                                if(!result.exists()){
                                                    holder.Favor.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                                                    Map<String, Object> doc = new HashMap<>();
                                                    doc.put("documentid", model.getDocumentid());
                                                    doc.put("email", model.getEmail());
                                                    doc.put("streetname", model.getStreetname());
                                                    doc.put("price", model.getPrice());
                                                    doc.put("place", model.getPlace());
                                                    doc.put("description", model.getDescription());
                                                    doc.put("tenantid", userID);
                                                    doc.put("type", model.getType());
                                                    doc.put("renterid", model.getRenterid());

                                                    db.collection("Favorites").document(userID).collection("Selected").
                                                            document(model.getDocumentid()).set(doc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                else{
                                                    holder.Favor.setBackgroundResource(R.drawable.ic_baseline_favorite_shadow_24);
                                                    DocumentReference eRef = db.collection("Favorites").document(userID).
                                                            collection("Selected")
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
                                            }
                                        }
                                    });


                                }
                            });

                            holder.cardView.setOnClickListener(new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    Intent intent = new Intent(view.getContext(),TenantApartmentDetails.class);
                                    intent.putExtra("DocumentID",model.getDocumentid());
                                    intent.putExtra("Price",String.valueOf(model.getPrice()));
                                    intent.putExtra("Title",model.getStreetname());
                                    intent.putExtra("UserID",userID);
                                    intent.putExtra("Place",model.getPlace());
                                    intent.putExtra("Description",model.getDescription());
                                    intent.putExtra("RenterID",model.getRenterid());
                                    startActivity(intent);
                                }
                            });

                        }
                    };

                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
                    recyclerView.setAdapter(adapter);
                    setlimitcardview.setVisibility(View.INVISIBLE);
                    onStart();

                }
                if (id == R.id.price_descending){
                    Query query = firebaseFirestore.collection("Apartments").orderBy("price", Query.Direction.DESCENDING);
                    options = new FirestoreRecyclerOptions.Builder<Model>()
                            .setQuery(query,Model.class)
                            .build();

                    adapter = new FirestoreRecyclerAdapter<Model,ItemViewHolder>(options) {
                        @NonNull
                        @Override
                        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_recyclerviewlist,parent,false);
                            return new ItemViewHolder(v);
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Model model) {

                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ApartmentImages").
                                    document(model.getDocumentid());
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    Picasso.get().load(documentSnapshot.getString("image0")).into(holder.imageSlider);
                                }
                            });


                            holder.Title.setText(model.getStreetname());
                            holder.Price.setText(String.valueOf(model.getPrice())+" CAD");
                            holder.Description.setText(model.getDescription());

                            holder.message.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            });


                            holder.Favor.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    db.collection("Favorites").document(userID).collection("Selected").
                                            document(model.getDocumentid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot result = task.getResult();
                                                if(!result.exists()){
                                                    holder.Favor.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                                                    Map<String, Object> doc = new HashMap<>();
                                                    doc.put("documentid", model.getDocumentid());
                                                    doc.put("email", model.getEmail());
                                                    doc.put("streetname", model.getStreetname());
                                                    doc.put("price", model.getPrice());
                                                    doc.put("place", model.getPlace());
                                                    doc.put("description", model.getDescription());
                                                    doc.put("tenantid", userID);
                                                    doc.put("type", model.getType());
                                                    doc.put("renterid", model.getRenterid());
                                                    db.collection("Favorites").document(userID).collection("Selected").
                                                            document(model.getDocumentid()).set(doc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                else{
                                                    holder.Favor.setBackgroundResource(R.drawable.ic_baseline_favorite_shadow_24);
                                                    DocumentReference eRef = db.collection("Favorites").document(userID).
                                                            collection("Selected")
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
                                            }
                                        }
                                    });


                                }
                            });

                            holder.cardView.setOnClickListener(new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    Intent intent = new Intent(view.getContext(),TenantApartmentDetails.class);
                                    intent.putExtra("Price",String.valueOf(model.getPrice()));
                                    intent.putExtra("Title",model.getStreetname());
                                    intent.putExtra("UserID",userID);
                                    intent.putExtra("DocumentID",model.getDocumentid());
                                    intent.putExtra("Place",model.getPlace());
                                    intent.putExtra("Description",model.getDescription());
                                    intent.putExtra("RenterID",model.getRenterid());
                                    startActivity(intent);
                                }
                            });

                        }
                    };

                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
                    recyclerView.setAdapter(adapter);
                    onStart();

                }
                return false;
            }
        });
        searchView = v.findViewById(R.id.search_view_home_bottom_view);
        mapView = (MapView) v.findViewById(R.id.mapViewHomeFragment);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        LinearLayout linearLayout = v.findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        recyclerView = v.findViewById(R.id.recyclerViewBottomSheet);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db2 = FirebaseFirestore.getInstance();



        Query query = firebaseFirestore.collection("Apartments");
        options = new FirestoreRecyclerOptions.Builder<Model>()
                .setQuery(query,Model.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Model,ItemViewHolder>(options) {
            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenant_recyclerviewlist,parent,false);
                return new ItemViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Model model) {

                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ApartmentImages").
                        document(model.getDocumentid());
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Picasso.get().load(documentSnapshot.getString("image0")).into(holder.imageSlider);
                    }
                });


                holder.Title.setText(model.getStreetname());
                holder.Price.setText(String.valueOf(model.getPrice())+" CAD");
                holder.Description.setText(model.getDescription());

                holder.message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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


                holder.Favor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        db.collection("Favorites").document(userID).collection("Selected").
                                document(model.getDocumentid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot result = task.getResult();
                                    if(!result.exists()){
                                        holder.Favor.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                                        Map<String, Object> doc = new HashMap<>();
                                        doc.put("documentid", model.getDocumentid());
                                        doc.put("email", model.getEmail());
                                        doc.put("streetname", model.getStreetname());
                                        doc.put("price", model.getPrice());
                                        doc.put("place", model.getPlace());
                                        doc.put("description", model.getDescription());
                                        doc.put("tenantid", userID);
                                        doc.put("type", model.getType());
                                        doc.put("renterid", model.getRenterid());
                                        db.collection("Favorites").document(userID).collection("Selected").
                                                document(model.getDocumentid()).set(doc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else{
                                        holder.Favor.setBackgroundResource(R.drawable.ic_baseline_favorite_shadow_24);
                                        DocumentReference eRef = db.collection("Favorites").document(userID).
                                                collection("Selected")
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
                                }
                            }
                        });


                    }
                });

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(),TenantApartmentDetails.class);
                        intent.putExtra("Price",String.valueOf(model.getPrice()));
                        intent.putExtra("Title",model.getStreetname());
                        intent.putExtra("UserID",userID);
                        intent.putExtra("DocumentID",model.getDocumentid());
                        intent.putExtra("Place",model.getPlace());
                        intent.putExtra("Description",model.getDescription());
                        intent.putExtra("RenterID",model.getRenterid());
                        startActivity(intent);
                    }
                });

            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(adapter);



        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchview = searchView.getQuery().toString();
                onStart();
            }
        });


        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        db2.collection("Apartments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("place")) {
                                markersArray.add(entry.getValue().toString());
                            }
                        }
                    }
                }
            }
        });

        for(int i = 0 ; i < markersArray.size() ; i++) {
            address= markersArray.get(i);
            mMap = googleMap;
            List<Address> addressList = null;
            if (address != null || !address.equals("")) {
                Geocoder geocoder = new Geocoder(getContext());
                try {
                    addressList = geocoder.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address1 = addressList.get(i);
                LatLng latLng = new LatLng(address1.getLatitude(), address1.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));
            }
        }

    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView Price;
        TextView Title;
        TextView Description;
        TextView Place;
        Button Favor,message;
        ImageView imageSlider;
        CardView cardView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Price = itemView.findViewById(R.id.txt_tenant_price);
            imageSlider = itemView.findViewById(R.id.Img_tenant_apartment);
            Place = itemView.findViewById(R.id.txt_tenant_place);
            Title = itemView.findViewById(R.id.txt_tenant_title);
            Description=itemView.findViewById(R.id.txt_tenant_description);
            Favor = itemView.findViewById(R.id.btn_tenant_favor2);
            message = itemView.findViewById(R.id.btn_tenant_message2);
            cardView = itemView.findViewById(R.id.card_view_tenant_recyclerview);
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