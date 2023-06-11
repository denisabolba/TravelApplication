package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NoteDetailsActivity extends AppCompatActivity {

    private static final String TAG = "GoogleMaps";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 1;
    EditText titleEditText, contentEditText, dateEditText, timeEditText, locationEditText,endDateEditText;
    ImageButton saveNoteButton, dateNoteButton, timeNoteButton, endDateNoteButton;
    Button btnNotification, btnSharePublic;
    TextView pageTitleTextView, deleteNoteTextViewBtn;
    String date, endDate, time, title, content, docId,nou, location,destination;
    boolean isEditMode = false, isNewMode = false;
    Calendar calendar = Calendar.getInstance();
    int currentHour = calendar.get(Calendar.HOUR);
    int currentMinute = calendar.get(Calendar.MINUTE);
    int currentYear = calendar.get(Calendar.YEAR);
    int currentMonth = calendar.get(Calendar.MONTH);
    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    final Calendar myCalendar = Calendar.getInstance();
    Timestamp timestamp;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    List<Users> userList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        endDateEditText = findViewById(R.id.notes_end_date_text);
        endDateNoteButton = findViewById(R.id.end_date_note_btn);
        timeEditText = findViewById(R.id.notes_time_text);
        timeNoteButton = findViewById(R.id.time_note_btn);
        btnNotification = findViewById(R.id.btnNotification);
        btnNotification = findViewById(R.id.btnNotification);
        locationEditText = findViewById(R.id.location_text);
        btnSharePublic = findViewById(R.id.sharePublic);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
            nou = getIntent().getStringExtra("nou");
            if(nou!=null && !nou.isEmpty()){
                isEditMode = false;
                isNewMode = true;
            }
        }

        if(isNewMode){
            DatabaseReference reference = database.getReference().child("notes").child(userId).child("my_notes");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    title = getIntent().getStringExtra("title");
                    content = getIntent().getStringExtra("content");
                    date = getIntent().getStringExtra("date");
                    endDate = getIntent().getStringExtra("endDate");
                    time = getIntent().getStringExtra("time");
                    location = getIntent().getStringExtra("location");

                    titleEditText.setText(title);
                    contentEditText.setText(content);
                    dateEditText.setText(date);
                    endDateEditText.setText(endDate);
                    timeEditText.setText(time);
                    locationEditText.setText(location);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (isEditMode) {
            pageTitleTextView.setText("Edit your trip");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);

            DatabaseReference reference = database.getReference().child("notes").child(userId).child("my_notes");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    title = getIntent().getStringExtra("title");
                    content = getIntent().getStringExtra("content");
                    date = getIntent().getStringExtra("date");
                    endDate = getIntent().getStringExtra("endDate");
                    time = getIntent().getStringExtra("time");
                    location = getIntent().getStringExtra("location");

                    titleEditText.setText(title);
                    contentEditText.setText(content);
                    dateEditText.setText(date);
                    endDateEditText.setText(endDate);
                    timeEditText.setText(time);
                    locationEditText.setText(location);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String noteTitle = titleEditText.getText().toString();
                String noteContent = contentEditText.getText().toString();
                String noteTime = timeEditText.getText().toString();
                String noteDate = dateEditText.getText().toString();
                String noteEndDate = endDateEditText.getText().toString();
                String formattedDate = String.format("%02d/%02d/%04d %02d:%02d", currentMonth, currentDay, currentYear, currentHour, currentMinute);
                String noteLocation = locationEditText.getText().toString();

                Note note = new Note(noteTitle, noteContent, noteDate, noteEndDate, noteTime, formattedDate, noteLocation);
                DatabaseReference reference;

                sendEmail(noteTitle,noteContent,isEditMode);


                if (isEditMode) {
                    String noteIdToUpdate = getIntent().getStringExtra("noteIdToUpdate");
                    reference = database.getReference().child("notes").child(userId).child("my_notes").child(noteIdToUpdate);

                    reference.setValue(note)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(NoteDetailsActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(NoteDetailsActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {

                    reference = database.getReference().child("notes").child(userId).child("my_notes");

                    reference.push().setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(NoteDetailsActivity.this, "success", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(NoteDetailsActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                }
            }

        });

        if (isEditMode) {
            pageTitleTextView.setText("Edit your trip");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
            btnSharePublic.setVisibility(View.VISIBLE);
            btnNotification.setVisibility(View.VISIBLE);
        }


        // saveNoteButton.setOnClickListener((v) -> saveNote());
        deleteNoteTextViewBtn.setOnClickListener((v) -> deleteNoteFromFirebase());


        dateNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });
        endDateNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseEndDate();
            }
        });
        timeNoteButton.setOnClickListener((v) -> chooseTime());


        String dateString = dateEditText.getText().toString();
        String timeString = timeEditText.getText().toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(NoteDetailsActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NoteDetailsActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference usersRef = database.getReference("user"); // Replace with the appropriate path to your users node

                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Users> userList = new ArrayList<>();

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Users users = userSnapshot.getValue(Users.class);
                            userList.add(users);
                        }

                        List<String> userNames = new ArrayList<>();
                        for (Users user : userList) {
                            userNames.add(user.getUserName()); // Replace getUserName() with the method to retrieve the user's name
                        }
                        String noteId = getIntent().getStringExtra("noteIdToUpdate");
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String sharerId = auth.getUid();

                        // Create an AlertDialog to display the user list
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NoteDetailsActivity.this);
                        dialogBuilder.setTitle("Select User");
                        dialogBuilder.setItems(userNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Users selectedUser = userList.get(which);
                                String selectedUserId = selectedUser.getUserId();
                                // Perform any action with the selected user

                                DatabaseReference notesRef = database.getReference("notes");
                                DatabaseReference user1SharedNotesRef = notesRef.child(selectedUserId).child("shared_notes");
                                user1SharedNotesRef.setValue(noteId + " " + userId);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                DatabaseReference sharedNote = database.getReference("notes").child(selectedUserId).child("my_notes");

                                DatabaseReference reference = database.getReference().child("notes").child(sharerId).child("my_notes").child(noteId);

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Retrieve the note data
                                        String sharedTitle = dataSnapshot.child("title").getValue(String.class);
                                        String sharedContent = dataSnapshot.child("content").getValue(String.class);
                                        String sharedDate = dataSnapshot.child("date").getValue(String.class);
                                        String sharedEndDate = dataSnapshot.child("endDate").getValue(String.class);
                                        String sharedTime = dataSnapshot.child("time").getValue(String.class);
                                        String sharedLocation = dataSnapshot.child("location").getValue(String.class);
                                        String formattedDate = String.format("%02d/%02d/%04d %02d:%02d", currentMonth, currentDay, currentYear, currentHour, currentMinute);

                                        sharedNote.child(noteId).child("title").setValue(sharedTitle);
                                        sharedNote.child(noteId).child("content").setValue(sharedContent);
                                        sharedNote.child(noteId).child("date").setValue(sharedDate);
                                        sharedNote.child(noteId).child("endDate").setValue(sharedEndDate);
                                        sharedNote.child(noteId).child("time").setValue(sharedTime);
                                        sharedNote.child(noteId).child("location").setValue(sharedLocation);
                                        sharedNote.child(noteId).child("currentDate").setValue(formattedDate);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Handle onCancelled
                                    }
                                });



                            }
                        });
                        dialogBuilder.show();

                        // Do something with the userList containing all users
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error
                    }
                });





            }

        });

        btnSharePublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String noteId = getIntent().getStringExtra("noteIdToUpdate");
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String sharerId = auth.getUid();

                DatabaseReference sharedNotePublic = database.getReference("publicNotes");

                DatabaseReference reference = database.getReference().child("notes").child(sharerId).child("my_notes").child(noteId);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Retrieve the note data
                        String sharedTitle = dataSnapshot.child("title").getValue(String.class);
                        String sharedContent = dataSnapshot.child("content").getValue(String.class);
                        String sharedDate = dataSnapshot.child("date").getValue(String.class);
                        String sharedTime = dataSnapshot.child("time").getValue(String.class);
                        String sharedEndDate = dataSnapshot.child("endDate").getValue(String.class);
                        String sharedLocation = dataSnapshot.child("location").getValue(String.class);
                        String formattedDate = String.format("%02d/%02d/%04d %02d:%02d", currentMonth, currentDay, currentYear, currentHour, currentMinute);

                        sharedNotePublic.child(noteId).child("title").setValue(sharedTitle);
                        sharedNotePublic.child(noteId).child("content").setValue(sharedContent);
                        sharedNotePublic.child(noteId).child("date").setValue(sharedDate);
                        sharedNotePublic.child(noteId).child("endDate").setValue(sharedEndDate);
                        sharedNotePublic.child(noteId).child("time").setValue(sharedTime);
                        sharedNotePublic.child(noteId).child("location").setValue(sharedLocation);
                        sharedNotePublic.child(noteId).child("currentDate").setValue(formattedDate);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle onCancelled
                    }
                });

            }
        });
    }

    public void scheduleNotification() {
        String desiredDate = dateEditText.getText().toString(); // Replace 'editText' with the ID of your EditText

        // Convert the desired date to a timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        try {
            Date desiredDateTime = dateFormat.parse(desiredDate);

            Calendar desiredCalendar = Calendar.getInstance();
            desiredCalendar.setTime(desiredDateTime);

            // Get the current date and time
            Calendar currentCalendar = Calendar.getInstance();

            SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            Log.d("Notification", "Current Date: " + logDateFormat.format(currentCalendar.getTime()));
            Log.d("Notification", "Desired Date: " + logDateFormat.format(desiredCalendar.getTime()));

            // Compare the current date with the desired date
            if (currentCalendar.get(Calendar.YEAR) == desiredCalendar.get(Calendar.YEAR)
                    && currentCalendar.get(Calendar.MONTH) == desiredCalendar.get(Calendar.MONTH)
                    && currentCalendar.get(Calendar.DAY_OF_MONTH) == desiredCalendar.get(Calendar.DAY_OF_MONTH)) {

                // Set the desired time component
                desiredCalendar.set(Calendar.HOUR_OF_DAY, 15); // Set to 2:00 PM
                desiredCalendar.set(Calendar.MINUTE, 16);
                desiredCalendar.set(Calendar.SECOND, 0);
                desiredCalendar.set(Calendar.MILLISECOND, 0);




                // Get the modified time as the trigger time
                long triggerTime = desiredCalendar.getTimeInMillis();

                // Get the modified time as the trigger time
                //long triggerTime = calendar.getTimeInMillis();

                // Create an intent for the broadcast receiver
                Intent intent = new Intent(this, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

                // Schedule the notification using AlarmManager
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            }
            } catch(ParseException e){
                e.printStackTrace();
            }

    }

    public void makeNotification() throws ParseException {

        String dateStr = dateEditText.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/dd");

            Date date = dateFormat.parse(dateStr);

            // Extract year, month, and day from the parsed date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Add 1 because months start from 0
            int day = calendar.get(Calendar.DAY_OF_MONTH);



        if (currentDay==day && currentMonth==month && currentYear==year)
        {
            String channelID = "CHANNEL_ID_NOTIFICATION";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                    .setSmallIcon(android.R.drawable.stat_notify_sync)
                    .setContentTitle("First Notification")
                    .setContentText("This is the body of the notification")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("data", "Some value to be passed here");

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
                if (notificationChannel == null) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    notificationChannel = new NotificationChannel(channelID, "Some description", importance);
                    notificationChannel.setLightColor(Color.GREEN);
                    notificationChannel.enableVibration(true);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }
            notificationManager.notify(0, builder.build());
        }
    }

    private void chooseTime() {
        TimePickerDialog dialog = new TimePickerDialog(this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {


                myCalendar.set(Calendar.HOUR_OF_DAY, hours);
                myCalendar.set(Calendar.MINUTE, minutes);
                timeEditText.setText(String.valueOf(hours) + ":" + String.valueOf(minutes));

            }
        }, currentHour, currentMinute, true);
        dialog.show();
    }

    private void chooseDate() {
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateEditText.setText(String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(dayOfMonth));

            }
        }, currentYear, currentMonth, currentDay);
        dialog.show();
    }
    private void chooseEndDate() {
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDateEditText.setText(String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(dayOfMonth));

            }
        }, currentYear, currentMonth, currentDay);
        dialog.show();
    }

    private void init() {
        ImageButton btnMap = (ImageButton) findViewById(R.id.btnMap);
        ImageButton btnDirection = (ImageButton) findViewById(R.id.btnDirection);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteDetailsActivity.this, GoogleMaps.class);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
                //startActivity(intent);
                //latitudeEditText.setText(getIntent().getStringExtra("key"));
            }
        });

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destination = locationEditText.getText().toString().trim();
                if (!destination.isEmpty()) {
                    getLastKnownLocation(destination);
                } else {
                    Toast.makeText(NoteDetailsActivity.this, "Please enter a destination", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void requestLocationUpdates() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String currentLatitude = String.valueOf(latitude);
                    String currentLongitude = String.valueOf(longitude);
                    openDirectionsInGoogleMaps(currentLatitude, currentLongitude, destination);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation(String destination) {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String currentLatitude = String.valueOf(latitude);
                            String currentLongitude = String.valueOf(longitude);
                            openDirectionsInGoogleMaps(currentLatitude, currentLongitude, destination);
                        } else {
                            Toast.makeText(NoteDetailsActivity.this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openDirectionsInGoogleMaps(String currentLatitude, String currentLongitude, String destination) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination + "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps app is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Data successfully returned from the second activity
                String value = data.getStringExtra("key"); // Retrieve the data from the intent extras
                locationEditText.setText(value);

                // Use the data as needed
            } else if (resultCode == RESULT_CANCELED) {
                // Handle if the second activity was canceled or no data was returned
            }
        }
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

        String noteIdToUpdate = getIntent().getStringExtra("noteIdToUpdate");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        DatabaseReference noteRef = FirebaseDatabase.getInstance().getReference()
                .child("notes")
                .child(userId) // Assuming you have the user ID stored in a variable
                .child("my_notes")
                .child(noteIdToUpdate);

// Remove the note from the database
        noteRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Note deletion successful
                    Toast.makeText(NoteDetailsActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Error occurred while deleting the note
                    Toast.makeText(NoteDetailsActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendEmail(String title,String content,Boolean isEditMode){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userEmail = user.getEmail();

        String recipientEmail = userEmail;
        String emailSubject;
        if(isEditMode){
            emailSubject = "A new note has been created";
        }
        else {
            emailSubject = "A note has been edited";
        }
        String emailBody = "Title: " + title + "\n" + "Content: " + content;

        JavaMailAPI mailAPI = new JavaMailAPI(this, recipientEmail, emailSubject, emailBody);
        mailAPI.execute();
    }
}