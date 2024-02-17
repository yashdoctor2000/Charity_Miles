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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
                    authenticateUser(username, password);
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

    }

    private void authenticateUser(final String username, final String password) {
        // Query the database by username
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userFound = false;
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    // Assuming each user's data is stored under a unique key
                    String dbUsername = userSnapshot.child("username").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);

                    if (dbUsername != null && dbUsername.equals(username)) {
                        // Username matches, now check password
                        if (dbPassword != null && dbPassword.equals(password)) {
                            // Password matches, login successful
                            Toast.makeText(MainActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                            userFound = true;
                            // Intent to navigate to another activity
                            // Intent intent = new Intent(MainActivity.this, NextActivity.class);
                            // startActivity(intent);
                            break; // Stop loop once user is found
                        } else {
                            // Username matches but password does not
                            passwordEditText.setError("Incorrect password");
                            userFound = true;
                            break; // Stop loop once user is found
                        }
                    }
                }
                if (!userFound) {
                    // User does not exist
                    usernameEditText.setError("User does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}