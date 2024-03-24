package com.example.charitymiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DonorHomePage extends AppCompatActivity {
    private AutoCompleteTextView spinnerDonationTypes;
    private RecyclerView recyclerViewOrganizations;
    private OrganizationAdapter organizationAdapter;
    private DatabaseReference databaseReference;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home_page);
        spinnerDonationTypes = findViewById(R.id.spinnerDonationTypes);
        recyclerViewOrganizations = findViewById(R.id.recyclerViewOrganizations);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        navigationView=findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeSpinner();
        setupRecyclerView();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    //Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
                    // Handle profile selection
                } else if (id == R.id.nav_contact_us) {
                    // Handle contact-us selection
                } else if (id == R.id.nav_about_us) {
                    // Handle about-us selection
                } else if (id == R.id.nav_sign_out) {
                    // Handle sign-out
                }

                // Close the drawer after action
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu, menu); // Ensure this matches your menu file's name
//        return true;
//    }

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
        spinnerDonationTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDonationType = parent.getItemAtPosition(position).toString();
                // Load organizations based on the selected donation type
                loadOrganizations(selectedDonationType);

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
                        if (organization != null) {
                            // Set the key (UID) of the organization data
                            organization.setUid(snapshot.getKey());
                        }
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
            holder.textViewOrganizationName.setText(organization.getorgName());
            holder.textViewOrganizationVision.setText(organization.getorgTagline());
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
            TextView textViewOrganizationVision;
            ImageView imageViewOrganizationPhoto;

            ViewHolder(View itemView) {
                super(itemView);
                textViewOrganizationName = itemView.findViewById(R.id.textViewOrganizationName);
                imageViewOrganizationPhoto = itemView.findViewById(R.id.imageViewOrganizationPhoto);
                textViewOrganizationVision = itemView.findViewById(R.id.textViewOrganizationVision);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            OrganizationModel selectedOrganization = organizations.get(position);
                            Intent detailIntent = new Intent(context, OrganizationDetailActivity.class);
                            detailIntent.putExtra("OrganizationDetail", selectedOrganization);
                            context.startActivity(detailIntent);
                        }
                    }
                });
            }
        }

    }
}