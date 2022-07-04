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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    CircleImageView profilePic;
    EditText email,password,userName;
    Button signup;
    Boolean isImageSelected=false;

    Uri imageUri;

   FirebaseAuth auth;
   FirebaseDatabase database;
   DatabaseReference reference;
   FirebaseStorage storage;
   StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        profilePic=findViewById(R.id.profilePic);
        userName=findViewById(R.id.userName);
        email=findViewById(R.id.SignUpEmailAddress);
        password=findViewById(R.id.SignUpPassword);
        signup=findViewById(R.id.SignUp2);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String uname,uemail,upassword;
               uname=userName.getText().toString();
               uemail=email.getText().toString();
               upassword=password.getText().toString();
               if(!uname.equals("") && !upassword.equals("") && !uemail.equals("")){
                   signUp(uname,uemail,upassword);
               }
               else{
                   Toast.makeText(SignUpActivity.this,"Enter the information.",Toast.LENGTH_LONG).show();
               }
            }
        });
    }
    public void selectImage(){
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,1);
    }
    public void signUp(String uname,String uemail,String upassword){
        auth.createUserWithEmailAndPassword(uemail,upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    reference.child("Users").child(auth.getUid()).child("UserName").setValue(uname);
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
                                        reference.child("Users").child(auth.getUid()).child("ProfilePic").setValue(imagePath);
                                    }
                                });
                            }
                        });
                    }
                    else
                        reference.child("Users").child(auth.getUid()).child("ProfilePic").setValue(null);

                    Toast.makeText(SignUpActivity.this,"Sign up complete!",Toast.LENGTH_LONG).show();
                    Intent i=new Intent(SignUpActivity.this,MainActivity.class);
                 //   i.putExtra("username",uname);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(SignUpActivity.this,"OOPS!Something went wrong.Try again later.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null && requestCode==1 && resultCode==RESULT_OK){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profilePic);
            isImageSelected=true;
        }
    }
}