package com.vhoh.vhonlinehospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {

    private Button userGuideButton;
    private Button privacyPolicyButton;
    private Button enterIntoApp;
    private Button aboutUsButton;

    private  ProgressDialog loadingBar;

    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String checker = "";
    String mVerificationId = "";
    PhoneAuthProvider.ForceResendingToken mResendToekn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        enterIntoApp = findViewById(R.id.enter_into_app);
        // userGuideButton = findViewById(R.id.user_guide_button);
        privacyPolicyButton = findViewById(R.id.privacy_policy_button);
        aboutUsButton = findViewById(R.id.about_us_button);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(RegistrationActivity.this);

        enterIntoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegistrationActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.alertdialog_login, null);

                final EditText phoneText = mView.findViewById(R.id.phoneText);
                final EditText codeText = mView.findViewById(R.id.codeText);
                final Button continueAndNextButton = mView.findViewById(R.id.continueNextButton);
                final RelativeLayout relativeLayout = mView.findViewById(R.id.phoneAuth);
                final CountryCodePicker countryCodePicker = mView.findViewById(R.id.ccp);
                countryCodePicker.registerCarrierNumberEditText(phoneText);

                continueAndNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (continueAndNextButton.getText().equals("Submit") || checker.equals("Code Sent")) {
                            String verificationCode = codeText.getText().toString();
                            if (verificationCode.equals("")) {
                                Toast.makeText(RegistrationActivity.this, "Please, enter verification code", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingBar.setTitle("Code Verification");
                                loadingBar.setMessage("Please wait, we are verifying your code");
                                loadingBar.setCanceledOnTouchOutside(false);
                                loadingBar.show();

                                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                                signInWithPhoneAuthCredential(credential);
                            }
                        } else {
                            final String phoneNumber = countryCodePicker.getFullNumberWithPlus();
                            if (!phoneNumber.equals("")) {
                                loadingBar.setTitle("Phone Number Verification");
                                loadingBar.setMessage("Please wait, we are verifying your phone number");
                                loadingBar.setCanceledOnTouchOutside(false);
                                loadingBar.show();

                                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, RegistrationActivity.this, mCallbacks);
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Please, enter a valid phone number", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        // Toast.makeText(RegistrationActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                        relativeLayout.setVisibility(View.VISIBLE);
                        loadingBar.dismiss();

                        continueAndNextButton.setText("Continue");
                        codeText.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        mVerificationId = s;
                        mResendToekn = forceResendingToken;

                        relativeLayout.setVisibility(View.GONE);
                        checker = "Code Sent";
                        continueAndNextButton.setText("Submit");
                        codeText.setVisibility(View.VISIBLE);

                        loadingBar.dismiss();
                        Toast.makeText(RegistrationActivity.this, "Code has been sent. please check.", Toast.LENGTH_SHORT).show();
                    }
                };

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        /*
        userGuideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegistrationActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.alertdialog_userguide, null);
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
         */

        privacyPolicyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegistrationActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.alertdialog_privacy_policy, null);
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, AboutUsActivity.class));
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Congratulations. You're logged in successfully.", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Error : " +  task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(RegistrationActivity.this, HomepageDrawerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void sendUserToMainActivity() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegistrationActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.alertdialog_usertype, null);
        mBuilder.setView(mView);

        RadioGroup userGroup = mView.findViewById(R.id.user_type_radio);
        final RadioButton doctorType = mView.findViewById(R.id.doctor_type);
        final RadioButton patientType = mView.findViewById(R.id.patient_type);

        doctorType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Welcome");
                loadingBar.setMessage("Please wait a moment, we are setting everything for you");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                defineUserType("Doctor");
            }
        });

        patientType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Welcome");
                loadingBar.setMessage("Please wait a moment, we are setting everything for you");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                defineUserType("Patient");
            }
        });

        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void defineUserType(final String userType) {
        try {
            final String currentUserId = mAuth.getCurrentUser().getUid();
            final DatabaseReference mUsersReference = FirebaseDatabase.getInstance().getReference();

            mUsersReference.child("User Type").child(currentUserId).setValue(userType)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (userType.equals("Doctor")) {
                                    mUsersReference.child("Doctor Info")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    loadingBar.dismiss();
                                                    if (dataSnapshot.hasChild(currentUserId)) {
                                                        startActivity(new Intent(RegistrationActivity.this, HomepageDrawerActivity.class));
                                                        finish();
                                                    } else {
                                                        mUsersReference.child("Non Verified Doctor Info")
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        loadingBar.dismiss();
                                                                        if (dataSnapshot.hasChild(currentUserId)) {
                                                                            startActivity(new Intent(RegistrationActivity.this, HomepageDrawerActivity.class));
                                                                            finish();
                                                                        } else {
                                                                            startActivity(new Intent(RegistrationActivity.this, EditDoctorProfileActivity.class));
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
                                } else if (userType.equals("Patient")) {
                                    mUsersReference.child("Patient Info")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    loadingBar.dismiss();
                                                    if (dataSnapshot.hasChild(currentUserId)) {
                                                        startActivity(new Intent(RegistrationActivity.this, HomepageDrawerActivity.class));
                                                        finish();
                                                    } else {
                                                        startActivity(new Intent(RegistrationActivity.this, EditPatientProfileActivity.class));
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(RegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}