package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList, originalArrayList;
    ImageView imglogout,leftIcon,rightIcon;
    TextView title;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private  NavigationView navigationView;
    ArrayList<Users> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        leftIcon = findViewById(R.id.left_icon);
        rightIcon = findViewById(R.id.right_icon);
        title = findViewById(R.id.toolbar_title);

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
        title.setText("Chat");


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
                        Intent intent = new Intent(MainActivity2.this, HomeMenuActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer after handling the item selection
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(MainActivity2.this, HomeActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_trips:
                        startActivity(new Intent(MainActivity2.this, MainActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_chat:
                        startActivity(new Intent(MainActivity2.this, MainActivity2.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(MainActivity2.this, ProfileActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_terms:
                        startActivity(new Intent(MainActivity2.this, TermsActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_logout:
                        Dialog dialog = new Dialog(MainActivity2.this);
                        dialog.setContentView(R.layout.dialog_layout);
                        Button no, yes;
                        yes = dialog.findViewById(R.id.yesbnt);
                        no = dialog.findViewById(R.id.nobnt);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(MainActivity2.this,LoginActivity.class);
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
                    case R.id.nav_contact:
                        startActivity(new Intent(MainActivity2.this, ContactSupportActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                }
                // Handle other menu item actions if required

                return false;
            }
        });


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        DatabaseReference reference = database.getReference().child("user");


        usersArrayList = new ArrayList<>();
        filteredList = new ArrayList<>();
        originalArrayList = new ArrayList<>();

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.requestFocus();
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(MainActivity2.this,usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search logic here
                filterUsers(newText);
                return true;
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                originalArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    if(!users.userId.equals(auth.getCurrentUser().getUid())) {
                        usersArrayList.add(users);
                        originalArrayList.add(users);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        imglogout = findViewById(R.id.logoutimg);

        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity2.this);
                dialog.setContentView(R.layout.dialog_layout);
                Button no, yes;
                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity2.this,LoginActivity.class);
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
            }
        });


        if (auth.getCurrentUser()==null){
            Intent intent = new Intent(MainActivity2.this,LoginActivity.class);
            startActivity(intent);
        }
    }
    private void filterUsers(String searchText) {
        usersArrayList.clear();
        if (searchText.isEmpty()) {
            usersArrayList.addAll(originalArrayList);
        } else {
            searchText = searchText.toLowerCase(Locale.getDefault());
            for (Users user : originalArrayList) {
                if (user.getUserName().toLowerCase(Locale.getDefault()).contains(searchText)) {
                    usersArrayList.add(user);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity2.this, rightIcon);
        popupMenu.getMenu().add("Home");
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("Contact Support");
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle() == "Home") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity2.this, HomeMenuActivity.class));
                    finish();
                    return true;
                }
                if (menuItem.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity2.this, LoginActivity.class));
                    finish();
                    return true;
                }
                if (menuItem.getTitle() == "Profile") {
                    startActivity(new Intent(MainActivity2.this, ProfileActivity.class));
                    return true;
                }
                if (menuItem.getTitle() == "Contact Support") {
                    startActivity(new Intent(MainActivity2.this, ContactSupportActivity.class));
                    return true;
                }
                return false;
            }
        });

    }
}