package com.vhoh.vhonlinehospital;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class SendFeedbackActivity extends AppCompatActivity {

    private EditText yourFeedback;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);

        loadingBar = new ProgressDialog(this);
        yourFeedback = findViewById(R.id.your_feedback);
    }

    public void sendFeedbackButton(View view) {
        loadingBar.setTitle("Sending");
        loadingBar.setMessage("Please wait ...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        String fb = yourFeedback.getText().toString();
        if (fb.isEmpty()) {
            loadingBar.dismiss();
            Toast.makeText(this, "Please, write something.", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("User Feedback")
                        .child((FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        .child(String.valueOf(Math.abs(((new Random()).nextInt()*1000))));
                databaseReference.setValue(fb);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                startActivity(new Intent(SendFeedbackActivity.this, HomepageDrawerActivity.class));
                finish();
            }
        }
    }
}
