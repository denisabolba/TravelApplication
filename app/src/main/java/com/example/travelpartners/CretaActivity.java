package com.example.travelpartners;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.travelpartners.R;
import com.google.firebase.auth.FirebaseAuth;

public class CretaActivity extends AppCompatActivity {

    WebView webView;
    ImageView rightIcon,leftIcon;
    TextView toolbarTitle;
    public String fileName = "HTML/Creta.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creta);

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


        webView = (WebView) findViewById(R.id.terms_id);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/" + fileName);
    }

    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(CretaActivity.this, rightIcon);
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
                    startActivity(new Intent(CretaActivity.this, HomeMenuActivity.class));
                    finish();
                    return true;
                }
                if (menuItem.getTitle() == "Logout") {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(CretaActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                if (menuItem.getTitle() == "Profile") {
                    startActivity(new Intent(CretaActivity.this, ProfileActivity.class));
                    return true;
                }
                if (menuItem.getTitle() == "Contact Support") {
                    startActivity(new Intent(CretaActivity.this, ContactSupportActivity.class));
                    return true;
                }
                return false;
            }
        });

    }
}