package com.example.letstalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class LogInActivity extends AppCompatActivity {
    EditText email,password;
    Button signup,login;
    TextView fPassword;
    SignInButton googleSignIn;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        email=findViewById(R.id.EmailAddress);
        password=findViewById(R.id.Password);
        signup=findViewById(R.id.SignUp);
        login=findViewById(R.id.LogIn);
        fPassword=findViewById(R.id.FPassword);
        googleSignIn=findViewById(R.id.googlesignin);
        auth=FirebaseAuth.getInstance();
       databaseReference= FirebaseDatabase.getInstance().getReference();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail,userPassword;
                userEmail=email.getText().toString();
                userPassword=password.getText().toString();
                if(!userEmail.isEmpty() && !userPassword.isEmpty())
                    logIn(userEmail,userPassword);
                else
                    Toast.makeText(LogInActivity.this,"Enter email id and password",Toast.LENGTH_LONG).show();

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i=new Intent(LogInActivity.this,SignUpActivity.class);
              startActivity(i);
            }
        });
        fPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LogInActivity.this,ForgotActivity.class);
                startActivity(i);
            }
        });
         googleSignIn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                signInGoogle();
             }
         });
    }
    public void logIn(String email,String password){
        auth.signInWithEmailAndPassword(email,password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                           if(task.isSuccessful()){
                               Intent i=new Intent(LogInActivity.this,MainActivity.class);
                               startActivity(i);
                               finish();
                           }
                           else{
                               Toast.makeText(LogInActivity.this,"OOPS!Some error occured.Try after sometime.",Toast.LENGTH_LONG).show();
                           }
                        }
                        }
        );
    }
    public void signInGoogle(){
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient= GoogleSignIn.getClient(this,gso);
        Intent i=googleSignInClient.getSignInIntent();
        startActivityForResult(i,3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user=auth.getCurrentUser();
        if(user!=null){
            Intent i=new Intent(LogInActivity.this,MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3){
            Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                Toast.makeText(LogInActivity.this,"You are successfully signed in!",Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(LogInActivity.this,"OOPS!Some error occured.Try after sometime.",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential authCredential= GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user= auth.getCurrentUser();
                    databaseReference.child("Users").child(user.getUid()).child("UserName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if(snapshot.getValue()!=null){
                                Intent i = new Intent(LogInActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                Intent i = new Intent(LogInActivity.this, GoogleSignUpActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

                }
                else{
                    Toast.makeText(LogInActivity.this,"OOPS!Some error occured.Try after sometime.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }
}