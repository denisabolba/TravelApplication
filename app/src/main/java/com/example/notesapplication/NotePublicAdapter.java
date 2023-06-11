package com.example.notesapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotePublicAdapter extends RecyclerView.Adapter<NotePublicAdapter.viewholder> {
    Context HomeActivity;
    ArrayList<Note> noteArrayList;
    ArrayList<String> noteIdList;
    String sharerId;
    public NotePublicAdapter(Activity HomeActivity, ArrayList<Note> noteArrayList, ArrayList<String> noteIdList) {
        this.HomeActivity=HomeActivity;
        this.noteArrayList=noteArrayList;
        this.noteIdList = noteIdList;
    }

    @NonNull
    @Override
    public NotePublicAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(HomeActivity).inflate(R.layout.custom_grid_layout,parent,false);
        return new NotePublicAdapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotePublicAdapter.viewholder holder, int position) {

        Note note = noteArrayList.get(position);
        String noteIdToUpdate = noteIdList.get(position);

        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.timestampText.setText(note.getCurrentDate());
        holder.location.setText(note.getLocation());
        holder.startDate.setText(note.getDate());
        holder.endDate.setText(note.getEndDate());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String docId = auth.getCurrentUser().getUid();
                String nou = "nou";

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference("publicNotes").child(noteIdToUpdate).child("sharerId");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Verificați dacă există date valide
                        if (dataSnapshot.exists()) {
                             sharerId = dataSnapshot.getValue(String.class);
                            // Utilizați valoarea sharerId în codul dvs.
                            // ...


                        } else {
                            // Documentul nu există sau câmpul sharerId este gol
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Tratați eroarea în cazul în care citirea datelor a eșuat
                    }
                });

                Intent intent = new Intent(HomeActivity, NoteDetailsActivity.class);
                intent.putExtra("title",note.getTitle());
                intent.putExtra("content",note.getContent());
                intent.putExtra("docId",docId);
                intent.putExtra("nou",nou);
                intent.putExtra("date",note.getDate());
                intent.putExtra("time",note.getTime());
                intent.putExtra("location",note.getLocation());
                intent.putExtra("sharerId",note.getSharerId());
                intent.putExtra("noteIdToUpdate", noteIdToUpdate); // Pass the noteIdToUpdate as an extr

                HomeActivity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return noteArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView title,content,timestampText,startDate,endDate,location;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title_text_view);
            content = itemView.findViewById(R.id.note_content_text_view);
            timestampText = itemView.findViewById(R.id.note_timestamp_text_view);
            location = itemView.findViewById(R.id.note_location_text_view);
            startDate = itemView.findViewById(R.id.note_startDate_text_view);
            endDate = itemView.findViewById(R.id.note_endDate_text_view);
            }
    }


}