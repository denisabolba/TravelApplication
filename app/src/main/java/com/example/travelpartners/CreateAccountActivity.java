package com.example.travelpartners;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelpartners.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText,confirmPasswordEditText,contactEditText,usernameEditText;
    Button createAccountBtn,saveDataBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;
    CircleImageView profilePic;

    Uri imageURI;
    String imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText =findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);
        contactEditText = findViewById(R.id.contact_edit_text);
        usernameEditText = findViewById(R.id.username_edit_text);
        //saveDataBtn = findViewById(R.id.save_data_btn);
        profilePic = findViewById(R.id.profile_pic);

        //receive data
        //data = getIntent().getStringExtra("data");

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });



        createAccountBtn.setOnClickListener(v ->createAccount());
        loginBtnTextView.setOnClickListener(v ->finish());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            if(data!=null){
                imageURI = data.getData();
                profilePic.setImageURI(imageURI);

            }
        }
    }

     void createAccount() {

        String name = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String contact = contactEditText.getText().toString();
        String status = "Hey I'm Using This Application";

        boolean isValidated = validateData(email,password,confirmPassword);
        if(!isValidated){
            return;
        }

        createAccountInFirebase(name,email,password,contact,status);
    }

    private void createAccountInFirebase(String name,String email, String password,String contact,String status) {
        changeInProgress(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    String id = task.getResult().getUser().getUid();
                    DatabaseReference reference = database.getReference().child("user").child(id);
                    StorageReference storageReference = storage.getReference().child("Upload").child(id);


                    if(imageURI!=null){
                        storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                                        @Override
                                        public void onSuccess(Uri uri){
                                            imageuri = uri.toString();
                                            Users users = new Users(id,name,email,password,contact,imageuri,status);
                                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Intent intent = new Intent(CreateAccountActivity.this,MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }else{
                                                        Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                            });
                                        }
                                    });
                                }else {
                                    Toast.makeText(CreateAccountActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        String status = "Hey I'm Using This Application";
                        imageuri ="https://firebasestorage.googleapis.com/v0/b/login-register-ce433.appspot.com/o/login.png?alt=media&token=cc764c98-bdc4-4270-88df-dec703200e37";
                        Users users = new Users(id,name,email,password,contact,imageuri,status);

                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (user != null) {
                                        user.sendEmailVerification();
                                    }

                                    Intent intent = new Intent(CreateAccountActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    firebaseAuth.signOut();
                                }else{
                                    Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }

                    Toast.makeText(CreateAccountActivity.this, "Successfully create account, check email to verify", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid!");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("Password length needs to be at least 6!");
            return false;
        }
        if(!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Password and Confirmation password do not mach!");
            return false;
        }
         return true;
     }
}