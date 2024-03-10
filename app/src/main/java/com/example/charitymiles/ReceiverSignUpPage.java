package com.example.charitymiles;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ReceiverSignUpPage extends AppCompatActivity {
    private EditText orgNameEditText, orgContactEditText, orgDescriptionEditText;
    private Button addPhotoButton, submitReceiverInfoButton;
    private ActivityResultLauncher<String> mGetContent;
    private static final int REQUEST_PERMISSION = 1; // This can be any integer unique to this request

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


        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.donationTypeAutoComplete);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.donation_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter);
        // Initialize photo addition logic

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                // Handle the returned Uri
                if (uri != null) {
                    // Use the Uri to load the image. E.g., display in an ImageView or upload to your server
                    // ImageView imageView = findViewById(R.id.your_image_view_id);
                    // imageView.setImageURI(uri);
                }
            }
        });
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ReceiverSignUpPage.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted, open the gallery
                    mGetContent.launch("image/*");
                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(ReceiverSignUpPage.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                }
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the gallery
                mGetContent.launch("image/*");
            } else {
                // Permission was denied. Handle the error.
                Toast.makeText(this, "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}