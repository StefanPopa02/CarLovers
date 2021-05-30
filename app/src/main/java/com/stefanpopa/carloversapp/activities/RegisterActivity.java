package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.util.UserApi;

import java.util.Arrays;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button registerBtn;
    private TextView logInInstead;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRootRef;
    private ProgressDialog pd;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.usernameEditText);
        firstName = findViewById(R.id.firstNameEditText);
        lastName = findViewById(R.id.lastNameEditText);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        registerBtn = findViewById(R.id.registerBtn);
        logInInstead = findViewById(R.id.loginUser);
        firebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();

        logInInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString();
                String usernameText = username.getText().toString().trim();
                String firstNameText = firstName.getText().toString().trim();
                String lastNameText = lastName.getText().toString().trim();

                if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(usernameText) ||
                        TextUtils.isEmpty(firstNameText) || TextUtils.isEmpty(lastNameText)) {
                    Toast.makeText(RegisterActivity.this, "Empty fields are not allowed", Toast.LENGTH_LONG).show();
                } else if (passwordText.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Your password must have atleast 6 characters", Toast.LENGTH_LONG).show();
                } else {
                    registerUser(emailText, passwordText, usernameText, firstNameText, lastNameText);
                }
            }
        });
    }

    private void registerUser(String emailText, String passwordText, String usernameText, String firstNameText, String lastNameText) {

        pd.setMessage("Please Wait");
        pd.show();
        firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(RegisterActivity.this, "A intrat pe success", Toast.LENGTH_SHORT).show();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("firstName", firstNameText);
                    map.put("lastName", lastNameText);
                    map.put("email", emailText);
                    map.put("username", usernameText);
                    map.put("id", firebaseAuth.getCurrentUser().getUid());
                    map.put("bio", "");
                    map.put("imageurl", "default");
                    map.put("followingClubs", Arrays.asList());
                    map.put("userType", "user");

                    // Add a new document with a generated ID
                    db.collection("users")
                            .add(map)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                                    pd.dismiss();
                                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "Registration succesfully!", Toast.LENGTH_SHORT).show();

                                                UserApi userApi = UserApi.getInstance();
                                                userApi.setUserId(firebaseAuth.getCurrentUser().getUid());
                                                userApi.setUsername(usernameText);

                                                Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("FireStore", "Error adding document", e);
                                    pd.dismiss();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                Log.d("REGISTER_TAG", e.getMessage());
                pd.dismiss();
            }
        });
    }
}