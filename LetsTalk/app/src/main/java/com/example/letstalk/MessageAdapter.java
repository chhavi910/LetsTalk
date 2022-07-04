package com.example.letstalk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    List<Modelclass> messageList;
    String userName;

    boolean status;
    int send,receive;

    public MessageAdapter(List<Modelclass> messageList, String userName) {
        this.messageList = messageList;
        this.userName = userName;

        status=false;
        send=1;
        receive=2;
    }

    @NonNull
    @NotNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==send){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_send,parent,false);
        }
        else{
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recieve,parent,false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageViewHolder holder, int position) {
        holder.textView.setText(messageList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public  class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            if(status){
                textView=itemView.findViewById(R.id.messagesend);
            }
           else {
                textView=itemView.findViewById(R.id.messagerecieve);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getFrom().equals(userName)){
            status =true;
            return send;
        }
       else{
           status=false;
           return receive;
        }
    }
}
