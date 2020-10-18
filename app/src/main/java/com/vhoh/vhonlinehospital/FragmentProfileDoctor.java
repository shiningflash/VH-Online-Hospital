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
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfileDoctor extends Fragment {

    private TextView profileDoctorName;
    private TextView profileDoctorDegree;
    private TextView profileDoctorDepartment;
    private TextView profileDoctorAvailability;
    private TextView profileDoctorEmail;
    private TextView profileDoctorContact;
    private TextView profileDoctorDescription;

    private CircleImageView doctorProfilePicture;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersReference;
    private DatabaseReference userCallRef;

    private String currentUserId;

    private String doctorName = "";

    private String doctorPath = "Non Verified Doctor Info";

    public FragmentProfileDoctor() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mAuth = FirebaseAuth.getInstance();
            mUsersReference = FirebaseDatabase.getInstance().getReference();
            userCallRef = mUsersReference.child("Users");

            currentUserId = mAuth.getCurrentUser().getUid();

            profileDoctorName = view.findViewById(R.id.profile_doctor_name);
            profileDoctorDegree = view.findViewById(R.id.profile_doctor_degree);
            profileDoctorDepartment = view.findViewById(R.id.profile_doctor_department);
            profileDoctorAvailability = view.findViewById(R.id.profile_doctor_availability);
            profileDoctorEmail = view.findViewById(R.id.profile_doctor_email);
            profileDoctorContact = view.findViewById(R.id.profile_doctor_contact);
            profileDoctorDescription = view.findViewById(R.id.profile_doctor_description);

            doctorProfilePicture = view.findViewById(R.id.doctor_profile_picture);

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
            final DatabaseReference databaseReference1 = mUsersReference.child("Non Verified Doctor Info").child(currentUserId);
            final DatabaseReference databaseReference2 = mUsersReference.child("Doctor Info").child(currentUserId);

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
                                                    Toast.makeText(getContext(), "The doctor doesn't exist anymore.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getActivity(), ShowDoctorListActivity.class);
                                                    startActivity(intent);
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
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setDoctorData(Doctor doctor) {
        String imgUrl = doctor.getImageUrl();
        Picasso.get().load(imgUrl).into(doctorProfilePicture);

        profileDoctorName.setText(doctor.getName());
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
    }
}
