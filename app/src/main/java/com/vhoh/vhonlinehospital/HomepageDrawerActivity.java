package com.vhoh.vhonlinehospital;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class HomepageDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    private DatabaseReference mUsersReference;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage_drawer);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mUsersReference = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        setTitle("VH Online Hospital");
        loadFragment(new FragmentHome());
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            setTitle("VH Online Hospital");
            loadFragment(new FragmentHome());
        }
        if (id == R.id.nav_category) {
            setTitle("Category");
            // loadFragment(new Category());
        }
        if (id == R.id.nav_myprofile) {
            setTitle("My Profile");
            if (userType.equals("Patient")) loadFragment(new FragmentProfilePatient());
            else loadFragment(new FragmentProfileDoctor());
        }
        if (id == R.id.nav_editprofile) {
            setTitle("Edit Profile");
            if (userType.equals("Doctor") || userType.equals("Non Verified Doctor")) {
                loadFragment(new FragmentEditDoctorProfile());
            }
            else if (userType.equals("Patient")) {
                loadFragment(new FragmentEditPatientProfile());
            }
        }
        if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent_logout = new Intent(HomepageDrawerActivity.this, RegistrationActivity.class);
            intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent_logout);
            finish();
        }
        if (id == R.id.nav_aboutus) {
            setTitle("About Us");
            loadFragment(new FragmentAboutUs());
        }
        if (id == R.id.nav_feedback) {
            setTitle("Feedback");
            loadFragment(new FragmentSendFeedback());
        }
        if (id == R.id.nav_privacy_policy) {
            setTitle("Privacy Policy");
            loadFragment(new FragmentPrivacyPolicy());
        }
        if (id == R.id.nav_delete_account) {
            setTitle("Delete Account");
            loadFragment(new FragmentDeleteAccount());
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit();
    }

    @Override
    protected void onStart() {
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
                            Toast.makeText(HomepageDrawerActivity.this, "Please, register properly.", Toast.LENGTH_SHORT).show();
                            Intent intent_logout = new Intent(HomepageDrawerActivity.this, RegistrationActivity.class);
                            intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent_logout);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
}
