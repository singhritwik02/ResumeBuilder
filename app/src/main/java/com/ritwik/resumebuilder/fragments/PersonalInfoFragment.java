package com.ritwik.resumebuilder.fragments;


import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ritwik.builderResume.helper.TextChangeListener;
import com.ritwik.resumebuilder.databases.PersonalInformationDatabase;
import com.ritwik.resumebuilder.R;
import com.ritwik.resumebuilder.datamodel.PersonalInfo;


public class PersonalInfoFragment extends Fragment {
    private PersonalInformationDatabase personalInfoData;
    private PersonalInfo personalInfo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personal_info, container, false);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.green));
        getActivity().getWindow().setNavigationBarColor(getActivity().getResources().getColor(R.color.white));
        personalInfoData = new PersonalInformationDatabase(getContext());
        MobileAds.initialize(requireActivity());
        AdView bottomBanner = root.findViewById(R.id.fpi_BottomBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        bottomBanner.loadAd(adRequest);

        personalInfo = new PersonalInfo();
        personalInfo = personalInfoData.getPersonalInformation();

        EditText nameEditText = root.findViewById(R.id.input_name);
        nameEditText.setText(personalInfo.getName());
        nameEditText.addTextChangedListener(new TextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                personalInfo.setName(s.toString());
                personalInfoData.setPersonalInfoItem(PersonalInformationDatabase.Companion.getKey_Name(), s.toString());
            }
        });

        EditText jobTitleEditText = root.findViewById(R.id.input_job_title);
        jobTitleEditText.setText(personalInfo.getJobTitle());
        jobTitleEditText.addTextChangedListener(new TextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                personalInfo.setJobTitle(s.toString());
                personalInfoData.setPersonalInfoItem(PersonalInformationDatabase.Companion.getKey_Job(), s.toString());
            }
        });

        EditText address1EditText = root.findViewById(R.id.input_address1);
        address1EditText.setText(personalInfo.getAddressLine1());
        address1EditText.addTextChangedListener(new TextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                personalInfo.setAddressLine1(s.toString());
                personalInfoData.setPersonalInfoItem(PersonalInformationDatabase.Companion.getKey_AddressLine1(), s.toString());
            }
        });

        EditText address2EditText = root.findViewById(R.id.input_address2);
        address2EditText.setText(personalInfo.getAddressLine2());
        address2EditText.addTextChangedListener(new TextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                personalInfo.setAddressLine2(s.toString());
                personalInfoData.setPersonalInfoItem(PersonalInformationDatabase.Companion.getKey_AddressLine2(), s.toString());
            }
        });

        EditText phoneEditText = root.findViewById(R.id.input_phone);
        phoneEditText.setText(personalInfo.getPhone());
        phoneEditText.addTextChangedListener(new TextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                personalInfo.setPhone(s.toString());
                personalInfoData.setPersonalInfoItem(PersonalInformationDatabase.Companion.getKey_Phone(), s.toString());
            }
        });

        EditText emailEditText = root.findViewById(R.id.input_email);
        emailEditText.setText(personalInfo.getEmail());
        emailEditText.addTextChangedListener(new TextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                personalInfo.setEmail(s.toString());
                personalInfoData.setPersonalInfoItem(PersonalInformationDatabase.Companion.getKey_Email(), s.toString());
            }
        });
        EditText introLineEditText = root.findViewById(R.id.input_IntroLine);
        introLineEditText.setText(personalInfo.getIntroLine());
        introLineEditText.addTextChangedListener(new TextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                personalInfo.setIntroLine(s.toString());
                personalInfoData.setPersonalInfoItem(PersonalInformationDatabase.Companion.getKey_IntroLine(), s.toString());
            }
        });

        return root;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1024);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            // uploading image to firebase
            FirebaseDatabase.getInstance("https://resumebuilderweb-4ddfa-default-rtdb.firebaseio.com/").getReference().child("test").setValue("ccece");
            StorageReference ref = FirebaseStorage.getInstance("gs://resumebuilderweb-4ddfa.appspot.com").getReference().child("profile");
            ref.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> {
                // show success toast
                Toast.makeText(getContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
            });
        }
    }

}
