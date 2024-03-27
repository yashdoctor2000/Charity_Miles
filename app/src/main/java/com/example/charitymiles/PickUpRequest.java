package com.example.charitymiles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PickUpRequest extends AppCompatActivity {

    private EditText etOrganizationName, etOrganizationDetails;
    private EditText etAddress, etDonationQuantity,etDonationItem;
    private EditText etPreferredDate, etPreferredTime;
    private FirebaseAuth mAuth;
    private Button btnSchedulePickup;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_request);
        etOrganizationName = findViewById(R.id.etOrganizationName);
        etOrganizationDetails=findViewById(R.id.etOrganizationDetails);
        etAddress=findViewById(R.id.etAddress);
        etDonationQuantity = findViewById(R.id.etDonationQuantity);
        etDonationItem = findViewById(R.id.etDonationItem);
        etPreferredDate=findViewById(R.id.etPreferredDate);
        etPreferredTime = findViewById(R.id.etPreferredTime);
        btnSchedulePickup = findViewById(R.id.btnSchedulePickup);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Donations");

        OrganizationModel organization = (OrganizationModel) getIntent().getSerializableExtra("OrganizationForPickup");

        String Uid = organization.getUid();
        String orgname = organization.getorgName();
        String orgContact = organization.getOrgContact();
        etOrganizationName.setText(orgname);
        etOrganizationDetails.setEnabled(false);
        etOrganizationDetails.setText(orgContact);
        etOrganizationName.setEnabled(false);


        btnSchedulePickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String OrgName = etOrganizationName.getText().toString().trim();
                final String desAddress = etAddress.getText().toString().trim();
                final String date = etPreferredDate.getText().toString().trim();
                final String time = etPreferredTime.getText().toString().trim();
                final String OrgId = Uid;

                FirebaseUser Donor = mAuth.getCurrentUser();
                if(Donor != null){
                    String DonorId = Donor.getUid();
                    Map<String, Object> donation = new HashMap<>();
                    donation.put("OrgName", OrgName);
                    donation.put("desAddress", desAddress);
                    donation.put("date",date);
                    donation.put("time",time);
                    donation.put("OrgId",OrgId);
                    donation.put("DonorId",DonorId);

                    myRef.setValue(donation).addOnSuccessListener(aVoid ->{
                        Toast.makeText(PickUpRequest.this, "UDonation Requested", Toast.LENGTH_SHORT).show();

                    }).addOnFailureListener(aVoid ->{
                        Toast.makeText(PickUpRequest.this, "Failed", Toast.LENGTH_SHORT).show();
                    });

                }

            }
        });


        Toast.makeText(PickUpRequest.this,""+Uid,Toast.LENGTH_LONG).show();


    }
}