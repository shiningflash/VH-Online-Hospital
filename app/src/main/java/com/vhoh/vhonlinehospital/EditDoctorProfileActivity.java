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

import de.hdodenhof.circleimageview.CircleImageView;

public class EditDoctorProfileActivity extends AppCompatActivity {

    private EditText doctorName;
    private Spinner doctorDepartment;
    private EditText doctorDegree;
    private EditText doctorAvailability;
    private EditText doctorEmail;
    private EditText doctorContact;
    private EditText doctorDescription;
    private Button doctorSaveProfileButton;

    private CircleImageView doctorProfilePicture;
    private static int GALLERY_PICK = 1;
    private Uri imageUri;
    private String downloadUrl;

    private StorageReference mStorageRef;

    private ProgressDialog loadingBar;

    private String activeStatus = "";
    private String secretKey = "";

    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference mUsersReference;

    private String doctorPath = "Non Verified Doctor Info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor_profile);
        setTitle("Edit Profile");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mUsersReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Picture");

        loadingBar = new ProgressDialog(this);

        doctorName = findViewById(R.id.doctor_name);
        doctorDepartment = findViewById(R.id.doctor_department);
        doctorDegree = findViewById(R.id.doctor_degree);
        doctorAvailability = findViewById(R.id.doctor_availability);
        doctorEmail = findViewById(R.id.doctor_email);
        doctorContact = findViewById(R.id.doctor_contact);
        doctorDescription = findViewById(R.id.doctor_description);
        doctorSaveProfileButton = findViewById(R.id.doctor_save_profile_button);

        doctorProfilePicture = findViewById(R.id.doctor_profile_picture);

        doctorProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);
            }
        });

        doctorSaveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Profile Data Uploading");
                loadingBar.setMessage("Please wait ... ");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                if (doctorName.getText().toString().isEmpty()) {
                    doctorName.setError("REQUIRED");
                    doctorName.requestFocus();
                    loadingBar.dismiss();
                    return;
                }

                if (doctorDepartment.getSelectedItem().toString().equals("Choose Department")) {
                    Toast.makeText(EditDoctorProfileActivity.this, "Please, choose your department", Toast.LENGTH_SHORT).show();
                    doctorDepartment.requestFocus();
                    loadingBar.dismiss();
                    return;
                }

                if (doctorDegree.getText().toString().isEmpty()) {
                    doctorDegree.setError("REQUIRED");
                    doctorDegree.requestFocus();
                    loadingBar.dismiss();
                    return;
                }

                if (doctorEmail.getText().toString().isEmpty()) {
                    doctorEmail.setError("REQUIRED");
                    doctorEmail.requestFocus();
                    loadingBar.dismiss();
                    return;
                }

                if (doctorContact.getText().toString().isEmpty()) {
                    doctorContact.setError("REQUIRED");
                    doctorContact.requestFocus();
                    loadingBar.dismiss();
                    return;
                }
                
                if (imageUri == null) {
                    Toast.makeText(EditDoctorProfileActivity.this, "Please, add a profile picture.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(EditDoctorProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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
                            Doctor doctor = new Doctor();
                            doctor.setName(doctorName.getText().toString());
                            doctor.setDepartment(String.valueOf(doctorDepartment.getSelectedItem()));
                            doctor.setDegree(doctorDegree.getText().toString());
                            doctor.setAvailability(doctorAvailability.getText().toString());
                            doctor.setEmail(doctorEmail.getText().toString());
                            doctor.setContact(doctorContact.getText().toString());
                            doctor.setDescription(doctorDescription.getText().toString());
                            doctor.setActive_status(activeStatus);
                            doctor.setSecretKey(String.valueOf((new Random().nextInt()*100000000)));

                            doctor.setImageUrl(downloadUrl);

                            mUsersReference.child(doctorPath).child(currentUserId).setValue(doctor)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mUsersReference.child("Users").child(currentUserId).child("name").setValue(doctorName.getText().toString());

                                                loadingBar.dismiss();
                                                Intent intent = new Intent(EditDoctorProfileActivity.this, HomepageDrawerActivity.class);
                                                Toast.makeText(EditDoctorProfileActivity.this, "Profile Info updated successfully.", Toast.LENGTH_SHORT).show();
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                loadingBar.dismiss();
                                                Toast.makeText(EditDoctorProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(EditDoctorProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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
            doctorProfilePicture.setImageURI(imageUri);
        }
    }
}