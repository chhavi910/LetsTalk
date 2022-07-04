package com.example.letstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class ForgotActivity extends AppCompatActivity {
    EditText email;
    Button send;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        email=findViewById(R.id.ForgotEmailAddress);
        send=findViewById(R.id.Send);
        auth=FirebaseAuth.getInstance();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().equals("")) {
                    auth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotActivity.this, "Check your email for the reset link", Toast.LENGTH_LONG).show();
                                Intent i=new Intent(ForgotActivity.this,LogInActivity.class);
                                startActivity(i);
                                finish();
                            } else
                                Toast.makeText(ForgotActivity.this, "OOPS!Something went wrong.Try again later.", Toast.LENGTH_LONG).show();
                                Intent i=new Intent(ForgotActivity.this,LogInActivity.class);
                                startActivity(i);
                                finish();
                        }
                    });
                }
            }
        });
    }
}