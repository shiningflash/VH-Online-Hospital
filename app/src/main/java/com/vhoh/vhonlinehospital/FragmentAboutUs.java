package com.vhoh.vhonlinehospital;


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
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAboutUs extends Fragment {

    private LinearLayout cardDeveloperProfile;
    private LinearLayout cardFounderProfile;
    private Button viewDeveloper;
    private Button viewFounder;

    private Button joinVhbd;
    private Button vhbdFacebook;

    private String developerId = "";


    public FragmentAboutUs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardDeveloperProfile = view.findViewById(R.id.card_developer_profile);
        cardFounderProfile = view.findViewById(R.id.card_founder_profile);
        viewDeveloper = view.findViewById(R.id.view_developer);
        viewFounder = view.findViewById(R.id.view_founder);

        joinVhbd = view.findViewById(R.id.join_vhbd);
        vhbdFacebook = view.findViewById(R.id.vhbd_facebook);

        cardDeveloperProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeveloperProfile();
            }
        });

        cardFounderProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFounderProfile();
            }
        });

        viewDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeveloperProfile();
            }
        });

        viewFounder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFounderProfile();
            }
        });

        joinVhbd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://vhbd.org/registration-form/"));
                startActivity(intent);
            }
        });

        vhbdFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/vohbd"));
                startActivity(intent);
            }
        });
    }

    public void showDeveloperProfile() {
        Intent intent = new Intent(getActivity(), DeveloperProfileActivity.class);
        startActivity(intent);
    }

    public void showFounderProfile() {
        Intent intent = new Intent(getActivity(), FounderProfileActivity.class);
        startActivity(intent);
    }
}
