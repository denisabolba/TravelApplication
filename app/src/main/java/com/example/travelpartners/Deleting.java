package com.example.travelpartners;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.travelpartners.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Deleting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleting);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("publicNotes");
        String noteIdToUpdate = getIntent().getStringExtra("noteIdToUpdate");
        usersRef.child(noteIdToUpdate).setValue(null);
        finish();
    }
}