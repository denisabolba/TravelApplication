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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;



public class InformationActivity extends AppCompatActivity {

    private CardView D1,D2,D3,D4,D5,D6,D7,D8;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

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
                        Intent intent = new Intent(InformationActivity.this, HomeMenuActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer after handling the item selection
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(InformationActivity.this, HomeActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_trips:
                        startActivity(new Intent(InformationActivity.this, MainActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_chat:
                        startActivity(new Intent(InformationActivity.this, MainActivity2.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(InformationActivity.this, ProfileActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_terms:
                        startActivity(new Intent(InformationActivity.this, TermsActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_contact:
                        startActivity(new Intent(InformationActivity.this, ContactSupportActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_logout:
                        Dialog dialog = new Dialog(InformationActivity.this);
                        dialog.setContentView(R.layout.dialog_layout);
                        Button no, yes;
                        yes = dialog.findViewById(R.id.yesbnt);
                        no = dialog.findViewById(R.id.nobnt);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseAuth.getInstance().signOut();
                                finish();
                                Intent intent = new Intent(InformationActivity.this,LoginActivity.class);
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
        D7 = (CardView) findViewById(R.id.d7);
        D8 = (CardView) findViewById(R.id.d8);

        D1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this,BarcelonaActivity.class));
            }
        });
        D2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this,CretaActivity.class));
            }
        });
        D3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this,EgyptActivity.class));
            }
        });
        D4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this,LondraActivity.class));
            }
        });
        D5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this,MaltaActivity.class));
            }
        });
        D6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this,ParisActivity.class));
            }
        });
        D7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this,RomaActivity.class));
            }
        });
        D8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InformationActivity.this,TenerifeActivity.class));
            }
        });

    }

}