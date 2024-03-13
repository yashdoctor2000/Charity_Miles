package com.example.charitymiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DonorHomePage extends AppCompatActivity {
    private Spinner spinnerDonationTypes;
    private RecyclerView recyclerViewOrganizations;
    private OrganizationAdapter organizationAdapter;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home_page);
        spinnerDonationTypes = findViewById(R.id.spinnerDonationTypes);
        recyclerViewOrganizations = findViewById(R.id.recyclerViewOrganizations);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        initializeSpinner();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerViewOrganizations.setLayoutManager(new LinearLayoutManager(this));
        organizationAdapter = new OrganizationAdapter(new ArrayList<>(), this);
        recyclerViewOrganizations.setAdapter(organizationAdapter);
    }

    private void initializeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.donation_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDonationTypes.setAdapter(adapter);
        spinnerDonationTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected donation type from the spinner
                String selectedDonationType = parentView.getItemAtPosition(position).toString();
                // Load organizations based on the selected donation type
                loadOrganizations(selectedDonationType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Interface callback
            }
        });
    }

    private void loadOrganizations(String donationType) {
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<OrganizationModel> organizations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the record is an organization and matches the donationType
                    String role = snapshot.child("role").getValue(String.class);
                    String type = snapshot.child("donationType").getValue(String.class);
                    if ("Receiver".equals(role) && donationType.equals(type)) {
                        OrganizationModel organization = snapshot.getValue(OrganizationModel.class);
                        organizations.add(organization);
                    }
                }
                organizationAdapter.setOrganizations(organizations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase", "Database error: " + databaseError.getMessage());
            }
        });
    }

    public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.ViewHolder>{
        private List<OrganizationModel> organizations;
        private Context context;


        public OrganizationAdapter(List<OrganizationModel> organizations, Context context) {
            this.organizations = organizations;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organization_summary_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OrganizationModel organization = organizations.get(position);
            holder.textViewOrganizationName.setText(organization.getName());
            Glide.with(context).load(organization.getimageUrl()).into(holder.imageViewOrganizationPhoto);
        }

        @Override
        public int getItemCount() {
            return organizations.size();
        }
        public void setOrganizations(List<OrganizationModel> organizations) {
            this.organizations = organizations;
            notifyDataSetChanged();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewOrganizationName;
            ImageView imageViewOrganizationPhoto;

            ViewHolder(View itemView) {
                super(itemView);
                textViewOrganizationName = itemView.findViewById(R.id.textViewOrganizationName);
                imageViewOrganizationPhoto = itemView.findViewById(R.id.imageViewOrganizationPhoto);
            }
        }

    }
}