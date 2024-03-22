package com.example.charitymiles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class PickUpRequest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_request);

        OrganizationModel organization = (OrganizationModel) getIntent().getSerializableExtra("OrganizationForPickup");

        String Uid = organization.getUid();

        Toast.makeText(PickUpRequest.this,""+Uid,Toast.LENGTH_LONG).show();


    }
}