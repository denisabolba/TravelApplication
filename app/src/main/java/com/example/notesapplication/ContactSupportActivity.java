package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ContactSupportActivity extends AppCompatActivity {

    EditText adminEmail, email, subject, message;
    Button button;

    FirebaseAuth auth;
    FirebaseDatabase database;
    ImageView rightIcon, leftIcon;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support);


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
                showMenu();
            }
        });


        adminEmail = findViewById(R.id.admin_email);
        email = findViewById(R.id.email);
        subject = findViewById(R.id.subject);
        message = findViewById(R.id.message);
        button = findViewById(R.id.btn);

        adminEmail.setEnabled(false);
        email.setEnabled(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("user").child(auth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userEmail = snapshot.child("mail").getValue().toString();
                email.setText(userEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
                Toast.makeText(ContactSupportActivity.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
                finish();
                //startActivity(new Intent(ContactSupportActivity.this,MainActivity.class));
            }
        });
    }

    private void sendEmail() {
        String mEmail = "denisabolba27@gmail.com";
        String mSubject = subject.getText().toString();
        String mUserMessage = message.getText().toString();
        String mUser = email.getText().toString();

        String mMessage = "User: " + mUser + "\n" + "User's message: " + "\n" + mUserMessage;


        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mEmail, mSubject, mMessage);

        javaMailAPI.execute();
    }
    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(ContactSupportActivity.this, rightIcon);
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
                    startActivity(new Intent(ContactSupportActivity.this, HomeMenuActivity.class));
                    finish();
                    return true;
                }
                if (menuItem.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(ContactSupportActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                if (menuItem.getTitle() == "Profile") {
                    startActivity(new Intent(ContactSupportActivity.this, ProfileActivity.class));
                    return true;
                }
                if (menuItem.getTitle() == "Contact Support") {
                    startActivity(new Intent(ContactSupportActivity.this, ContactSupportActivity.class));
                    return true;
                }
                return false;
            }
        });

    }
}