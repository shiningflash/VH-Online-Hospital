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

public class ShowMyDoctorListActivity extends AppCompatActivity {

    private RecyclerView findMyDoctorList;

    private DatabaseReference mUsersReference;
    private DatabaseReference newUsersReference;

    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_doctor_list);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        mUsersReference = FirebaseDatabase.getInstance().getReference();
        newUsersReference = mUsersReference.child("My Doctor").child(currentUserId);

        findMyDoctorList = (RecyclerView) findViewById(R.id.find_doctor_list);
        findMyDoctorList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FirebaseRecyclerOptions<DoctorId> options = new FirebaseRecyclerOptions.Builder<DoctorId>()
                .setQuery(newUsersReference, DoctorId.class)
                .build();

        FirebaseRecyclerAdapter<DoctorId, ShowMyDoctorListActivity.ShowMyDoctorViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<DoctorId, ShowMyDoctorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ShowMyDoctorListActivity.ShowMyDoctorViewHolder holder, final int position, @NonNull final DoctorId model) {

                final String visit_user_id = getRef(position).getKey();

                mUsersReference.child("Doctor Info").child(visit_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Doctor doctor = dataSnapshot.getValue(Doctor.class);

                                    holder.cardDoctorName.setText(doctor.getName());
                                    holder.cardDoctorDegree.setText(doctor.getDegree());
                                    holder.cardDoctorDepartment.setText(doctor.getDepartment());
                                    holder.cardDoctorAvailability.setText(doctor.getAvailability());

                                    String imgUrl = doctor.getImageUrl();
                                    Picasso.get().load(imgUrl).into(holder.cardDoctorPicture);

                                    if (doctor.getActive_status().equals("1")) {
                                        holder.cardDoctorAvailability.setVisibility(View.GONE);
                                        holder.cardDoctorActiveNow.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ShowMyDoctorListActivity.this, ProfileDoctorActivity.class);
                        intent.putExtra("visit_user_id", visit_user_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ShowMyDoctorListActivity.ShowMyDoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_doctor, parent, false);
                ShowMyDoctorListActivity.ShowMyDoctorViewHolder viewHolder = new ShowMyDoctorListActivity.ShowMyDoctorViewHolder(view);
                return viewHolder;
            }
        };

        findMyDoctorList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowMyDoctorListActivity.this, HomepageDrawerActivity.class));
                finish();
            }
        });
    }

    public static class ShowMyDoctorViewHolder extends RecyclerView.ViewHolder {
        TextView cardDoctorName;
        TextView cardDoctorDegree;
        TextView cardDoctorDepartment;
        TextView cardDoctorAvailability;
        TextView cardDoctorActiveNow;
        RelativeLayout cardView;
        CircleImageView cardDoctorPicture;

        public ShowMyDoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            cardDoctorName = itemView.findViewById(R.id.card_doctor_name);
            cardDoctorDegree = itemView.findViewById(R.id.card_doctor_degree);
            cardDoctorDepartment = itemView.findViewById(R.id.card_doctor_department);
            cardDoctorAvailability = itemView.findViewById(R.id.card_doctor_availability);
            cardDoctorActiveNow = itemView.findViewById(R.id.card_doctor_active_now);
            cardView = itemView.findViewById(R.id.card_doctor_view);
            cardDoctorPicture= itemView.findViewById(R.id.card_doctor_picture);
        }
    }
}
