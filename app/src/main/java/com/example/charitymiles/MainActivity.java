package com.example.charitymiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;
    private TextView forgotPassword;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.signup_button);
        forgotPassword = findViewById(R.id.forgot_password);
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(username,password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    //Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_LONG).show();
                                    FirebaseUser fuser = mAuth.getCurrentUser();
                                    if(fuser != null){
                                        String uid = fuser.getUid();
                                        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String role = snapshot.child("role").getValue(String.class);
                                                if (role.equals("Donor")){
                                                    //Toast.makeText(getApplicationContext(),"Redirect to Donor",Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(MainActivity.this, DonorHomePage.class);
                                                    startActivity(intent);
                                                } else if (role.equals("Receiver")) {
                                                    Intent intent = new Intent(MainActivity.this, ReceiverHomePage.class);
                                                    startActivity(intent);
                                                    //Toast.makeText(getApplicationContext(),"Redirect to Receiver",Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    Toast.makeText(getApplicationContext(),"Can't get value: "+role,Toast.LENGTH_LONG).show();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(getApplicationContext(),"Database Error"+error,Toast.LENGTH_LONG).show();

                                            }
                                        });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    if (username.isEmpty()) {
                        usernameEditText.setError("This field cannot be empty");
                    }
                    if (password.isEmpty()) {
                        passwordEditText.setError("This field cannot be empty");
                    }
                }

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpPage.class);
                startActivity(intent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, forgetPassword.class);
                startActivity(intent);
            }
        });

    }

}