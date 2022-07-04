package com.example.letstalk;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter <UsersAdapter.ViewHolder>{
    List<String> userList;
    String userName;
    Context mContext;

    DatabaseReference databaseReference;

    public UsersAdapter(List<String> userList, String userName, Context mContext) {
        this.userList = userList;
        this.userName = userName;
        this.mContext = mContext;
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            databaseReference.child("Users").child(userList.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                     String otherName=snapshot.child("UserName").getValue().toString();


                    holder.userName.setText(otherName);

                    if(snapshot.child("ProfilePic").getValue()!=null){
                        String imageURL=snapshot.child("ProfilePic").getValue().toString();
                        Picasso.get().load(imageURL).into(holder.userImage);
                    }
                    holder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i=new Intent(mContext,MyChatActivity.class);
                            i.putExtra("username",userName);
                            i.putExtra("othername",otherName);
                            mContext.startActivity(i);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView userName;
        private CircleImageView userImage;
        private CardView cardView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.textviewusername);
            userImage=itemView.findViewById(R.id.userimage);
            cardView=itemView.findViewById(R.id.userscard);
        }
    }
}
