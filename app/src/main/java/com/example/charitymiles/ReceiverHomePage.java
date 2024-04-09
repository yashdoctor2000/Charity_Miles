package com.example.charitymiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ReceiverHomePage extends AppCompatActivity {
    private AutoCompleteTextView spinnerPickUpTypes;
    private RecyclerView recyclerViewPickUps;
    private ReceiverHomePage.PickUpAdapter pickUpAdapter;
    private DatabaseReference databaseReference;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_home_page);
        spinnerPickUpTypes = findViewById(R.id.spinnerPickUpTypes);
        recyclerViewPickUps = findViewById(R.id.recyclerViewPickUps);
        navigationView=findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeSpinner();
        setupRecyclerView();

        if (mAuth.getCurrentUser() != null) {
            listenForNewPickupRequests();
        }

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
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(ReceiverHomePage.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }

                // Close the drawer after action
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void listenForNewPickupRequests() {
        databaseReference.child("Pick-UP-Request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String OrgId = snapshot.child("OrgId").getValue(String.class);
                    FirebaseUser fuser = mAuth.getCurrentUser();
                    if (fuser != null && fuser.getUid().equals(OrgId)) {
                        showNotification("New Pickup Request", "A new pickup request is available.");
                        break; // Optionally break after finding a relevant request
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "new_pickup_requests";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        // Intent that restarts the app when the notification is pressed
        Intent intent = new Intent(this, ReceiverHomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your own drawable
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setContentInfo("Info");

        notificationManager.notify(1, notificationBuilder.build());
    }

    private void initializeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pickup_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPickUpTypes.setAdapter(adapter);

        // Set the AutoCompleteTextView to show the first item as default
        if (adapter.getCount() > 0) {
            String defaultValue = adapter.getItem(0).toString();
            spinnerPickUpTypes.setText(defaultValue, false); // Prevents the dropdown from showing
            loadDonors(defaultValue); // Load organizations based on the default donation type
        }
        spinnerPickUpTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPickUpType = parent.getItemAtPosition(position).toString();
                // Load organizations based on the selected donation type
                loadDonors(selectedPickUpType);

            }

        });
    }

    private void loadDonors(String pickupTypes) {
        int activeType;
        if (pickupTypes.equals("Active Pickups")){
            activeType = 1;
        }
        else {
            activeType = 0;
        }

        databaseReference.child("Pick-UP-Request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ReceiverModel> receivers = new ArrayList<>();
                FirebaseUser fuser = mAuth.getCurrentUser();
                String userId = fuser.getUid();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the record is an organization and matches the donationType
                    String OrgId = snapshot.child("OrgId").getValue(String.class);
                    int status = snapshot.child("IsStatus").getValue(int.class);
                    if (userId.equals(OrgId) && activeType == status) {
                        ReceiverModel receiver = snapshot.getValue(ReceiverModel.class);
                        if (receiver != null) {
                            // Set the key (UID) of the organization data
                            receiver.setUid(snapshot.getKey());
                        }
                        receivers.add(receiver);
                    }
                }
                pickUpAdapter.setOrganizations(receivers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void setupRecyclerView() {
        recyclerViewPickUps.setLayoutManager(new LinearLayoutManager(this));
        pickUpAdapter = new PickUpAdapter(new ArrayList<>(), this);
        recyclerViewPickUps.setAdapter(pickUpAdapter);
    }

    public class PickUpAdapter extends RecyclerView.Adapter<ReceiverHomePage.PickUpAdapter.ViewHolder>{
        private List<ReceiverModel> receivers;
        private Context context;


        public PickUpAdapter(List<ReceiverModel> receivers, Context context) {
            this.receivers = receivers;
            this.context = context;
        }

        @NonNull

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donor_summary_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ReceiverModel receiver = receivers.get(position);
            holder.textViewDonorName.setText("Name: " + receiver.getDonorName());
            String text = "Items <b>" + receiver.getDonationQuantity() + "</b> requested for pickup.";
            holder.textViewNumberofItems.setText(Html.fromHtml(text));

        }


        @Override
        public int getItemCount() {
            return receivers.size();
        }
        public void setOrganizations(List<ReceiverModel> receivers) {
            this.receivers = receivers;
            notifyDataSetChanged();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewDonorName;
            TextView textViewNumberofItems;
            //ImageView imageViewOrganizationPhoto;

            ViewHolder(View itemView) {
                super(itemView);
//                super(itemView);
                textViewDonorName = itemView.findViewById(R.id.textViewDonorName);
                //imageViewOrganizationPhoto = itemView.findViewById(R.id.imageViewOrganizationPhoto);
                textViewNumberofItems = itemView.findViewById(R.id.textViewNumberofItems);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            ReceiverModel selectedReceiver = receivers.get(position);
                            Intent detailIntent = new Intent(context, PickUpDetailPage.class);
                            detailIntent.putExtra("PickUpDetails", selectedReceiver);
                            context.startActivity(detailIntent);
                        }
                    }
                });
            }
        }

    }
    
    
}