package com.example.charitymiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ReceiverSignUpPage extends AppCompatActivity {
    private EditText orgNameEditText, orgContactEditText, orgDescriptionEditText;
    private Button addPhotoButton, submitReceiverInfoButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_sign_up_page);

        mAuth = FirebaseAuth.getInstance();
        orgNameEditText = findViewById(R.id.orgNameEditText);
        orgContactEditText = findViewById(R.id.orgContactEditText);
        orgDescriptionEditText = findViewById(R.id.orgDescriptionEditText);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        submitReceiverInfoButton = findViewById(R.id.submitReceiverInfoButton);

        // Initialize photo addition logic
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement your logic to add photos here
                // This could involve opening a file picker, accessing the camera, etc.
            }
        });

        // Submit button logic
        submitReceiverInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user= mAuth.getCurrentUser();
                String userId = user.getUid();
                Toast.makeText(ReceiverSignUpPage.this,""+userId,Toast.LENGTH_LONG).show();
                // Implement your submission logic here
                // This might involve validating input fields, uploading photos, and saving data to Firebase
            }
        });
    }
}