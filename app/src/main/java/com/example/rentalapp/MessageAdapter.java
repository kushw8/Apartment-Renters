package com.example.rentalapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ChatMessage> chats;
    FirebaseAuth mAuth;
    public MessageAdapter(Context context, List<ChatMessage> chats){
        this.context = context;
        this.chats = chats;
        mAuth =FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =null;
        if(viewType==0){
            view = LayoutInflater.from(context).inflate(R.layout.from_ui,parent,false);
            return new SenderViewHolder(view);
        }
        view = LayoutInflater.from(context).inflate(R.layout.to_ui,parent,false);
        return new ReceiverViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType()==0){
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.mText.setText(chats.get(position).getMessageText());
        }
        if(holder.getItemViewType()==1){
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            receiverViewHolder.mText.setText(chats.get(position).getMessageText());
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(chats.get(position).getFrom().equals(mAuth.getCurrentUser().getUid())){
            return 1;
        }
        return 0;

    }


    public class SenderViewHolder extends RecyclerView.ViewHolder{
        private TextView mText;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.from_chat);
        }
    }
    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        private TextView mText;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.to_chat);
        }
    }
}
