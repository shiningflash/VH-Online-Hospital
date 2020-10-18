package com.vhoh.vhonlinehospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerifyDoctorActivity extends AppCompatActivity {

    private RecyclerView findDoctorList;

    private DatabaseReference userReference;
    private DatabaseReference mUsersReference;

    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_doctor);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        mUsersReference = FirebaseDatabase.getInstance().getReference();
        userReference = mUsersReference.child("Non Verified Doctor Info");

        findDoctorList = (RecyclerView) findViewById(R.id.find_verify_doctor_list);
        findDoctorList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FirebaseRecyclerOptions<Doctor> options = new FirebaseRecyclerOptions.Builder<Doctor>()
                    .setQuery(userReference, Doctor.class)
                    .build();

        FirebaseRecyclerAdapter<Doctor, VerifyDoctorActivity.VerifyDoctorViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Doctor, VerifyDoctorActivity.VerifyDoctorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final VerifyDoctorActivity.VerifyDoctorViewHolder holder, final int position, @NonNull final Doctor model) {
                holder.cardVerifyDoctorName.setText(model.getName());
                holder.cardVerifyDoctorDegree.setText(model.getDegree());
                holder.cardVerifyDoctorDepartment.setText(model.getDepartment());
                final String visit_user_id = getRef(position).getKey();

                String imgUrl = model.getImageUrl();
                Picasso.get().load(imgUrl).into(holder.cardVerifyDoctorPicture);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VerifyDoctorActivity.this, ProfileDoctorActivity.class);
                        intent.putExtra("visit_user_id", visit_user_id);
                        startActivity(intent);
                    }
                });

                holder.verifyProfileView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VerifyDoctorActivity.this, ProfileDoctorActivity.class);
                        intent.putExtra("visit_user_id", visit_user_id);
                        startActivity(intent);
                    }
                });

                holder.verifyAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            userReference.child(visit_user_id)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Doctor doctor = dataSnapshot.getValue(Doctor.class);

                                                mUsersReference.child("Doctor Info").child(visit_user_id).setValue(doctor)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    DatabaseReference reference = userReference.child(visit_user_id);
                                                                    reference.removeValue();
                                                                    Toast.makeText(VerifyDoctorActivity.this, "Doctor added to verified list.", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(VerifyDoctorActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        } catch (Exception e) {
                            Toast.makeText(VerifyDoctorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.verifyRemoveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            DatabaseReference reference = userReference.child(visit_user_id);
                            reference.removeValue();
                        } catch (Exception e) {
                            Toast.makeText(VerifyDoctorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public VerifyDoctorActivity.VerifyDoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_doctor_before_verification, parent, false);
                VerifyDoctorActivity.VerifyDoctorViewHolder viewHolder = new VerifyDoctorActivity.VerifyDoctorViewHolder(view);
                return viewHolder;
            }
        };

        findDoctorList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerifyDoctorActivity.this, HomepageDrawerActivity.class));
                finish();
            }
        });
    }

    public static class VerifyDoctorViewHolder extends RecyclerView.ViewHolder {
        TextView cardVerifyDoctorName;
        TextView cardVerifyDoctorDegree;
        TextView cardVerifyDoctorDepartment;
        RelativeLayout cardVerifydView;
        Button verifyProfileView;
        Button verifyAddButton;
        Button verifyRemoveButton;
        CircleImageView cardVerifyDoctorPicture;

        public VerifyDoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            cardVerifyDoctorName = itemView.findViewById(R.id.card_verify_doctor_name);
            cardVerifyDoctorDegree = itemView.findViewById(R.id.card_verify_doctor_degree);
            cardVerifyDoctorDepartment = itemView.findViewById(R.id.card_verify_doctor_department);
            cardVerifydView = itemView.findViewById(R.id.card_verify_doctor_view);
            verifyProfileView = itemView.findViewById(R.id.verify_view_profile);
            verifyAddButton = itemView.findViewById(R.id.verify_add_doctor);
            verifyRemoveButton = itemView.findViewById(R.id.verify_remove_doctor);
            cardVerifyDoctorPicture = itemView.findViewById(R.id.card_verify_doctor_picture);
        }
    }
}
