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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class NotificationListActivity extends AppCompatActivity {

    private RecyclerView findNotificationList;

    private DatabaseReference mUsersReference;
    private DatabaseReference newUsersReference;

    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        mUsersReference = FirebaseDatabase.getInstance().getReference();
        newUsersReference = mUsersReference.child("Users").child("Notifications").child(currentUserId);

        findNotificationList = (RecyclerView) findViewById(R.id.notification_list);
        findNotificationList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        try {
            FirebaseRecyclerOptions<Notification> options = new FirebaseRecyclerOptions.Builder<Notification>()
                    .setQuery(newUsersReference.orderByChild("date"), Notification.class)
                    .build();

            FirebaseRecyclerAdapter<Notification, NotificationListActivity.NotificationViewHolder> firebaseRecyclerAdapter
                    = new FirebaseRecyclerAdapter<Notification, NotificationViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final NotificationListActivity.NotificationViewHolder holder, final int position, @NonNull final Notification model) {

                    final String visit_user_id = getRef(position).getKey();
                    holder.notificationText.setText(model.getNote());
                    holder.notificationTime.setText(model.getDate());

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newUsersReference.child(visit_user_id).removeValue();

                            Intent intent = new Intent(NotificationListActivity.this, SendMessageActivity.class);
                            intent.putExtra("visit_user_id", visit_user_id);
                            startActivity(intent);
                        }
                    });
                }

                @NonNull
                @Override
                public NotificationListActivity.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
                    NotificationListActivity.NotificationViewHolder viewHolder = new NotificationListActivity.NotificationViewHolder(view);
                    return viewHolder;
                }
            };

            findNotificationList.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationText;
        TextView notificationTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            notificationText = itemView.findViewById(R.id.notification_text);
            notificationTime = itemView.findViewById(R.id.notification_time);
        }
    }
}
