package com.vhoh.vhonlinehospital;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfilePatient extends Fragment {

    private TextView profilePatientName;
    private TextView profilePatientAge;
    private TextView profilePatientAddress;
    private TextView profilePatientEmail;
    private TextView profilePatientContact;
    private TextView profilePatientDescription;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersReference;

    private String currentUserId;

    public FragmentProfilePatient() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_patient, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mAuth = FirebaseAuth.getInstance();
            mUsersReference = FirebaseDatabase.getInstance().getReference();

            currentUserId = mAuth.getCurrentUser().getUid();

            profilePatientName = view.findViewById(R.id.profile_patient_name);
            profilePatientAddress = view.findViewById(R.id.profile_patient_address);
            profilePatientEmail = view.findViewById(R.id.profile_patient_email);
            profilePatientContact = view.findViewById(R.id.profile_patient_contact);
            profilePatientAge = view.findViewById(R.id.profile_patient_age);
            profilePatientDescription = view.findViewById(R.id.profile_patient_description);

            initiate();

            MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            AdView mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void initiate() {
        try {
            final DatabaseReference databaseReference = mUsersReference.child("Patient Info").child(currentUserId);

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
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}