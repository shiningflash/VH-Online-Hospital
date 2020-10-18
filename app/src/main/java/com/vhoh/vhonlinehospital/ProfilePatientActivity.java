package com.vhoh.vhonlinehospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ProfilePatientActivity extends AppCompatActivity {

    private TextView profilePatientName;
    private TextView profilePatientAge;
    private TextView profilePatientAddress;
    private TextView profilePatientEmail;
    private TextView profilePatientContact;
    private TextView profilePatientDescription;

    private Button patientSendMessageButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersReference;

    private String profileUserId = "";
    private String currentUserId;

    private String doctorName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_patient);

        try {
            mAuth = FirebaseAuth.getInstance();
            mUsersReference = FirebaseDatabase.getInstance().getReference();

            currentUserId = mAuth.getCurrentUser().getUid();
            profileUserId = getIntent().getStringExtra("visit_user_id");

            profilePatientName = findViewById(R.id.profile_patient_name);
            profilePatientAddress = findViewById(R.id.profile_patient_address);
            profilePatientEmail = findViewById(R.id.profile_patient_email);
            profilePatientContact = findViewById(R.id.profile_patient_contact);
            profilePatientAge = findViewById(R.id.profile_patient_age);
            profilePatientDescription = findViewById(R.id.profile_patient_description);

            patientSendMessageButton = findViewById(R.id.patient_send_message_button);

            initiate();

            patientSendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Date date = new Date();
                    final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    final String strDate = dateFormat.format(date).toString();

                    // for sending notification
                    DatabaseReference ref = mUsersReference.child("Users");

                    Notification notification = new Notification();
                    notification.setProfileid(currentUserId);

                    notification.setDate(strDate);

                    notification.setNote(doctorName + " responded to your appointment. Please, click to check.");

                    ref.child("Notifications").child(profileUserId).child(currentUserId).setValue(notification);

                    Intent intent = new Intent(ProfilePatientActivity.this, SendMessageActivity.class);
                    intent.putExtra("visit_user_id", profileUserId);
                    startActivity(intent);
                }
            });

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void initiate() {
        try {
            if (!currentUserId.equals(profileUserId)) {
                patientSendMessageButton.setVisibility(View.VISIBLE);
            }

            FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(currentUserId).exists()) {
                        String username = dataSnapshot.child(currentUserId).child("name").getValue().toString();
                        doctorName = username;
                    }
                    else {
                        doctorName = "Your doctor";
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            final DatabaseReference databaseReference = mUsersReference.child("Patient Info").child(profileUserId);

            databaseReference
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Patient patient = dataSnapshot.getValue(Patient.class);
                                profilePatientName.setText(patient.getName());
                                profilePatientAddress.setText(patient.getAddress());
                                if (!patient.getEmail().equals(""))
                                    profilePatientEmail.setText("Email: " + patient.getEmail());
                                else profilePatientEmail.setText("Email: Not provided");
                                profilePatientContact.setText("Contact: " + patient.getContact());
                                if (!patient.getDescription().equals(""))
                                    profilePatientDescription.setText(patient.getDescription());
                                else profilePatientDescription.setText("Not provided");
                                if (!patient.getAge().equals(""))
                                    profilePatientAge.setVisibility(View.GONE);
                                else profilePatientAge.setText("Age - " + patient.getAge());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getDoctorName() {
        return this.doctorName;
    }

    public void setDoctorName(String name) {
        this.doctorName = name;
    }
}