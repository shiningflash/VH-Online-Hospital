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

public class ShowMyPatientListActivity extends AppCompatActivity {

    private RecyclerView findMyPatientList;

    private DatabaseReference mUsersReference;
    private DatabaseReference newUsersReference;

    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_patient_list);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        mUsersReference = FirebaseDatabase.getInstance().getReference();
        newUsersReference = mUsersReference.child("My Patient").child(currentUserId);

        findMyPatientList = (RecyclerView) findViewById(R.id.find_patient_list);
        findMyPatientList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FirebaseRecyclerOptions<PatientId> options = new FirebaseRecyclerOptions.Builder<PatientId>()
                .setQuery(newUsersReference, PatientId.class)
                .build();

        FirebaseRecyclerAdapter<PatientId, ShowMyPatientListActivity.ShowMyPatientViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<PatientId, ShowMyPatientViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ShowMyPatientListActivity.ShowMyPatientViewHolder holder, final int position, @NonNull final PatientId model) {

                final String visit_user_id = getRef(position).getKey();

                mUsersReference.child("Patient Info").child(visit_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Patient patient = dataSnapshot.getValue(Patient.class);
                                    holder.cardPatientName.setText(patient.getName());
                                    holder.cardPatientLocation.setText("Address: " + patient.getAddress());
                                }
                                else {
                                    mUsersReference.child("Doctor Info").child(visit_user_id)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        Doctor doctor = dataSnapshot.getValue(Doctor.class);
                                                        holder.cardPatientName.setText(doctor.getName());
                                                        holder.cardPatientLocation.setText("Occupation: Doctor");
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

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ShowMyPatientListActivity.this, ProfilePatientActivity.class);
                        intent.putExtra("visit_user_id", visit_user_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ShowMyPatientListActivity.ShowMyPatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_patient, parent, false);
                ShowMyPatientListActivity.ShowMyPatientViewHolder viewHolder = new ShowMyPatientListActivity.ShowMyPatientViewHolder(view);
                return viewHolder;
            }
        };

        findMyPatientList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowMyPatientListActivity.this, HomepageDrawerActivity.class));
                finish();
            }
        });
    }

    public static class ShowMyPatientViewHolder extends RecyclerView.ViewHolder {
        TextView cardPatientName;
        TextView cardPatientLocation;

        public ShowMyPatientViewHolder(@NonNull View itemView) {
            super(itemView);

            cardPatientName = itemView.findViewById(R.id.card_patient_name);
            cardPatientLocation = itemView.findViewById(R.id.card_patient_location);
        }
    }
}
