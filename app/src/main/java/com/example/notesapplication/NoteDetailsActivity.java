package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

import java.util.Calendar;

public class NoteDetailsActivity extends AppCompatActivity {

    private static final String TAG = "GoogleMaps";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    EditText titleEditText, contentEditText, dateEditText, timeEditText;
    ImageButton saveNoteButton, dateNoteButton, timeNoteButton;
    Button btnNotification;
    TextView pageTitleTextView, deleteNoteTextViewBtn;
    String date, time, title, content, docId;
    boolean isEditMode = false;
    Calendar calendar = Calendar.getInstance();
    int currentHour = calendar.get(Calendar.HOUR);
    int currentMinute = calendar.get(Calendar.MINUTE);
    int currentYear = calendar.get(Calendar.YEAR);
    int currentMonth = calendar.get(Calendar.MONTH);
    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    final Calendar myCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        if (isServicesOK()) {
            init();
        }

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteButton = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);
        dateEditText = findViewById(R.id.notes_date_text);
        dateNoteButton = findViewById(R.id.date_note_btn);
        timeEditText = findViewById(R.id.notes_time_text);
        timeNoteButton = findViewById(R.id.time_note_btn);
        btnNotification = findViewById(R.id.btnNotification);

        //receive data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        dateEditText.setText(date);
        timeEditText.setText(time);

        if (isEditMode) {
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }


        saveNoteButton.setOnClickListener((v) -> saveNote());
        deleteNoteTextViewBtn.setOnClickListener((v) -> deleteNoteFromFirebase());


        dateNoteButton.setOnClickListener((v) -> chooseDate());
        timeNoteButton.setOnClickListener((v) -> chooseTime());


        String dateString = dateEditText.getText().toString();
        String timeString = timeEditText.getText().toString();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(NoteDetailsActivity.this, Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(NoteDetailsActivity.this,new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNotification();
            }
        });
    }

    public void makeNotification(){
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentTitle("First Notification")
                .setContentText("This is the body of the notification")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(),NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data","Some value to be passed here");

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
            if(notificationChannel == null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID,"Some description",importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0,builder.build());
    }
    private void chooseTime() {
        TimePickerDialog dialog = new TimePickerDialog(this,R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {


                myCalendar.set(Calendar.HOUR_OF_DAY, hours);
                myCalendar.set(Calendar.MINUTE, minutes);
                timeEditText.setText(String.valueOf(hours)+":"+String.valueOf(minutes));

            }
        }, currentHour, currentMinute, true);
        dialog.show();
    }

    private void chooseDate() {
         DatePickerDialog dialog = new DatePickerDialog(this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
             @Override
             public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                 myCalendar.set(Calendar.YEAR, year);
                 myCalendar.set(Calendar.MONTH, month);
                 myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                 dateEditText.setText(String.valueOf(year)+"/"+String.valueOf(month)+"/"+String.valueOf(dayOfMonth));

             }
         }, currentYear, currentMonth, currentDay);
         dialog.show();
    }

    private void init(){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteDetailsActivity.this,GoogleMaps.class);
                startActivity(intent);
            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG,"isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(NoteDetailsActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make requests
            Log.d(TAG,"isServicesOK: Google Play Services is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG,"isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(NoteDetailsActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    void deleteNoteFromFirebase() {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);


        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed while deleting note");

                }
            }
        });
    }

    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        String noteTime = timeEditText.getText().toString();
        String noteDate = dateEditText.getText().toString();

        if(noteTitle == null || noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setDate(noteDate);
        note.setTime(noteTime);
        note.setTimestamp(Timestamp.now());


        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();

        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsActivity.this,"Note add successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed while adding note");

                }
            }
        });
    }
}