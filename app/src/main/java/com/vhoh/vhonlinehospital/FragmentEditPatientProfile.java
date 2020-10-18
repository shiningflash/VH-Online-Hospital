package com.vhoh.vhonlinehospital;


import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditPatientProfile extends Fragment {

    private EditText patientName;
    private EditText patientAge;
    private EditText patientEmail;
    private EditText patientContact;
    private EditText patientAddress;
    private EditText patientDescription;
    private Button patientSaveProfileButton;

    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference mUsersReference;


    public FragmentEditPatientProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_patient_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mUsersReference = FirebaseDatabase.getInstance().getReference();

        loadingBar = new ProgressDialog(getContext());

        patientName = view.findViewById(R.id.patient_name);
        patientAge = view.findViewById(R.id.patient_age);
        patientEmail = view.findViewById(R.id.patient_email);
        patientContact = view.findViewById(R.id.patient_contact);
        patientAddress = view.findViewById(R.id.patient_address);
        patientDescription = view.findViewById(R.id.patient_description);

        patientSaveProfileButton = view.findViewById(R.id.patient_save_profile_button);

        patientSaveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Profile Updating");
                loadingBar.setMessage("Please wait ... ");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                if (patientName.getText().toString().isEmpty()) {
                    patientName.setError("REQUIRED");
                    patientName.requestFocus();
                    loadingBar.dismiss();
                    return;
                }

                if (patientContact.getText().toString().isEmpty()) {
                    patientContact.setError("REQUIRED");
                    patientContact.requestFocus();
                    loadingBar.dismiss();
                    return;
                }

                if (patientAddress.getText().toString().isEmpty()) {
                    patientAddress.setError("REQUIRED");
                    patientAddress.requestFocus();
                    loadingBar.dismiss();
                    return;
                }

                Patient patient = new Patient();
                patient.setName(patientName.getText().toString());
                patient.setAge(patientAge.getText().toString());
                patient.setEmail(patientEmail.getText().toString());
                patient.setAddress(patientAddress.getText().toString());
                patient.setContact(patientContact.getText().toString());
                patient.setDescription(patientDescription.getText().toString());

                mUsersReference.child("Patient Info").child(currentUserId).setValue(patient)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mUsersReference.child("Users").child(currentUserId).child("name").setValue(patientName.getText().toString());

                                    loadingBar.dismiss();
                                    Intent intent = new Intent(getActivity(), HomepageDrawerActivity.class);
                                    Toast.makeText(getContext(), "Profile Info updated successfully.", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }
                                else {
                                    loadingBar.dismiss();
                                    Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        putPreviousInfoOnPlace();
    }

    private void putPreviousInfoOnPlace() {
        mUsersReference.child("Patient Info").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Patient patient = dataSnapshot.getValue(Patient.class);

                            if (!patient.getName().equals("")) patientName.setText(patient.getName());
                            if (!patient.getAge().equals("")) patientAge.setText(patient.getAge());
                            if (!patient.getEmail().equals("")) patientEmail.setText(patient.getEmail());
                            if (!patient.getAddress().equals("")) patientAddress.setText(patient.getAddress());
                            if (!patient.getContact().equals("")) patientContact.setText(patient.getContact());
                            if (!patient.getDescription().equals("")) patientDescription.setText(patient.getDescription());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
