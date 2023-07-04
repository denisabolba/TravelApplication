package com.example.travelpartners;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.travelpartners.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    EditText dataEditText;
    String datax;
    TextView setName,setEmail,pname,pusername,pstatus,pcontact;
    CircleImageView setPic;
    Button editBtn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri setImageUri;
    String email,password;

    ImageView rightIcon,leftIcon;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


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

        toolbarTitle.setText("Profile");

        editBtn = findViewById(R.id.edit_profile_button);
        setName = findViewById(R.id.profile_name);
        setEmail = findViewById(R.id.profile_email);
        setPic = findViewById(R.id.profile_image);
        pname = findViewById(R.id.profilename);
        pcontact = findViewById(R.id.profilecontact);
        pstatus = findViewById(R.id.profilestatus);
        pusername = findViewById(R.id.profileusername);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        DatabaseReference reference = database.getReference().child("user").child(auth.getCurrentUser().getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("mail").getValue().toString();
                password = snapshot.child("password").getValue().toString();
                String name = snapshot.child("userName").getValue().toString();
                String profile = snapshot.child("profilePic").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String contact = snapshot.child("contact").getValue().toString();
                setName.setText(name);
                setEmail.setText(email);
                pname.setText(name);
                pcontact.setText(contact);
                pusername.setText(name);
                pstatus.setText(status);



                Picasso.get().load(profile).into(setPic);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,Setting.class));
            }
        });

    }
    void showMenu(){
        PopupMenu popupMenu  = new PopupMenu(ProfileActivity.this,rightIcon);
        popupMenu.getMenu().add("Home");
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("Contact Support");
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Home"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(ProfileActivity.this,HomeMenuActivity.class));
                    finish();
                    return true;
                }
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                    finish();
                    return true;
                }
                if(menuItem.getTitle()=="Profile"){
                    startActivity(new Intent(ProfileActivity.this,ProfileActivity.class));
                    return true;
                }
                if(menuItem.getTitle()=="Contact Support"){
                    startActivity(new Intent(ProfileActivity.this,ContactSupportActivity.class));
                    return true;
                }
                return false;
            }
        });

    }

}
