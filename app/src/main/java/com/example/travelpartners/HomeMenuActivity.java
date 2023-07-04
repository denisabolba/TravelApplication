package com.example.travelpartners;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.travelpartners.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView D1,D2,D3,D4,D5,D6;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        if(currentUser==null){
            startActivity(new Intent(HomeMenuActivity.this,LoginActivity.class));
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("accountsDeleted").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String deleted = snapshot.getValue(String.class);
                if (deleted != null && !deleted.isEmpty()) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = currentUser.getUid();
                    DatabaseReference userRef2 = FirebaseDatabase.getInstance().getReference()
                            .child("user")
                            .child(userId);
                    userRef2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Proceed to delete the user from Firebase Authentication
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // User deleted successfully
                                            Toast.makeText(HomeMenuActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth auth = FirebaseAuth.getInstance();
                                            auth.signOut();
                                            finish();
                                            startActivity(new Intent(HomeMenuActivity.this, LoginActivity.class));

                                        } else {
                                            // Error occurred while deleting the user
                                            Toast.makeText(HomeMenuActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        // Handle Home selection
                        Intent intent = new Intent(HomeMenuActivity.this, HomeMenuActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer after handling the item selection
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(HomeMenuActivity.this, HomeActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_trips:
                        startActivity(new Intent(HomeMenuActivity.this, MainActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_chat:
                        startActivity(new Intent(HomeMenuActivity.this, MainActivity2.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(HomeMenuActivity.this, ProfileActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_terms:
                        startActivity(new Intent(HomeMenuActivity.this, TermsActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_contact:
                        startActivity(new Intent(HomeMenuActivity.this, ContactSupportActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_logout:
                        Dialog dialog = new Dialog(HomeMenuActivity.this);
                        dialog.setContentView(R.layout.dialog_layout);
                        Button no, yes;
                        yes = dialog.findViewById(R.id.yesbnt);
                        no = dialog.findViewById(R.id.nobnt);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseAuth.getInstance().signOut();
                                finish();
                                Intent intent = new Intent(HomeMenuActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;

                }
                // Handle other menu item actions if required

                return false;
            }
        });


        D1 = (CardView) findViewById(R.id.d1);
        D2 = (CardView) findViewById(R.id.d2);
        D3 = (CardView) findViewById(R.id.d3);
        D4 = (CardView) findViewById(R.id.d4);
        D5 = (CardView) findViewById(R.id.d5);
        D6 = (CardView) findViewById(R.id.d6);

        D1.setOnClickListener((View.OnClickListener)this);
        D2.setOnClickListener((View.OnClickListener)this);
        D3.setOnClickListener((View.OnClickListener)this);
        D4.setOnClickListener((View.OnClickListener)this);
        D5.setOnClickListener((View.OnClickListener)this);
        D6.setOnClickListener((View.OnClickListener)this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.d1:
                i = new Intent(this,HomeActivity.class);
                startActivity(i);
                break;
            case R.id.d2:
                i = new Intent(this,MainActivity.class);
                startActivity(i);
                break;
            case R.id.d3:
                i = new Intent(this,MainActivity2.class);
                startActivity(i);
                break;
            case R.id.d4:
                i = new Intent(this,ProfileActivity.class);
                startActivity(i);
                break;
            case R.id.d5:
                i = new Intent(this,ContactSupportActivity.class);
                startActivity(i);
                break;
            case R.id.d6:
                i = new Intent(this,InformationActivity.class);
                startActivity(i);
                break;
        }
    }
}