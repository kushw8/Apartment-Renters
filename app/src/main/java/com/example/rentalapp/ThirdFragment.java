package com.example.rentalapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.rentalapp.tenantfragments.AllAdsFragment;
import com.example.rentalapp.tenantfragments.MyadsFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ThirdFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView_third);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("Chatroom").whereEqualTo("renterid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirestoreRecyclerOptions<ChatUserModel> options = new FirestoreRecyclerOptions.Builder<ChatUserModel>()
                .setQuery(query,ChatUserModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<ChatUserModel, ItemViewHolder>(options) {

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_chat_list_item,parent,false);
                return new ItemViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull ChatUserModel model) {

                holder.Chatroom.setText(model.getChatroomname());
                holder.Username.setText(model.getTenantname());

                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ChatroomActivity.class);
                        intent.putExtra("tenantid",model.getTenantid());
                        intent.putExtra("documentid",model.getDocumentid());
                        intent.putExtra("renter",model.getRenterid());
                        intent.putExtra("chatid",model.getChatid());
                        intent.putExtra("rentername",model.getRentername());
                        intent.putExtra("tenantname",model.getTenantname());
                        intent.putExtra("chatroomname",model.getChatroomname());
                        intent.putExtra("isuser","1");
                        intent.putExtra("activity","third");
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

        TextView Chatroom;
        TextView Username;
        CardView linearLayout;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Chatroom = itemView.findViewById(R.id.txt_chatlist_chatroom);
            Username = itemView.findViewById(R.id.txt_chatlist_username);
            linearLayout = itemView.findViewById(R.id.card_layout_chat_user_list);

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