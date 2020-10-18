package com.vhoh.vhonlinehospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowDoctorListActivity extends AppCompatActivity {

    private RecyclerView findDoctorList;
    private EditText searchByName;
    private Spinner searchByDepartment;
    private String doctorNameChange = "";
    private String doctorDepartmentChange = "";

    private DatabaseReference userReference;
    private DatabaseReference mUsersReference;

    private String calledBy;

    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_doctor_list);

        setTitle("Find Doctor");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        userReference = FirebaseDatabase.getInstance().getReference().child("Doctor Info");
        mUsersReference = FirebaseDatabase.getInstance().getReference();

        searchByName = (EditText) findViewById(R.id.search_doctor_name);
        // searchByDepartment = (Spinner) findViewById(R.id.search_doctor_department);
        findDoctorList = (RecyclerView) findViewById(R.id.find_doctor_list);
        findDoctorList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        searchByName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doctorNameChange = s.toString();
                onStart();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowDoctorListActivity.this, HomepageDrawerActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Doctor> options = null;

        if (doctorNameChange.equals("")) {
            options = new FirebaseRecyclerOptions.Builder<Doctor>()
                    .setQuery(userReference, Doctor.class)
                    .build();
        }
        else {
            options = new FirebaseRecyclerOptions.Builder<Doctor>()
                    .setQuery(userReference
                                    .orderByChild("name")
                                    .startAt(doctorNameChange)
                                    .endAt(doctorNameChange + "\uf8ff")
                            , Doctor.class)
                    .build();
        }

        FirebaseRecyclerAdapter<Doctor, FindDoctorViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Doctor, FindDoctorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindDoctorViewHolder holder, final int position, @NonNull final Doctor model) {
                holder.cardDoctorName.setText(model.getName());
                holder.cardDoctorDegree.setText(model.getDegree());
                holder.cardDoctorDepartment.setText(model.getDepartment());
                holder.cardDoctorAvailability.setText(model.getAvailability());

                String imgUrl = model.getImageUrl();
                Picasso.get().load(imgUrl).into(holder.cardDoctorPicture);

                if (model.getActive_status().equals("1")) {
                    holder.cardDoctorAvailability.setVisibility(View.GONE);
                    holder.cardDoctorActiveNow.setVisibility(View.VISIBLE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent intent = new Intent(ShowDoctorListActivity.this, ProfileDoctorActivity.class);
                        intent.putExtra("visit_user_id", visit_user_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindDoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_doctor, parent, false);
                FindDoctorViewHolder viewHolder = new FindDoctorViewHolder(view);
                return viewHolder;
            }
        };

        findDoctorList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FindDoctorViewHolder extends RecyclerView.ViewHolder {
        TextView cardDoctorName;
        TextView cardDoctorDegree;
        TextView cardDoctorDepartment;
        TextView cardDoctorAvailability;
        TextView cardDoctorActiveNow;
        RelativeLayout cardView;
        CircleImageView cardDoctorPicture;

        public FindDoctorViewHolder(@NonNull View itemView) {
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
