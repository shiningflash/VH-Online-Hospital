package com.vhoh.vhonlinehospital;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class SendMessageActivity extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    TextView receiverName;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersReference;
    private DatabaseReference mChatMessageUsersReferenceSender;
    private DatabaseReference mChatMessageUsersReferenceReceiver;
    private String profileUserId = "";
    private String currentUserId;
    private String profileUserName = "";
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);
        receiverName = findViewById(R.id.receiver_name);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserId = getIntent().getStringExtra("visit_user_id");

        mUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mChatMessageUsersReferenceSender = mUsersReference.child("Chat Message").child(currentUserId).child(profileUserId);
        mChatMessageUsersReferenceReceiver = mUsersReference.child("Chat Message").child(profileUserId).child(currentUserId);

        mUsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileUserId).exists()) {
                    profileUserName = dataSnapshot.child(profileUserId).child("name").getValue().toString();
                    receiverName.setText(profileUserName);
                }
                if (dataSnapshot.child(currentUserId).exists()) {
                    currentUserName = dataSnapshot.child(currentUserId).child("name").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SendMessageActivity.this, HomepageDrawerActivity.class));
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", currentUserName);
                    mChatMessageUsersReferenceSender.push().setValue(map);
                    mChatMessageUsersReferenceReceiver.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        mChatMessageUsersReferenceSender.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String message = dataSnapshot.child("message").getValue().toString();
                String userName = dataSnapshot.child("user").getValue().toString();

                if(userName.equals(currentUserName)){
                    addMessageBox(message, 1);
                }
                else{
                    addMessageBox(message, 2);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(SendMessageActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.setMargins(5, 5, 5, 5);
        lp2.weight = 10.0f;
        textView.setTextColor(Color.BLACK);
        textView.setPadding(15, 9, 15, 9);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.edit);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}