package com.vhoh.vhonlinehospital;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {

    View view;

    private DatabaseReference mUsersReference;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private String userType = "";

    private RelativeLayout verifyDoctorAction;
    private RelativeLayout verifyDonor;
    private RelativeLayout gotoMyPatient;
    private RelativeLayout offerVideoSessionAction;

    private RelativeLayout findDoctorList;
    private RelativeLayout gotoNotification;
    private RelativeLayout gotoMyDoctor;
    private RelativeLayout gotoMakeDonation;

    private RelativeLayout gotoDonorList;

    private AdView mAdView1;
    private AdView mAdView2;


    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verifyDoctorAction = view.findViewById(R.id.verify_doctor_action);
        verifyDonor = view.findViewById(R.id.verify_donor);
        gotoMyPatient = view.findViewById(R.id.goto_my_patient);
        offerVideoSessionAction = view.findViewById(R.id.offer_video_session_action);

        findDoctorList = view.findViewById(R.id.findDoctorList);
        gotoNotification = view.findViewById(R.id.gotoNotification);
        gotoMyDoctor = view.findViewById(R.id.gotoMyDoctor);
        gotoMakeDonation = view.findViewById(R.id.makeDonation);
        gotoDonorList = view.findViewById(R.id.donor_list);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mUsersReference = FirebaseDatabase.getInstance().getReference();

        verifyDoctorAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VerifyDoctorActivity.class);
                startActivity(intent);
            }
        });

        verifyDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VerifyDonorActivity.class);
                startActivity(intent);
            }
        });

        gotoMyPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowMyPatientListActivity.class);
                startActivity(intent);
            }
        });

        offerVideoSessionAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(getContext());
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
                                Toast.makeText(getContext(), "Enter a code of length at least 4.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mUsersReference.child("Doctor Info").child(currentUserId).child("active_status").setValue("1");
                            mUsersReference.child("Doctor Info").child(currentUserId).child("secretKey").setValue(code);

                            Intent intent = new Intent(getActivity(), VideoChatViewActivity.class);
                            intent.putExtra("channel_code", code);
                            intent.putExtra("doctor_id", currentUserId);
                            startActivity(intent);
                        }
                    });

                    android.app.AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }
                catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findDoctorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowDoctorListActivity.class);
                startActivity(intent);
            }
        });

        gotoNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationListActivity.class);
                startActivity(intent);
            }
        });

        gotoMyDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowMyDoctorListActivity.class);
                startActivity(intent);
            }
        });

        gotoMakeDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MakeDonationActivity.class);
                startActivity(intent);
            }
        });

        gotoDonorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowDonorListActivity.class);
                startActivity(intent);
            }
        });

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView1 = view.findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);

        mAdView2 = view.findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest2);
    }

    @Override
    public void onStart() {
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
                            Toast.makeText(getContext(), "Please, register properly.", Toast.LENGTH_SHORT).show();
                            Intent intent_logout = new Intent(getActivity(), RegistrationActivity.class);
                            intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent_logout);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
}
