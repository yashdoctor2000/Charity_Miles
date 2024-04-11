package com.example.charitymiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PickUpDetailPage extends AppCompatActivity {

    private TextView textViewTypeOfItems, textViewDonorName, textViewNumberofItems, textViewDateTime, textViewDetailContact, textViewDetailAddress;
    private Button buttonPickUpComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_detail_page);

        textViewDonorName = findViewById(R.id.textViewDonorName);
        textViewTypeOfItems=findViewById(R.id.textViewTypeOfItems);
        textViewNumberofItems=findViewById(R.id.textViewNumberofItems);
        textViewDateTime = findViewById(R.id.textViewDateTime);
        textViewDetailContact = findViewById(R.id.textViewDetailContact);
        textViewDetailAddress=findViewById(R.id.textViewDetailAddress);
        //etPreferredTime = findViewById(R.id.etPreferredTime);
        buttonPickUpComplete = findViewById(R.id.buttonPickUpComplete);
        //etAdditionalInformation = findViewById(R.id.etAdditionalInformation);


        ReceiverModel receiver = (ReceiverModel) getIntent().getSerializableExtra("PickUpDetails");

        String pickupRequestId = receiver.getUid();
        //Toast.makeText(PickUpDetailPage.this,""+id,Toast.LENGTH_LONG).show();

        textViewDonorName.setText(receiver.getDonorName());
        textViewTypeOfItems.setText(receiver.getDonationItem());
        textViewNumberofItems.setText(receiver.getDonationQuantity());
        textViewDateTime.setText(receiver.getDate() + " "+ receiver.getTime());
        textViewDetailContact.setText(receiver.getContact());
        textViewDetailAddress.setText(receiver.getDesAddress());

        buttonPickUpComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PickUpDetailPage.this)
                        .setTitle("Confirm Pickup Completion")
                        .setMessage("Are you sure you want to mark this pickup as complete?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Yes", so proceed with updating the status
                                updatePickupStatus(pickupRequestId);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
    }

    private void updatePickupStatus(String pickupRequestId) {
        DatabaseReference pickupRequestRef = FirebaseDatabase.getInstance().getReference("Pick-UP-Request").child(pickupRequestId);

        pickupRequestRef.child("IsStatus").setValue(0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PickUpDetailPage.this, "Pickup marked as complete", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PickUpDetailPage.this,ReceiverHomePage.class);
                        startActivity(intent);
                        // Optionally, navigate the user away from this page or refresh the data
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PickUpDetailPage.this, "Failed to update pickup status", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}