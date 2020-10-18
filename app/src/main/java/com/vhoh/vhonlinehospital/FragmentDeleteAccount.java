package com.vhoh.vhonlinehospital;


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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDeleteAccount extends Fragment {
    private Button deleteConfirm;

    private DatabaseReference mUsersReference;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private String userType = "";

    public FragmentDeleteAccount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_delete_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deleteConfirm = view.findViewById(R.id.delete_confirm);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mUsersReference = FirebaseDatabase.getInstance().getReference();

        deleteConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(getContext())
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
                                        Toast.makeText(getContext(), "Can not detect user type. Please, contact to admin.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    reference = reference.child(str).child(currentUserId);
                                    reference.removeValue();

                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                                    Toast.makeText(getContext(), "Successfully deleted your account.", Toast.LENGTH_SHORT).show();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Can not delete your account. Please, contact to admins.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        defineUserType();
    }

    private void defineUserType() {
        mUsersReference.child("User Type").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            userType = dataSnapshot.getValue(String.class);

                            if (userType.equals("Doctor")) {
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
