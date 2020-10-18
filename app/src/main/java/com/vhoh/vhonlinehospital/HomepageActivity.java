package com.vhoh.vhonlinehospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Callable;

public class HomepageActivity extends AppCompatActivity {

    private Button editProfileButton;
    private Button logoutButton;

    private DatabaseReference mUsersReference;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private String userType = "";
    private String calledBy = "";

    private RelativeLayout verifyDoctorAction;
    private RelativeLayout verifyDonor;
    private RelativeLayout gotoMyPatient;
    private RelativeLayout offerVideoSessionAction;
    private ProgressDialog loadingBar;

    private AdView mAdView1;
    private AdView mAdView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        loadingBar = new ProgressDialog(this);

        editProfileButton = findViewById(R.id.edit_profile_button);
        logoutButton = findViewById(R.id.logout_button);

        verifyDoctorAction = findViewById(R.id.verify_doctor_action);
        verifyDonor = findViewById(R.id.verify_donor);
        gotoMyPatient = findViewById(R.id.goto_my_patient);
        offerVideoSessionAction = findViewById(R.id.offer_video_session_action);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mUsersReference = FirebaseDatabase.getInstance().getReference();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType.equals("Doctor") || userType.equals("Non Verified Doctor")) {
                   // startActivity(new Intent(HomepageActivity.this, EditDoctorProfileActivity.class));
                }
                else if (userType.equals("Patient")) {
                   // startActivity(new Intent(HomepageActivity.this, EditPatientProfileActivity.class));
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent_logout = new Intent(HomepageActivity.this, RegistrationActivity.class);
                intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_logout);
                finish();
            }
        });

        verifyDoctorAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this, VerifyDoctorActivity.class);
                startActivity(intent);
            }
        });

        verifyDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this, VerifyDonorActivity.class);
                startActivity(intent);
            }
        });

        gotoMyPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this, ShowMyPatientListActivity.class);
                startActivity(intent);
            }
        });

        offerVideoSessionAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(HomepageActivity.this);
                    final View mView = getLayoutInflater().inflate(R.layout.alertdialog_doctor_video_channel_code, null);
                    mBuilder.setView(mView);

                    final EditText channelCodeET = mView.findViewById(R.id.channel_code);
                    Button continueButton = mView.findViewById(R.id.continue_button);

                    continueButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String code = channelCodeET.getText().toString();
                            if (code.isEmpty()) {
                                channelCodeET.setError("REQUIRED");
                                channelCodeET.requestFocus();
                                return;
                            }
                            if (code.length() < 4) {
                                Toast.makeText(HomepageActivity.this, "Enter a code of length at least 4.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mUsersReference.child("Doctor Info").child(currentUserId).child("active_status").setValue("1");
                            mUsersReference.child("Doctor Info").child(currentUserId).child("secretKey").setValue(code);

                            Intent intent = new Intent(HomepageActivity.this, VideoChatViewActivity.class);
                            intent.putExtra("channel_code", code);
                            intent.putExtra("doctor_id", currentUserId);
                            startActivity(intent);
                        }
                    });

                    android.app.AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }
                catch (Exception e) {
                    Toast.makeText(HomepageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView1 = findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);

        mAdView2 = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest2);
    }

    @Override
    protected void onStart() {
        super.onStart();

        verifyAdmin();
        defineUserType();
    }

    private void verifyAdmin() {
        mUsersReference.child("Admin").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            verifyDoctorAction.setVisibility(View.VISIBLE);
                            verifyDonor.setVisibility(View.VISIBLE);
                            offerVideoSessionAction.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void defineUserType() {
        mUsersReference.child("User Type").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            userType = dataSnapshot.getValue(String.class);

                            if (userType.equals("Doctor")) {
                                gotoMyPatient.setVisibility(View.VISIBLE);
                                offerVideoSessionAction.setVisibility(View.VISIBLE);

                                mUsersReference.child("Doctor Info").child(currentUserId)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) userType = "Doctor";
                                                else userType = "Non Verified Doctor";
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                        else {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(HomepageActivity.this, "Please, register properly.", Toast.LENGTH_SHORT).show();
                            Intent intent_logout = new Intent(HomepageActivity.this, RegistrationActivity.class);
                            intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent_logout);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public void findDoctorListMethod(View view) {
        Intent intent = new Intent(HomepageActivity.this, ShowDoctorListActivity.class);
        startActivity(intent);
    }

    public void makeDonation(View view) {
        Intent intent = new Intent(HomepageActivity.this, MakeDonationActivity.class);
        startActivity(intent);
    }

    public void gotoMyDoctorButton(View view) {
        Intent intent = new Intent(HomepageActivity.this, ShowMyDoctorListActivity.class);
        startActivity(intent);
    }

    public void gotoNotification(View view) {
        Intent intent = new Intent(HomepageActivity.this, NotificationListActivity.class);
        startActivity(intent);
    }

    public void menuButton(View view) {
        try {
            android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(HomepageActivity.this);
            final View mView = getLayoutInflater().inflate(R.layout.alertdialog_homepage_menu, null);
            mBuilder.setView(mView);

            Button aboutUsButton = mView.findViewById(R.id.about_us_button);
            Button feedbackButton = mView.findViewById(R.id.feedback_button);
            Button deleteAccountButton = mView.findViewById(R.id.delete_account_button);

            aboutUsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomepageActivity.this, AboutUsActivity.class);
                    startActivity(intent);
                }
            });

            feedbackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomepageActivity.this, SendFeedbackActivity.class);
                    startActivity(intent);
                }
            });

            deleteAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new AlertDialog.Builder(HomepageActivity.this)
                                .setTitle("Delete Account")
                                .setMessage("Are you sure to delete your account?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                        String str = "";
                                        if (userType.toLowerCase().equals("doctor")) str = "Doctor Info";
                                        else if (userType.toLowerCase().equals("patient")) str = "Patient Info";
                                        else if (userType.toLowerCase().equals("non verified doctor")) str = "Non Verified Doctor Info";
                                        else {
                                            Toast.makeText(HomepageActivity.this, "Can not detect user type. Please, contact to admin.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        reference = reference.child(str).child(currentUserId);
                                        reference.removeValue();

                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(HomepageActivity.this, RegistrationActivity.class);
                                        Toast.makeText(HomepageActivity.this, "Successfully deleted your account.", Toast.LENGTH_SHORT).show();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                    } catch (Exception e) {
                        Toast.makeText(HomepageActivity.this, "Can not delete your account. Please, contact to admins.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            android.app.AlertDialog dialog = mBuilder.create();
            dialog.show();
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void viewProfileButton(View view) {
        Intent intent;
        if (userType.equals("Patient")) intent = new Intent(HomepageActivity.this, ProfilePatientActivity.class);
        else intent = new Intent(HomepageActivity.this, ProfileDoctorActivity.class);

        intent.putExtra("visit_user_id", currentUserId);
        startActivity(intent);
    }

    public void gotoDonorList(View view) {
        Intent intent = new Intent(HomepageActivity.this, ShowDonorListActivity.class);
        startActivity(intent);
    }
}
