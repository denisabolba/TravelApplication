package com.example.travelpartners;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.example.travelpartners.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setting extends AppCompatActivity {

    CircleImageView setprofile;
    EditText setname, setstatus,setcontact,setusername;
    Button donebut,delete_btn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri setImageUri;
    String email,password;
    String deleted = "";
    ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private  NavigationView navigationView;
    ImageView rightIcon,leftIcon;
    TextView toolbarTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


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
                        Intent intent = new Intent(Setting.this, HomeMenuActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer after handling the item selection
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(Setting.this, HomeActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_trips:
                        startActivity(new Intent(Setting.this, MainActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_chat:
                        startActivity(new Intent(Setting.this, MainActivity2.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(Setting.this, ProfileActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_terms:
                        startActivity(new Intent(Setting.this, TermsActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_contact:
                        startActivity(new Intent(Setting.this, ContactSupportActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_logout:
                        Dialog dialog = new Dialog(Setting.this);
                        dialog.setContentView(R.layout.dialog_layout);
                        Button no, yes;
                        yes = dialog.findViewById(R.id.yesbnt);
                        no = dialog.findViewById(R.id.nobnt);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(Setting.this,LoginActivity.class);
                                finish();
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


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        setprofile = findViewById(R.id.settingprofile);
        setname = findViewById(R.id.settingname);
        setstatus = findViewById(R.id.settingstatus);
        setcontact = findViewById(R.id.settingcontact);
        setusername = findViewById(R.id.settingusername);
        donebut = findViewById(R.id.donebutt);
        delete_btn = findViewById(R.id.delete_btn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);

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
                setname.setText(name);
                setstatus.setText(status);
                setcontact.setText(contact);
                setusername.setText(name);

                Picasso.get().load(profile).into(setprofile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        donebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                String name = setname.getText().toString();
                String Status = setstatus.getText().toString();
                String contact = setcontact.getText().toString();
                if (setImageUri!=null){
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri = uri.toString();

                                    Users users = new Users(auth.getUid(),name,email,password,contact,finalImageUri,Status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Toast.makeText(Setting.this, "Data Is saved ", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Setting.this,ProfileActivity.class);
                                                startActivity(intent);
                                                //finish();
                                            }else {
                                                progressDialog.dismiss();
                                                Toast.makeText(Setting.this, "Some thing went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }else {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri = uri.toString();
                            Users users = new Users(auth.getUid(), name,email,password,contact,finalImageUri,Status);
                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(Setting.this, "Data Is save ", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Setting.this,ProfileActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        progressDialog.dismiss();
                                        Toast.makeText(Setting.this, "Some thing went romg", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String userId = currentUser.getUid();
                // Replace with the actual user ID
                deleted = "true";
                DatabaseReference databaseReference = database.getReference().child("accountsDeleted").child(userId);
                databaseReference.setValue(deleted);


// Delete the user's data from the Realtime Database
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("notes")
                        .child(userId); // Adjust the path according to your database structure

                userRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // User's data deleted successfully


                        } else {
                            // Error occurred while deleting the user's data
                            Toast.makeText(Setting.this, "Failed to delete user's data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                DatabaseReference userRef2 = FirebaseDatabase.getInstance().getReference()
                        .child("user")
                        .child(userId);
                userRef2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Proceed to delete the user from Firebase Authentication
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // User deleted successfully
                                        Toast.makeText(Setting.this, "User deleted", Toast.LENGTH_SHORT).show();
                                        auth.signOut();
                                        startActivity(new Intent(Setting.this,LoginActivity.class));
                                        finish();
                                    } else {
                                        // Error occurred while deleting the user
                                        Toast.makeText(Setting.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data != null) {
                setImageUri = data.getData();
                setprofile.setImageURI(setImageUri);
            }
        }


    }
    private void sendEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userEmail = user.getEmail();

        String mEmail = "denisabolba27@gmail.com";
        String emailSubject = "DELETE user account";
        String emailBody = "Delete user account with email:" + userEmail;

        JavaMailAPI mailAPI = new JavaMailAPI(this, mEmail, emailSubject, emailBody);
        mailAPI.execute();
    }

    void showMenu(){
        PopupMenu popupMenu  = new PopupMenu(Setting.this,rightIcon);
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
                    startActivity(new Intent(Setting.this,HomeMenuActivity.class));
                    finish();
                    return true;
                }
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Setting.this,LoginActivity.class));
                    finish();
                    return true;
                }
                if(menuItem.getTitle()=="Profile"){
                    startActivity(new Intent(Setting.this,ProfileActivity.class));
                    return true;
                }
                if(menuItem.getTitle()=="Contact Support"){
                    startActivity(new Intent(Setting.this,ContactSupportActivity.class));
                    return true;
                }
                return false;
            }
        });

    }

}