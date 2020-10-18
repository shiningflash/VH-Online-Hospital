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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerifyDonorActivity extends AppCompatActivity {

    private RecyclerView findDonorList;

    private DatabaseReference userReference;
    private DatabaseReference mUsersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_donor);

        mUsersReference = FirebaseDatabase.getInstance().getReference();
        userReference = mUsersReference.child("Verify Donation");

        findDonorList = (RecyclerView) findViewById(R.id.find_verify_donor_list);
        findDonorList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FirebaseRecyclerOptions<Donation> options = new FirebaseRecyclerOptions.Builder<Donation>()
                .setQuery(userReference, Donation.class)
                .build();

        FirebaseRecyclerAdapter<Donation, VerifyDonorActivity.VerifyDonorViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Donation, VerifyDonorActivity.VerifyDonorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final VerifyDonorActivity.VerifyDonorViewHolder holder, final int position, @NonNull final Donation model) {
                holder.cardVerifyDonorName.setText(model.getName());
                holder.cardVerifyDonorContact.setText(model.getContact());
                holder.cardVerifyDonorTrxid.setText(model.getTrxid());
                holder.cardVerifyDonorDate.setText(model.getDate());
                holder.cardVerifyDonorAmount.setText(model.getAmount());

                final String donation_id = getRef(position).getKey();

                holder.verifyAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            userReference.child(donation_id)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Donation donation = dataSnapshot.getValue(Donation.class);

                                                mUsersReference.child("Donation List").child(donation_id).setValue(donation)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(VerifyDonorActivity.this, "Donation added to list.", Toast.LENGTH_SHORT).show();
                                                                    holder.verifyAddButton.setVisibility(View.GONE);
                                                                    holder.verifyRemoveButton.setVisibility(View.GONE);
                                                                } else {
                                                                    Toast.makeText(VerifyDonorActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                            DatabaseReference reference = userReference.child(donation_id);
                            reference.removeValue();
                        } catch (Exception e) {
                            Toast.makeText(VerifyDonorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.verifyRemoveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            DatabaseReference reference = userReference.child(donation_id);
                            reference.removeValue();
                        } catch (Exception e) {
                            Toast.makeText(VerifyDonorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public VerifyDonorActivity.VerifyDonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_verify_donor, parent, false);
                VerifyDonorActivity.VerifyDonorViewHolder viewHolder = new VerifyDonorActivity.VerifyDonorViewHolder(view);
                return viewHolder;
            }
        };

        findDonorList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerifyDonorActivity.this, HomepageDrawerActivity.class));
                finish();
            }
        });
    }

    public static class VerifyDonorViewHolder extends RecyclerView.ViewHolder {
        TextView cardVerifyDonorName;
        TextView cardVerifyDonorContact;
        TextView cardVerifyDonorTrxid;
        TextView cardVerifyDonorDate;
        TextView cardVerifyDonorAmount;

        Button verifyAddButton;
        Button verifyRemoveButton;

        public VerifyDonorViewHolder(@NonNull View itemView) {
            super(itemView);

            cardVerifyDonorName = itemView.findViewById(R.id.card_verify_donor_name);
            cardVerifyDonorContact = itemView.findViewById(R.id.card_verify_donor_contact);
            cardVerifyDonorTrxid = itemView.findViewById(R.id.card_verify_donor_trxid);
            cardVerifyDonorDate = itemView.findViewById(R.id.card_verify_donor_date);
            cardVerifyDonorAmount = itemView.findViewById(R.id.card_verify_donor_amount);

            verifyAddButton = itemView.findViewById(R.id.verify_add_donor);
            verifyRemoveButton = itemView.findViewById(R.id.verify_remove_donor);
        }
    }
}
