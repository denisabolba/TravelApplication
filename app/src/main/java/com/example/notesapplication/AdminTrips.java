package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminTrips extends AppCompatActivity {

    EditText locationEditText, startDateEditText, endDateEditText;
    Button searchBtn;
    ImageButton sortBtn;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AdminTripAdapter notePublicAdapter;
    ArrayList<Note> noteArrayList, filteredNoteList;
    ArrayList<String> noteIdList;
    String sortField; // Câmpul după care se va face sortarea
    boolean ascending; // true pentru sortare ascendentă, false pentru sortare descendentă
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    ImageView rightIcon,leftIcon;
    TextView toolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_trips);;

        leftIcon = findViewById(R.id.left_icon);
        rightIcon = findViewById(R.id.right_icon);
        toolbarTitle = findViewById(R.id.toolbar_title);

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // showMenu2();
            }
        });

        toolbarTitle.setText("Search Trips");

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
                        Intent intent = new Intent(AdminTrips.this, HomeMenuActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer after handling the item selection
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(AdminTrips.this, HomeActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_trips:
                        startActivity(new Intent(AdminTrips.this, MainActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_chat:
                        startActivity(new Intent(AdminTrips.this, MainActivity2.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(AdminTrips.this, ProfileActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_terms:
                        startActivity(new Intent(AdminTrips.this, TermsActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_contact:
                        startActivity(new Intent(AdminTrips.this, ContactSupportActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_logout:
                        Dialog dialog = new Dialog(AdminTrips.this);
                        dialog.setContentView(R.layout.dialog_layout);
                        Button no, yes;
                        yes = dialog.findViewById(R.id.yesbnt);
                        no = dialog.findViewById(R.id.nobnt);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(AdminTrips.this, LoginActivity.class);
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

        locationEditText = findViewById(R.id.destination_edit_text);
        startDateEditText = findViewById(R.id.start_date_edit_text);
        endDateEditText = findViewById(R.id.end_date_edit_text);
        searchBtn = findViewById(R.id.search_button);
        sortBtn = findViewById(R.id.sort_btn);

        noteArrayList = new ArrayList<>();
        filteredNoteList = new ArrayList<>();
        noteIdList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        notePublicAdapter = new AdminTripAdapter(AdminTrips.this, noteArrayList, noteIdList);
        recyclerView.setAdapter(notePublicAdapter);


        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sortează lista de obiecte Note utilizând expresie lambda
                showMenu();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                filteredNoteList.clear();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

                String locationFilter = locationEditText.getText().toString().trim();

                for (Note note : noteArrayList) {

                    String startDateText = startDateEditText.getText().toString();
                    Date startDate = null;
                    if (!startDateText.equals("")) {
                        try {
                            startDate = dateFormat.parse(startDateText);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    String endDateText = endDateEditText.getText().toString();
                    Date endDate = null;
                    if (!endDateText.equals("")) {
                        try {
                            endDate = dateFormat.parse(endDateText);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    String noteDateText = note.getDate();
                    Date noteDate = null;
                    try {
                        noteDate = dateFormat.parse(noteDateText);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    String noteEndDateText = note.getEndDate();
                    Date noteEndDate = null;
                    try {
                        noteEndDate = dateFormat.parse(noteEndDateText);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    if (startDate != null || endDate != null) {

                        if (endDate != null && startDate == null) {
                            if (noteDate.before(endDate)) {
                                filteredNoteList.add(note);
                            }
                        } else {
                            if (endDate == null && startDate != null) {
                                if (noteDate.after(startDate)) {
                                    filteredNoteList.add(note);
                                }
                            } else {
                                if (noteDate.after(startDate) && noteEndDate.before(endDate)) {
                                    filteredNoteList.add(note);
                                }
                            }
                        }
                    }

                    if(note.getLocation().equals(locationFilter)){
                        filteredNoteList.add(note);
                    }

                }
                notePublicAdapter.noteArrayList = filteredNoteList;
                notePublicAdapter.notifyDataSetChanged();
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        DatabaseReference notesRef = database.getReference().child("publicNotes");

        notesRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteArrayList.clear();
                noteIdList.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    String noteIdToUpdate = noteSnapshot.getKey();
                    noteIdList.add(noteIdToUpdate);

                    Note note = noteSnapshot.getValue(Note.class);
                    noteArrayList.add(note);
                }

                notePublicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });

    }

    void sortare(String theSortField, boolean theAscending) {
        noteArrayList.sort((note1, note2) -> {
            int result;
            switch (theSortField) {
                case "location":
                    result = note1.getTitle().compareTo(note2.getTitle());
                    break;
                case "date":
                    result = note1.getDate().compareTo(note2.getDate());
                    break;
                case "title":
                    result = note1.getDate().compareTo(note2.getTitle());
                    break;
                default:
                    throw new IllegalArgumentException("Câmpul specificat nu este valid.");
            }

            // Inversează rezultatul dacă se dorește sortare descrescătoare
            if (!theAscending) {
                result = -result;
            }

            return result;
        });

        // Actualizează afișarea RecyclerView-ului
        notePublicAdapter.notifyDataSetChanged();
    }

    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(AdminTrips.this, sortBtn);
        popupMenu.getMenu().add("Ascending by Location");
        popupMenu.getMenu().add("Ascending by Title");
        popupMenu.getMenu().add("Ascending by StartDate");
        popupMenu.getMenu().add("Descending by Location");
        popupMenu.getMenu().add("Descending by Title");
        popupMenu.getMenu().add("Descending by StartDate");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle() == "Ascending by Location") {
                    sortField = "location";
                    ascending = true;
                    sortare(sortField,ascending);
                    return true;
                }
                if (menuItem.getTitle() =="Ascending by Title") {
                    sortField = "title";
                    ascending = true;
                    sortare(sortField,ascending);
                    return true;
                }
                if (menuItem.getTitle() =="Ascending by StartDate") {
                    sortField = "date";
                    ascending = true;
                    sortare(sortField,ascending);
                    return true;
                }
                if (menuItem.getTitle() == "Descending by Location") {
                    sortField = "location";
                    ascending = false;
                    sortare(sortField,ascending);
                    return true;
                }
                if (menuItem.getTitle() =="Descending by Title") {
                    sortField = "title";
                    ascending = false;
                    sortare(sortField,ascending);
                    return true;
                }
                if (menuItem.getTitle() =="Descending by StartDate") {
                    sortField = "date";
                    ascending = false;
                    sortare(sortField,ascending);
                    return true;
                }

                return false;
            }
        });
    }
    void showMenu2() {
        PopupMenu popupMenu = new PopupMenu(AdminTrips.this, rightIcon);
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
                    startActivity(new Intent(AdminTrips.this, HomeMenuActivity.class));
                    finish();
                    return true;
                }
                if (menuItem.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(AdminTrips.this, LoginActivity.class));
                    finish();
                    return true;
                }
                if (menuItem.getTitle() == "Profile") {
                    startActivity(new Intent(AdminTrips.this, ProfileActivity.class));
                    return true;
                }
                if (menuItem.getTitle() == "Contact Support") {
                    startActivity(new Intent(AdminTrips.this, ContactSupportActivity.class));
                    return true;
                }
                return false;
            }
        });

    }
}
