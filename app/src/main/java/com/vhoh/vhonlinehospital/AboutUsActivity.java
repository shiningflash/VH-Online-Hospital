package com.vhoh.vhonlinehospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AboutUsActivity extends AppCompatActivity {

    private LinearLayout cardDeveloperProfile;
    private LinearLayout cardFounderProfile;
    private Button viewDeveloper;
    private Button viewFounder;

    private Button joinVhbd;
    private Button vhbdFacebook;

    private String developerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        cardDeveloperProfile = findViewById(R.id.card_developer_profile);
        cardFounderProfile = findViewById(R.id.card_founder_profile);
        viewDeveloper = findViewById(R.id.view_developer);
        viewFounder = findViewById(R.id.view_founder);

        joinVhbd = findViewById(R.id.join_vhbd);
        vhbdFacebook = findViewById(R.id.vhbd_facebook);

        cardDeveloperProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeveloperProfile();
            }
        });

        cardFounderProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFounderProfile();
            }
        });

        viewDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeveloperProfile();
            }
        });

        viewFounder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFounderProfile();
            }
        });

        joinVhbd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://vhbd.org/registration-form/"));
                startActivity(intent);
            }
        });

        vhbdFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/vohbd"));
                startActivity(intent);
            }
        });
    }

    public void showDeveloperProfile() {
        Intent intent = new Intent(AboutUsActivity.this, DeveloperProfileActivity.class);
        startActivity(intent);
    }

    public void showFounderProfile() {
        Intent intent = new Intent(AboutUsActivity.this, FounderProfileActivity.class);
        startActivity(intent);
    }
}
