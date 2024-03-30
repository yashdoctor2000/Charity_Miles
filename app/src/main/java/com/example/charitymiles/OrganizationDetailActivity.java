package com.example.charitymiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class OrganizationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_detail);
        ImageView imageViewDetailPhoto = findViewById(R.id.imageViewDetailPhoto);
        TextView textViewDetailDescription = findViewById(R.id.textViewDetailDescription);
        TextView textViewDetailContact = findViewById(R.id.textViewDetailContact);
        TextView textViewUid = findViewById(R.id.textViewUid);
        TextView textViewDetailAddress = findViewById(R.id.textViewDetailAddress);
        Button buttonRequestPickup = findViewById(R.id.buttonRequestPickup);


        OrganizationModel organization = (OrganizationModel) getIntent().getSerializableExtra("OrganizationDetail");
        textViewUid.setText(organization.getOrgDescription());
        textViewDetailDescription.setText(organization.getorgName());
        textViewDetailContact.setText(organization.getOrgContact());
        textViewDetailAddress.setText(organization.getAddress());

// For the contact number to be clickable and open the dialer
        textViewDetailContact.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + organization.getOrgContact()));
            startActivity(dialIntent);
        });

// Assuming you're using Glide for image loading
        Glide.with(this).load(organization.getimageUrl()).into(imageViewDetailPhoto);

        buttonRequestPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizationDetailActivity.this, PickUpRequest.class);
                intent.putExtra("OrganizationForPickup",organization);
                startActivity(intent);
            }
        });

    }
}