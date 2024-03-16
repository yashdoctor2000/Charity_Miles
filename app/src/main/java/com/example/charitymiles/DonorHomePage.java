package com.example.charitymiles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonorHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home_page);

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.donationTypeAutoComplete);
        organizationNameTextView = findViewById(R.id.organizationNameTextView);
        organizationCategoryTextView = findViewById(R.id.organizationCategoryTextView);
        organizationDescriptionTextView = findViewById(R.id.organizationDescriptionTextView);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.donation_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Add a listener to detect item selection events
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            // Fetch data from Firebase Database based on the selected item
            fetchData(selectedItem);
        });
    }

    // Method to fetch data from Firebase Database based on the selected item
    private void fetchData(String selectedItem) {
        // Query the Firebase Database to get the organization's details based on the selected item
        mDatabase.child("organizations").orderByChild("category").equalTo(selectedItem).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle data fetched from Firebase Database
                if (dataSnapshot.exists()) {
                    // Loop through the dataSnapshot to get each organization's details
                    for (DataSnapshot organizationSnapshot : dataSnapshot.getChildren()) {
                        // Get the organization's details
                        String organizationName = organizationSnapshot.child("name").getValue(String.class);
                        String organizationCategory = organizationSnapshot.child("category").getValue(String.class);
                        String organizationDescription = organizationSnapshot.child("description").getValue(String.class);

                        // Update UI with organization's details
                        organizationNameTextView.setText("Name: " + organizationName);
                        organizationCategoryTextView.setText("Category: " + organizationCategory);
                        organizationDescriptionTextView.setText("Description: " + organizationDescription);

                        // Only display the details of the first organization found (if there are multiple)
                        break;
                    }
                } else {
                    // No organization found for selected category
                    Toast.makeText(DonorHomePage.this, "No organization found for selected category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(DonorHomePage.this, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}