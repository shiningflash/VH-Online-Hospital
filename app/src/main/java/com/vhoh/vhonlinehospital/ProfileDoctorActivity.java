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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDoctorActivity extends AppCompatActivity {

    private TextView profileDoctorName;
    private TextView profileDoctorDegree;
    private TextView profileDoctorDepartment;
    private TextView profileDoctorAvailability;
    private TextView profileDoctorEmail;
    private TextView profileDoctorContact;
    private TextView profileDoctorDescription;
    private TextView profileDoctorActiveNow;
    private TextView profileDoctorActiveNowDescription;

    private Button doctorVideoCallButton;
    private Button doctorSendMessageButton;

    private CircleImageView doctorProfilePicture;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersReference;
    private DatabaseReference userCallRef;

    private String profileUserId = "";
    private String currentUserId;

    private String channelCode = "";
    private String doctorName = "";
    private String patientName = "";
    private Boolean active_doctor = false;

    private String doctorPath = "Non Verified Doctor Info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_doctor);

        try {
            mAuth = FirebaseAuth.getInstance();
            mUsersReference = FirebaseDatabase.getInstance().getReference();
            userCallRef = mUsersReference.child("Users");

            currentUserId = mAuth.getCurrentUser().getUid();
            profileUserId = getIntent().getStringExtra("visit_user_id");

            profileDoctorName = findViewById(R.id.profile_doctor_name);
            profileDoctorDegree = findViewById(R.id.profile_doctor_degree);
            profileDoctorDepartment = findViewById(R.id.profile_doctor_department);
            profileDoctorAvailability = findViewById(R.id.profile_doctor_availability);
            profileDoctorEmail = findViewById(R.id.profile_doctor_email);
            profileDoctorContact = findViewById(R.id.profile_doctor_contact);
            profileDoctorDescription = findViewById(R.id.profile_doctor_description);

            doctorVideoCallButton = findViewById(R.id.doctor_video_call_button);
            doctorSendMessageButton = findViewById(R.id.doctor_send_message_button);

            doctorProfilePicture = findViewById(R.id.doctor_profile_picture);

            profileDoctorActiveNow = findViewById(R.id.profile_doctor_active_now);
            profileDoctorActiveNowDescription = findViewById(R.id.profile_doctor_active_now_description);

            initiate();

            findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProfileDoctorActivity.this, ShowDoctorListActivity.class));
                    finish();
                }
            });

            findViewById(R.id.doctor_list_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProfileDoctorActivity.this, ShowDoctorListActivity.class));
                    finish();
                }
            });

            doctorVideoCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (doctorPath.equals("Non Verified Doctor Info")) {
                            Toast.makeText(ProfileDoctorActivity.this, "He is not verified yet.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (!getActive_doctor()) {
                                Toast.makeText(ProfileDoctorActivity.this, "Your doctor is not active. Ask for appointment via inbox or wait for him to active.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            try {
                                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(ProfileDoctorActivity.this);
                                final View mView = getLayoutInflater().inflate(R.layout.alertdialog_patient_video_channel_code, null);
                                mBuilder.setView(mView);

                                final EditText patientChannelCode = mView.findViewById(R.id.patient_channel_code);
                                Button continueButton = mView.findViewById(R.id.patient_continue_button);

                                continueButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String code = patientChannelCode.getText().toString();
                                        if (code.isEmpty()) {
                                            patientChannelCode.setError("REQUIRED");
                                            patientChannelCode.requestFocus();
                                            return;
                                        }
                                        if (!code.equals(getChannelCode())) {
                                            Toast.makeText(ProfileDoctorActivity.this, "You entered wrong code.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        // saving in doctors contact
                                        PatientId patientId = new PatientId(currentUserId);
                                        mUsersReference.child("My Patient").child(profileUserId).child(currentUserId).setValue(patientId);

                                        // saving in patients contact
                                        DoctorId doctorId = new DoctorId(profileUserId);
                                        mUsersReference.child("My Doctor").child(currentUserId).child(profileUserId).setValue(doctorId);

                                        Intent intent = new Intent(ProfileDoctorActivity.this, VideoChatViewActivity.class);
                                        intent.putExtra("channel_code", channelCode);
                                        intent.putExtra("doctor_id", profileUserId);
                                        startActivity(intent);
                                    }
                                });

                                android.app.AlertDialog dialog = mBuilder.create();
                                dialog.show();
                            }
                            catch (Exception e) {
                                Toast.makeText(ProfileDoctorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(ProfileDoctorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            doctorSendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // saving in doctors contact
                    PatientId patientId = new PatientId(currentUserId);
                    mUsersReference.child("My Patient").child(profileUserId).child(currentUserId).setValue(patientId);

                    // saving in patients contact
                    DoctorId doctorId = new DoctorId(profileUserId);
                    mUsersReference.child("My Doctor").child(currentUserId).child(profileUserId).setValue(doctorId);

                    final Date date = new Date();

                    // for sending notification
                    DatabaseReference ref = mUsersReference.child("Users");

                    Notification notification = new Notification();
                    notification.setProfileid(currentUserId);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String strDate = dateFormat.format(date).toString();
                    notification.setDate(strDate);

                    notification.setNote(patientName + " asked for your appointment. Please, click to check.");

                    ref.child("Notifications").child(profileUserId).child(currentUserId).setValue(notification);

                    Intent intent = new Intent(ProfileDoctorActivity.this, SendMessageActivity.class);
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
                doctorSendMessageButton.setVisibility(View.VISIBLE);
                doctorVideoCallButton.setVisibility(View.VISIBLE);
            }

            FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(currentUserId).exists()) {
                        String username = dataSnapshot.child(currentUserId).child("name").getValue().toString();
                        patientName = username;
                    }
                    else {
                        patientName = "A patient";
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfileDoctorActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            final DatabaseReference databaseReference1 = mUsersReference.child("Non Verified Doctor Info").child(profileUserId);
            final DatabaseReference databaseReference2 = mUsersReference.child("Doctor Info").child(profileUserId);

            databaseReference2
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                doctorPath = "Doctor Info";

                                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                                setDoctorData(doctor);
                            }
                            else {
                                databaseReference1
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    Doctor doctor = dataSnapshot.getValue(Doctor.class);
                                                    setDoctorData(doctor);
                                                }
                                                else {
                                                    Toast.makeText(ProfileDoctorActivity.this, "The doctor doesn't exist anymore.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(ProfileDoctorActivity.this, ShowDoctorListActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
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

    private void setDoctorData(Doctor doctor) {
        String imgUrl = doctor.getImageUrl();
        Picasso.get().load(imgUrl).into(doctorProfilePicture);

        profileDoctorName.setText(doctor.getName());
        setDoctorName(doctor.getName());
        profileDoctorDepartment.setText(doctor.getDepartment());
        profileDoctorDegree.setText(doctor.getDegree());
        profileDoctorAvailability.setText(doctor.getAvailability());
        if (!doctor.getEmail().equals(""))
            profileDoctorEmail.setText("Email: " + doctor.getEmail());
        else profileDoctorEmail.setText("Email: Not provided");
        if (!doctor.getContact().equals(""))
            profileDoctorContact.setText("Contact: " + doctor.getContact());
        else profileDoctorContact.setText("Contact: Not provided");
        if (!doctor.getDescription().equals(""))
            profileDoctorDescription.setText(doctor.getDescription());
        else profileDoctorDescription.setText("Not provided");

        if (doctor.getActive_status().equals("1")) {
            profileDoctorActiveNow.setVisibility(View.VISIBLE);
            profileDoctorActiveNowDescription.setVisibility(View.VISIBLE);

            setActive_doctor(true);
            setChannelCode(doctor.getSecretKey());
            profileDoctorActiveNowDescription.setText("Use channel code - " + doctor.getSecretKey() + " - to join with doctor.");
        }

        else {
            setActive_doctor(false);
            profileDoctorActiveNow.setVisibility(View.GONE);
            profileDoctorActiveNowDescription.setVisibility(View.GONE);
        }
    }

    public Boolean getActive_doctor() {
        return this.active_doctor;
    }

    public void setActive_doctor(Boolean flag) {
        this.active_doctor = flag;
    }

    public String getChannelCode() {
        return this.channelCode;
    }

    public void setChannelCode(String cc) {
        this.channelCode = cc;
    }

    public String getDoctorName() {
        return this.doctorName;
    }

    public void setDoctorName(String name) {
        this.doctorName = name;
    }

    public String getPatientName() {
        return this.patientName;
    }

    public void setPatientName(String name) {
        this.patientName = name;
    }
}
