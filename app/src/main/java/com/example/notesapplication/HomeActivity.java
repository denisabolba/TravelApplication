package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    EditText locationEditText, startDateEditText, endDateEditText;
    Button searchBtn;
    ImageButton sortBtn;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    NotePublicAdapter notePublicAdapter;
    ArrayList<Note> noteArrayList, filteredNoteList;
    ArrayList<String> noteIdList;
    String sortField; // Câmpul după care se va face sortarea
    boolean ascending; // true pentru sortare ascendentă, false pentru sortare descendentă


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        notePublicAdapter = new NotePublicAdapter(HomeActivity.this, noteArrayList, noteIdList);
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
        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, sortBtn);
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
}