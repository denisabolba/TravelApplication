package com.example.notesapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.checkerframework.checker.units.qual.C;

public class HomeMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView D1,D2,D3,D4,D5,D6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);

        D1 = (CardView) findViewById(R.id.d1);
        D2 = (CardView) findViewById(R.id.d2);
        D3 = (CardView) findViewById(R.id.d3);
        D4 = (CardView) findViewById(R.id.d4);
        D5 = (CardView) findViewById(R.id.d5);
        D6 = (CardView) findViewById(R.id.d6);

        D1.setOnClickListener((View.OnClickListener)this);
        D2.setOnClickListener((View.OnClickListener)this);
        D3.setOnClickListener((View.OnClickListener)this);
        D4.setOnClickListener((View.OnClickListener)this);
        D5.setOnClickListener((View.OnClickListener)this);
        D6.setOnClickListener((View.OnClickListener)this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.d1:
                i = new Intent(this,HomeActivity.class);
                startActivity(i);
                break;
            case R.id.d2:
                i = new Intent(this,MainActivity.class);
                startActivity(i);
                break;
            case R.id.d3:
                i = new Intent(this,MainActivity2.class);
                startActivity(i);
                break;
            case R.id.d4:
                i = new Intent(this,ProfileActivity.class);
                startActivity(i);
                break;
            case R.id.d5:
                i = new Intent(this,ContactSupportActivity.class);
                startActivity(i);
                break;
        }
    }
}