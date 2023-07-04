package com.example.travelpartners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.travelpartners.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.viewholder> {
    Context mainActivity;
    ArrayList<Note> noteArrayList;
    ArrayList<String> noteIdList;
    public NoteAdapter(Activity mainActivity, ArrayList<Note> noteArrayList,ArrayList<String> noteIdList) {
        this.mainActivity=mainActivity;
        this.noteArrayList=noteArrayList;
        this.noteIdList = noteIdList;
    }

    @NonNull
    @Override
    public NoteAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.recycler_note_item,parent,false);
        return new NoteAdapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.viewholder holder, int position) {

        Note note = noteArrayList.get(position);
        String noteIdToUpdate = noteIdList.get(position);

        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.timestampText.setText(note.getCurrentDate());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String docId = auth.getCurrentUser().getUid();

                Intent intent = new Intent(mainActivity, NoteDetailsActivity.class);
                intent.putExtra("title",note.getTitle());
                intent.putExtra("content",note.getContent());
                intent.putExtra("docId",docId);
                intent.putExtra("date",note.getDate());
                intent.putExtra("time",note.getTime());
                intent.putExtra("endDate",note.getEndDate());
                intent.putExtra("location",note.getLocation());
                intent.putExtra("noteIdToUpdate", noteIdToUpdate); // Pass the noteIdToUpdate as an extr

                mainActivity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return noteArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView title,content,timestampText;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title_text_view);
            content = itemView.findViewById(R.id.note_content_text_view);
            timestampText = itemView.findViewById(R.id.note_timestamp_text_view);
            }
    }
}