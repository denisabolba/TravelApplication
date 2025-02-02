package com.example.travelpartners;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelpartners.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {

    String reciverimg, reciverUid, reciverName, SenderUID;
    CircleImageView profile;
    TextView reciverNName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static String senderImg;
    public static String reciverIImg;
    CardView sendbtn;
    EditText textmsg;

    String senderRoom, reciverRoom;
    RecyclerView messageAdpter;
    ArrayList<msgModelclass> messagesArrayList;
    messagesAdapter mmessagesAdpter;
    ImageView rightIcon, leftIcon;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);


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


        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentU = FirebaseAuth.getInstance().getCurrentUser();
        String uId = currentU.getUid();

        reciverName = getIntent().getStringExtra("name");
        reciverimg = getIntent().getStringExtra("receiverImg");
        reciverUid = getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        reciverNName = findViewById(R.id.recivername);
        profile = findViewById(R.id.profileimgg);
        messageAdpter = findViewById(R.id.msgadpter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdpter.setLayoutManager(linearLayoutManager);
        mmessagesAdpter = new messagesAdapter(chatWin.this, messagesArrayList);
        messageAdpter.setAdapter(mmessagesAdpter);


        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText("" + reciverName);

        SenderUID = uId;
        senderRoom = SenderUID + reciverUid;
        reciverRoom = reciverUid + SenderUID;


        DatabaseReference reference = database.getReference().child("user").child(uId);
        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");


        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    messagesArrayList.add(messages);
                }
                mmessagesAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilePic").getValue().toString();
                reciverIImg = reciverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textmsg.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(chatWin.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                    return;
                }
                textmsg.setText("");
                Date date = new Date();
                msgModelclass messagess = new msgModelclass(message, SenderUID, date.getTime());

                database = FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(reciverRoom)
                                        .child("messages")
                                        .push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });
            }
        });

    }

    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(chatWin.this, rightIcon);
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
                    startActivity(new Intent(chatWin.this, HomeMenuActivity.class));
                    finish();
                    return true;
                }
                if (menuItem.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(chatWin.this, LoginActivity.class));
                    finish();
                    return true;
                }
                if (menuItem.getTitle() == "Profile") {
                    startActivity(new Intent(chatWin.this, ProfileActivity.class));
                    return true;
                }
                if (menuItem.getTitle() == "Contact Support") {
                    startActivity(new Intent(chatWin.this, ContactSupportActivity.class));
                    return true;
                }
                return false;
            }
        });

    }
}