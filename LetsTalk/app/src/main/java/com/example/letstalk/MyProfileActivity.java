package com.example.letstalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {
    CircleImageView profilePic;
    EditText name;
    Button update;
    Boolean isImageSelected=false;
    String image;
    Uri imageUri;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getActionBar();
        name=findViewById(R.id.updateuserName);
        update=findViewById(R.id.updateprofile);
        profilePic=findViewById(R.id.updateprofilePic);

        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        getUserInfo();
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }
    public void getUserInfo(){
        databaseReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String username=snapshot.child("UserName").getValue().toString();
                if(snapshot.child("ProfilePic").getValue()!=null){
                    image=snapshot.child("ProfilePic").getValue().toString();
                    Picasso.get().load(image).into(profilePic);
                }
                name.setText(username);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    public void updateProfile(){
        String updatename=name.getText().toString();
        databaseReference.child("Users").child(auth.getUid()).child("UserName").setValue(updatename);
        if(isImageSelected){
            UUID imageID=UUID.randomUUID();
            String imageName="images/"+imageID+".jpg";
            storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference sReference=storage.getReference(imageName);
                    sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imagePath=uri.toString();
                            databaseReference.child("Users").child(auth.getUid()).child("ProfilePic").setValue(imagePath);
                        }
                    });
                }
            });
        }
        else
            databaseReference.child("Users").child(auth.getUid()).child("ProfilePic").setValue(image);

        Toast.makeText(MyProfileActivity.this,"Your profile is updated!",Toast.LENGTH_LONG).show();
        Intent i=new Intent(MyProfileActivity.this,MainActivity.class);
        i.putExtra("username",updatename);
        startActivity(i);
        finish();

    }
    public void selectImage(){
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,2);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null && requestCode==2 && resultCode==RESULT_OK){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profilePic);
            isImageSelected=true;
        }
    }
}