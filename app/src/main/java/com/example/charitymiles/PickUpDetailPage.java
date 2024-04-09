package com.example.charitymiles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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

        textViewDonorName.setText(receiver.getDonorName());
        textViewTypeOfItems.setText(receiver.getDonationItem());
        textViewNumberofItems.setText(receiver.getDonationQuantity());
        textViewDateTime.setText(receiver.getDate() + " "+ receiver.getTime());
        textViewDetailContact.setText(receiver.getContact());
        textViewDetailAddress.setText(receiver.getDesAddress());
    }
}