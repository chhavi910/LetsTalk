package com.example.letstalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyChatActivity extends AppCompatActivity {
    RecyclerView rvChat;
    EditText messageText;
    TextView textViewChat;
    FloatingActionButton send;
    ImageView back;

    String userName,otherName;
    DatabaseReference databaseReference;

    MessageAdapter adapter;
    List<Modelclass> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat);
        rvChat=findViewById(R.id.rvchat);
        messageText=findViewById(R.id.messagetext);
        send=findViewById(R.id.sendbutton);
        back=findViewById(R.id.backbutton);
        textViewChat=findViewById(R.id.textViewchat);

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        messageList=new ArrayList<>();

        userName=getIntent().getStringExtra("username");
        otherName=getIntent().getStringExtra("othername");
        textViewChat.setText(otherName);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MyChatActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=messageText.getText().toString();
                if(!message.isEmpty()){
                    sendMessage(message);
                    messageText.setText("");
                }
                else
                    Toast.makeText(MyChatActivity.this,"Please enter a message",Toast.LENGTH_LONG).show();
            }
        });
        getMessage();
    }
    public void sendMessage(String message){
        String keyOfMessage=databaseReference.child("Messages").child(userName).child(otherName).push().getKey();
        Map<String,Object>messageMap=new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("from",userName);
        databaseReference.child("Messages").child(userName).child(otherName).child(keyOfMessage).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    databaseReference.child("Messages").child(otherName).child(userName).child(keyOfMessage).setValue(messageMap);
                }
            }
        });
    }
    public void getMessage(){
        databaseReference.child("Messages").child(userName).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Modelclass modelclass=snapshot.getValue(Modelclass.class);
                messageList.add(modelclass);
                adapter.notifyDataSetChanged();
                rvChat.scrollToPosition(messageList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        adapter=new MessageAdapter(messageList,userName);
        rvChat.setAdapter(adapter);
    }
}