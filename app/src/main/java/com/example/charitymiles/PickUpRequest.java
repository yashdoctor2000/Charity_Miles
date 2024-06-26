package com.example.charitymiles;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PickUpRequest extends AppCompatActivity {

    private EditText etOrganizationName, etOrganizationDetails, etContactNumber;
    private EditText etAddress, etDonationQuantity,etDonationItem;
    private EditText etPreferredDate, etPreferredTime, etAdditionalInformation;
    private FirebaseAuth mAuth;
    private Button btnSchedulePickup;
    private DatabaseReference myRef, myRequest;

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
        etContactNumber = findViewById(R.id.etContactNumber);
        etAdditionalInformation = findViewById(R.id.etAdditionalInformation);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Donations");
        myRequest = FirebaseDatabase.getInstance().getReference("Pick-UP-Request");


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
                final String donationQuantity = etDonationQuantity.getText().toString().trim();
                final String donationItem = etDonationItem.getText().toString().trim();
                final String date = etPreferredDate.getText().toString().trim();
                final String time = etPreferredTime.getText().toString().trim();
                final String addInfo = etAdditionalInformation.getText().toString().trim();
                final String contact = etContactNumber.getText().toString().trim();
                final String OrgId = Uid;
                final int status = 1;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = dateFormat.format(cal.getTime());

                if (desAddress.isEmpty() || donationQuantity.isEmpty() || donationItem.isEmpty() || date.isEmpty() || time.isEmpty() || contact.isEmpty()) {
                    Toast.makeText(PickUpRequest.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return; // Stop the execution if any field is empty.
                }

                FirebaseUser Donor = mAuth.getCurrentUser();
                if(Donor != null){
                    String DonorId = Donor.getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(DonorId);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Assuming "name" is a field under the user's UID
                                String Donorname = snapshot.child("name").getValue(String.class);

                                Map<String, Object> donation = new HashMap<>();
                                Map<String, Object> request = new HashMap<>();
                                donation.put("OrgName", OrgName);
                                donation.put("DonorName", Donorname);
                                donation.put("desAddress", desAddress);
                                donation.put("donationItem",donationItem);
                                donation.put("donationQuantity", donationQuantity);
                                donation.put("date",date);
                                donation.put("time",time);
                                donation.put("OrgId",OrgId);
                                donation.put("DonorId",DonorId);
                                donation.put("AddInfo",addInfo);
                                donation.put("Contact",contact);
                                donation.put("Date", formattedDate);

                                request.put("OrgName", OrgName);
                                request.put("desAddress", desAddress);
                                request.put("DonorName", Donorname);
                                request.put("donationItem",donationItem);
                                request.put("donationQuantity", donationQuantity);
                                request.put("date",date);
                                request.put("time",time);
                                request.put("OrgId",OrgId);
                                request.put("DonorId",DonorId);
                                request.put("AddInfo",addInfo);
                                request.put("IsStatus", status);
                                request.put("Contact", contact);

                                myRef.push().setValue(donation).addOnSuccessListener(aVoid ->{


                                }).addOnFailureListener(aVoid ->{
                                    Toast.makeText(PickUpRequest.this, "Failed", Toast.LENGTH_SHORT).show();
                                });

                                myRequest.push().setValue(request).addOnSuccessListener(aVoid ->{
                                    Toast.makeText(PickUpRequest.this, "Donation Requested", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PickUpRequest.this,DonorFinal.class);
                                    intent.putExtra("DonatedOrg",organization);
                                    startActivity(intent);
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                }

            }
        });


        //Toast.makeText(PickUpRequest.this,""+Uid,Toast.LENGTH_LONG).show();


    }
}