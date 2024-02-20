package com.example.charitymiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpPage extends AppCompatActivity {

    private EditText nameEditText, addressEditText, mobileNumberEditText, passwordEditText, usernameEditText;
    private Button signUpButton;
    private RadioGroup roleRadioGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Users");
        nameEditText = findViewById(R.id.nameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        signUpButton = findViewById(R.id.signUpButton);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nameEditText.getText().toString().trim();
                final String address = addressEditText.getText().toString().trim();
                final String mobileNumber = mobileNumberEditText.getText().toString().trim();
                final String username = usernameEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString().trim();
                int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();

                String role = ""; // Default role value
                if (selectedRoleId == R.id.donorRadioButton) {
                    role = "Donor";
                } else if (selectedRoleId == R.id.receiverRadioButton) {
                    role = "Receiver";
                }


                if (validateForm(name, address, mobileNumber, username, password, role)) {
                    String finalRole = role;
                    checkUsernameExists(username, new CheckUserCallback() {
                        @Override
                        public void onCallback(boolean exists) {
                            if (!exists) {
                                registerUser(name, address, mobileNumber, username, password, finalRole);
                            } else {
                                Toast.makeText(SignUpPage.this, "Username already exists.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

    }

    private boolean validateForm(String name, String address, String mobileNumber, String username, String password, String role){
        if (name.isEmpty() || address.isEmpty() || mobileNumber.isEmpty() || password.isEmpty() || role.isEmpty() || username.isEmpty()) {
            // If any field is empty, show a toast message
            Toast.makeText(SignUpPage.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!username.contains("@") || !username.endsWith(".com")){
            usernameEditText.setError("Username must be an email ending with @email.com");
            return false;
        }
        else if(password.length() < 6){
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }
        else if(mobileNumber.length() != 10){
            mobileNumberEditText.setError("Mobile number must be exactly 10 digits long");
            return false;
        }
        else {
            // All fields are filled, proceed to save the data to Firebase Realtime Database
            return true;
        }

    }

    private void registerUser(final String name, final String address, final String mobileNumber, final String username, final String password, final String role) {
        mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Registration success
                FirebaseUser user = mAuth.getCurrentUser();
                saveUserData(name, address, mobileNumber, password, role, username);
            } else {
                // Registration failed
                String errorMessage = task.getException().getMessage();
                Toast.makeText(SignUpPage.this, ""+errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUsernameExists(String username, CheckUserCallback callback) {
        Query usernameQuery = myRef.orderByChild("username").equalTo(username);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onCallback(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignUpPage.this, "Failed to check username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private interface CheckUserCallback {
        void onCallback(boolean exists);
    }

    private void saveUserData(String name, String address, String mobileNumber, String password, String role, String username) {
        // Creating a unique ID for each user (or you can use the mobile number if it's unique)
        FirebaseUser fuser = mAuth.getCurrentUser();
        if(fuser !=null) {
            String userId = fuser.getUid();
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("address", address);
            user.put("mobileNumber", mobileNumber);
            user.put("role", role);
            user.put("username", username); // Consider using a more secure way to store user passwords or sensitive information

            myRef.child(userId).setValue(user).addOnSuccessListener(aVoid -> {
                Toast.makeText(SignUpPage.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpPage.this, MainActivity.class);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                Toast.makeText(SignUpPage.this, "Failed to register user", Toast.LENGTH_SHORT).show();
            });
        }else{
            Toast.makeText(SignUpPage.this, "Authentication failed. User not signed in.", Toast.LENGTH_SHORT).show();
        }
    }
}
