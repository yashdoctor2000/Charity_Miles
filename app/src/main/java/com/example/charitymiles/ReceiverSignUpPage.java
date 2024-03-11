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
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class ReceiverSignUpPage extends AppCompatActivity {
    private EditText orgNameEditText, orgContactEditText, orgDescriptionEditText;
    private Button addPhotoButton, submitReceiverInfoButton;
    private ActivityResultLauncher<String> mGetContent;
    private static final int REQUEST_PERMISSION = 1; // This can be any integer unique to this request

    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private ImageView selectedImage;
    private Uri imageUri;
    private DatabaseReference databaseReference;


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
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        selectedImage = findViewById(R.id.selectedImage);



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
                    selectedImage.setImageURI(uri);
                    imageUri = uri;
                    //ImageView imageView = findViewById(R.id.selectedImage);
                    //imageView.setImageURI(uri);
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
                //String userId = user.getUid();
                //Toast.makeText(ReceiverSignUpPage.this,""+userId,Toast.LENGTH_LONG).show();
                if (imageUri != null){
                    uploadImageToFirebase(user.getUid());
                }
                else{
                    Toast.makeText(ReceiverSignUpPage.this, "Please select an image and make sure you're logged in.", Toast.LENGTH_LONG).show();
                }
                // Implement your submission logic here
                // This might involve validating input fields, uploading photos, and saving data to Firebase
            }
        });
    }

    private void uploadImageToFirebase(String userId) {
        StorageReference fileReference = storageReference.child(userId + "/" + System.currentTimeMillis() + ".jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        saveUserData(userId, imageUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReceiverSignUpPage.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(String userId, String imageUrl) {
        String orgName = orgNameEditText.getText().toString().trim();
        String orgContact = orgContactEditText.getText().toString().trim();
        String orgDescription = orgDescriptionEditText.getText().toString().trim();
        AutoCompleteTextView donationTypeView = findViewById(R.id.donationTypeAutoComplete);
        String donationType = donationTypeView.getText().toString().trim(); // Get the selected donation type


        // Create a HashMap to store user data
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("orgName", orgName);
        userData.put("orgContact", orgContact);
        userData.put("orgDescription", orgDescription);
        userData.put("donationType", donationType); // Include the donation type in the map
        userData.put("imageUrl", imageUrl); // Include the image URL in the map

        // Save the user data in the Realtime Database under the user's UID
        databaseReference.child(userId).updateChildren(userData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(ReceiverSignUpPage.this, "User data saved successfully", Toast.LENGTH_LONG).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(ReceiverSignUpPage.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
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