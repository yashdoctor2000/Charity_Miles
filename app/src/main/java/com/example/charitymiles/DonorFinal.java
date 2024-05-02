package com.example.charitymiles;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

public class DonorFinal extends AppCompatActivity {

    String organizationName, donorName;
    private TextView tvCongratsMessage, tvCongratsDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_final);

        tvCongratsMessage = findViewById(R.id.tvCongratsMessage);
        tvCongratsDetail = findViewById(R.id.tvCongratsDetail);


        OrganizationModel organization = (OrganizationModel) getIntent().getSerializableExtra("DonatedOrg");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserUID = currentUser.getUid(); // This is the user's UID
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userRef = databaseReference.child("Users").child(currentUserUID);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        donorName = dataSnapshot.child("name").getValue(String.class);

                        // Update the UI elements directly within the onDataChange
                        if (donorName != null) {
                            tvCongratsMessage.setText(String.format("Congratulations, %s!", donorName));
                            tvCongratsDetail.setText(String.format("Your donation to %s is greatly appreciated.", organizationName));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("Firebase", "Failed to read user's name: " + databaseError.getMessage());
                }
            });
        }
        organizationName = organization.getorgName();


        // Find the TextViews
        TextView tvCongratsMessage = findViewById(R.id.tvCongratsMessage);
        TextView tvCongratsDetail = findViewById(R.id.tvCongratsDetail);

        // Set the text dynamically
        tvCongratsMessage.setText(String.format("Congratulations, %s!", donorName));
        tvCongratsDetail.setText(String.format("Your donation to %s is greatly appreciated.", organizationName));

    }
}