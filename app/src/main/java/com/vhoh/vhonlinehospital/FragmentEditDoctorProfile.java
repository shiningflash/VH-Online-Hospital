package com.vhoh.vhonlinehospital;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentEditDoctorProfile extends Fragment {

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
    private String imgUrl;

    private StorageReference mStorageRef;

    private ProgressDialog loadingBar;

    private String activeStatus = "";
    private String secretKey = "";

    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference mUsersReference;

    private String doctorPath = "Non Verified Doctor Info";

    public FragmentEditDoctorProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_doctor_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mUsersReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Picture");

        loadingBar = new ProgressDialog(getContext());

        doctorName = view.findViewById(R.id.doctor_name);
        doctorDepartment = view.findViewById(R.id.doctor_department);
        doctorDegree = view.findViewById(R.id.doctor_degree);
        doctorAvailability = view.findViewById(R.id.doctor_availability);
        doctorEmail = view.findViewById(R.id.doctor_email);
        doctorContact = view.findViewById(R.id.doctor_contact);
        doctorDescription = view.findViewById(R.id.doctor_description);
        doctorSaveProfileButton = view.findViewById(R.id.doctor_save_profile_button);

        doctorProfilePicture = view.findViewById(R.id.doctor_profile_picture);

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
                    Toast.makeText(getContext(), "Please, choose your department", Toast.LENGTH_SHORT).show();
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
                    uploadUserData(imgUrl);
                }

                else {
                    loadingBar.setTitle("Profile Picture Uploading");
                    loadingBar.setMessage("Please wait ... ");

                    final StorageReference filePath = mStorageRef.child(currentUserId);
                    final UploadTask uploadTask = filePath.putFile(imageUri);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                loadingBar.dismiss();
                                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                throw task.getException();
                            }
                            downloadUrl = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUrl = task.getResult().toString();

                                // another work
                                uploadUserData(downloadUrl);
                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            });
    }

    @Override
    public void onStart() {
        super.onStart();

        putPreviousInfoOnPlace();
    }

    private void putPreviousInfoOnPlace() {
        final DatabaseReference databaseReference1 = mUsersReference.child("Non Verified Doctor Info").child(currentUserId);
        final DatabaseReference databaseReference2 = mUsersReference.child("Doctor Info").child(currentUserId);

        databaseReference2
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Doctor doctor = dataSnapshot.getValue(Doctor.class);
                            doctorPath = "Doctor Info";
                            setPrevData(doctor);
                        }
                        else {
                            databaseReference1
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                                                setPrevData(doctor);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_PICK && resultCode== getActivity().RESULT_OK && data!=null) {
            imageUri = data.getData();
            doctorProfilePicture.setImageURI(imageUri);
        }
    }

    private void setPrevData(Doctor doctor) {
        doctorName.setText(doctor.getName());
        doctorDepartment.setPrompt(doctor.getDepartment());
        doctorDegree.setText(doctor.getDegree());
        doctorAvailability.setText(doctor.getAvailability());
        if (!doctor.getEmail().equals("")) doctorEmail.setText(doctor.getEmail());
        if (!doctor.getContact().equals("")) doctorContact.setText(doctor.getContact());
        if (!doctor.getDescription().equals("")) doctorDescription.setText(doctor.getDescription());
        activeStatus = doctor.getActive_status();

        imgUrl = doctor.getImageUrl();
        Picasso.get().load(imgUrl).into(doctorProfilePicture);
    }

    private void uploadUserData(String ppUrl) {
        loadingBar.setTitle("Profile Data Uploading");
        loadingBar.setMessage("Please wait ... ");

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

        doctor.setImageUrl(ppUrl);

        mUsersReference.child(doctorPath).child(currentUserId).setValue(doctor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mUsersReference.child("Users").child(currentUserId).child("name").setValue(doctorName.getText().toString());

                            loadingBar.dismiss();
                            Intent intent = new Intent(getActivity(), HomepageDrawerActivity.class);
                            Toast.makeText(getActivity(), "Profile Info updated successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
