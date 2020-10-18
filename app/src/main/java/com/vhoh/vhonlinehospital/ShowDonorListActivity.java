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

public class ShowDonorListActivity extends AppCompatActivity {

    private RecyclerView findDonorList;

    private DatabaseReference userReference;
    private DatabaseReference mUsersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_donor_list);

        mUsersReference = FirebaseDatabase.getInstance().getReference();
        userReference = mUsersReference.child("Donation List");

        findDonorList = (RecyclerView) findViewById(R.id.find_donor_list);
        findDonorList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FirebaseRecyclerOptions<Donation> options = new FirebaseRecyclerOptions.Builder<Donation>()
                .setQuery(userReference, Donation.class)
                .build();

        FirebaseRecyclerAdapter<Donation, ShowDonorListActivity.ShowDonorViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Donation, ShowDonorListActivity.ShowDonorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ShowDonorListActivity.ShowDonorViewHolder holder, final int position, @NonNull final Donation model) {
                holder.cardDonorName.setText(model.getName());
                holder.cardDonorDate.setText(model.getDate());
                holder.cardDonorAmount.setText(model.getAmount());

                final String donation_id = getRef(position).getKey();
            }

            @NonNull
            @Override
            public ShowDonorListActivity.ShowDonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_donor, parent, false);
                ShowDonorListActivity.ShowDonorViewHolder viewHolder = new ShowDonorListActivity.ShowDonorViewHolder(view);
                return viewHolder;
            }
        };

        findDonorList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowDonorListActivity.this, HomepageDrawerActivity.class));
                finish();
            }
        });
    }

    public static class ShowDonorViewHolder extends RecyclerView.ViewHolder {
        TextView cardDonorName;
        TextView cardDonorDate;
        TextView cardDonorAmount;

        public ShowDonorViewHolder(@NonNull View itemView) {
            super(itemView);

            cardDonorName = itemView.findViewById(R.id.card_donor_name);
            cardDonorDate = itemView.findViewById(R.id.card_donor_date);
            cardDonorAmount = itemView.findViewById(R.id.card_donor_amount);
        }
    }
}
