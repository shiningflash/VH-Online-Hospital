package com.vhoh.vhonlinehospital;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class EditPatientProfileActivity extends AppCompatActivity {

    private EditText patientName;
    private EditText patientAge;
    private EditText patientEmail;
    private EditText patientContact;
    private EditText patientAddress;
    private EditText patientDescription;
    private Button patientSaveProfileButton;

    private ImageView patientProfilePicture;
    private static int GALLERY_PICK = 1;
    private Uri imageUri;
    private String downloadUrl;

    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference mUsersReference;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient_profile);
        setTitle("Edit Profile");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mUsersReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Picture");

        loadingBar = new ProgressDialog(this);

        patientName = findViewById(R.id.patient_name);
        patientAge = findViewById(R.id.patient_age);
        patientEmail = findViewById(R.id.patient_email);
        patientContact = findViewById(R.id.patient_contact);
        patientAddress = findViewById(R.id.patient_address);
        patientDescription = findViewById(R.id.patient_description);

        patientProfilePicture = findViewById(R.id.patient_profile_picture);

        patientProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);
            }
        });

        patientSaveProfileButton = findViewById(R.id.patient_save_profile_button);

        patientSaveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Profile Data Uploading");
                loadingBar.setMessage("Please wait ... ");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                if (patientName.getText().toString().isEmpty()) {
                    patientName.setError("REQUIRED");
                    patientName.requestFocus();
                    loadingBar.dismiss();
                    return;
                }

                if (patientContact.getText().toString().isEmpty()) {
                    patientContact.setError("REQUIRED");
                    patientContact.requestFocus();
                    loadingBar.dismiss();
                    return;
                }

                if (patientAddress.getText().toString().isEmpty()) {
                    patientAddress.setError("REQUIRED");
                    patientAddress.requestFocus();
                    loadingBar.dismiss();
                    return;
                }

                if (imageUri == null) {
                    Toast.makeText(EditPatientProfileActivity.this, "Please, add a profile picture.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    return;
                }

                loadingBar.setTitle("Profile Picture Uploading");
                loadingBar.setMessage("Please wait ... ");

                final StorageReference filePath = mStorageRef.child(currentUserId);
                final UploadTask uploadTask = filePath.putFile(imageUri);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(EditPatientProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            throw task.getException();
                        }
                        downloadUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        loadingBar.setTitle("Profile Data Uploading");
                        loadingBar.setMessage("Please wait ... ");
                        if (task.isSuccessful()) {
                            downloadUrl = task.getResult().toString();

                            // another work
                            Patient patient = new Patient();
                            patient.setName(patientName.getText().toString());
                            patient.setAge(patientAge.getText().toString());
                            patient.setEmail(patientEmail.getText().toString());
                            patient.setAddress(patientAddress.getText().toString());
                            patient.setContact(patientContact.getText().toString());
                            patient.setDescription(patientDescription.getText().toString());

                            patient.setImageUrl(downloadUrl);

                            mUsersReference.child("Patient Info").child(currentUserId).setValue(patient)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mUsersReference.child("Users").child(currentUserId).child("name").setValue(patientName.getText().toString());

                                                loadingBar.dismiss();
                                                Intent intent = new Intent(EditPatientProfileActivity.this, HomepageActivity.class);
                                                Toast.makeText(EditPatientProfileActivity.this, "Profile Info updated successfully.", Toast.LENGTH_SHORT).show();
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                loadingBar.dismiss();
                                                Toast.makeText(EditPatientProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(EditPatientProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_PICK && resultCode==RESULT_OK && data!=null) {
            imageUri = data.getData();
            patientProfilePicture.setImageURI(imageUri);
        }
    }
}