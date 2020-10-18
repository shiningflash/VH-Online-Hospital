package com.vhoh.vhonlinehospital;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSendFeedback extends Fragment {

    private EditText yourFeedback;
    private ProgressDialog loadingBar;
    private Button yourFeedbackButton;

    public FragmentSendFeedback() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingBar = new ProgressDialog(getContext());
        yourFeedback = view.findViewById(R.id.your_feedback);
        yourFeedbackButton = view.findViewById(R.id.your_feedback_button);

        yourFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Sending");
                loadingBar.setMessage("Please wait ...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                String fb = yourFeedback.getText().toString();
                if (fb.isEmpty()) {
                    loadingBar.dismiss();
                    Toast.makeText(getContext(), "Please, write something.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("User Feedback")
                                .child((FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                .child(String.valueOf(Math.abs(((new Random()).nextInt()*1000))));
                        databaseReference.setValue(fb);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } finally {
                        startActivity(new Intent(getActivity(), HomepageDrawerActivity.class));
                    }
                }
            }
        });
    }
}
