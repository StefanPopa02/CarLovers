package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.stefanpopa.carloversapp.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button registerBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        registerBtn = findViewById(R.id.registerBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString();

                if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)) {
                    Toast.makeText(RegisterActivity.this, "Empty fields are not allowed", Toast.LENGTH_LONG).show();
                } else if (passwordText.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Your password must have atleast 6 characters", Toast.LENGTH_LONG).show();
                } else {
                    registerUser(emailText, passwordText);
                }
            }
        });
    }

    private void registerUser(String emailText, String passwordText) {
        firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "User registered succesfully!",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "Registration failed!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}