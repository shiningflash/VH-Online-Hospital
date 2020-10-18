package com.vhoh.vhonlinehospital;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeveloperProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_profile);

        Button amirulFacebook = findViewById(R.id.amirul_facebook);
        Button amirulLinkedin = findViewById(R.id.amirul_linkedin);
        Button amirulGithub = findViewById(R.id.amirul_github);

        amirulFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/shiningflaash"));
                startActivity(intent);
            }
        });

        amirulLinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/amirulislamalmamun"));
                startActivity(intent);
            }
        });

        amirulGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shiningflash"));
                startActivity(intent);
            }
        });
    }
}
