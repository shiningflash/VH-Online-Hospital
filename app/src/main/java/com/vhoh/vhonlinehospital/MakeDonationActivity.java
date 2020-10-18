package com.vhoh.vhonlinehospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.Random;

public class MakeDonationActivity extends AppCompatActivity {

    private EditText donorName;
    private EditText donorContact;
    private EditText donorAmount;
    private EditText donortrxid;
    private TextView merchantAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_donation);

        donorName = findViewById(R.id.donor_name);
        donorContact = findViewById(R.id.donor_contact);
        donorAmount = findViewById(R.id.donor_amount);
        donortrxid = findViewById(R.id.donor_trxid);

        Button visitWebsite = findViewById(R.id.visit_website);
        Button howSendMoney = findViewById(R.id.how_send_money);
        merchantAccount = findViewById(R.id.merchant_number);

        Button donationSubmitButton = findViewById(R.id.donation_submit_button);

        final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Verify Donation");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String currentUserId = mAuth.getCurrentUser().getUid();

        visitWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://vhbd.org/"));
                startActivity(intent);
            }
        });

        howSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MakeDonationActivity.this, HomepageDrawerActivity.class));
                finish();
            }
        });

        donationSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = donorName.getText().toString();
                String contact = donorContact.getText().toString();
                String amount = donorAmount.getText().toString();
                String trxid = donortrxid.getText().toString();

                if (name.isEmpty()) {
                    donorName.setError("REQUIRED");
                    donorName.requestFocus();
                    return;
                }

                if (contact.isEmpty()) {
                    donorContact.setError("REQUIRED");
                    donorContact.requestFocus();
                    return;
                }

                if (amount.isEmpty()) {
                    donorAmount.setError("REQUIRED");
                    donorAmount.requestFocus();
                    return;
                }

                if (trxid.isEmpty()) {
                    donortrxid.setError("REQUIRED");
                    donortrxid.requestFocus();
                    return;
                }

                final Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String strDate = dateFormat.format(date).toString();

                Donation donation = new Donation();
                donation.setName(name);
                donation.setContact(contact);
                donation.setAmount(amount);
                donation.setTrxid(trxid);
                donation.setUserid(currentUserId);
                donation.setDate(strDate);

                String key = String.valueOf(((int)(Math.random() * 1000000000))) + String.valueOf(((int)(Math.random() * 1000000000)));

                mDatabaseRef.child(key).setValue(donation)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MakeDonationActivity.this, "Thanks for your donation. We will update the info soon.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(MakeDonationActivity.this, HomepageDrawerActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(MakeDonationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance().getReference().child("Merchant Account").child("bkash")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String mAccount = dataSnapshot.getValue(String.class);
                            merchantAccount.setText(mAccount);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
