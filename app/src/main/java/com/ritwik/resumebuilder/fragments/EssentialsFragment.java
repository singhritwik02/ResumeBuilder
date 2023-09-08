package com.ritwik.resumebuilder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ritwik.builderResume.helper.TextChangeListener;
import com.ritwik.resumebuilder.databases.EssentialsDatabase;
import com.ritwik.resumebuilder.R;
import com.ritwik.resumebuilder.datamodel.Essentials;

public class EssentialsFragment extends Fragment {

    private EssentialsDatabase essentialsDatabase;
    private Essentials essentials;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =
                inflater.inflate(R.layout.fragment_essentials, container, false);
        essentialsDatabase = new EssentialsDatabase(getContext());
        essentials = new Essentials();
        essentials = essentialsDatabase.getEssentials();
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.darkPurple));
        getActivity().getWindow().setNavigationBarColor(getActivity().getResources().getColor(R.color.white));
        AdView bannerAd = root.findViewById(R.id.fe_BottomBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(adRequest);

        EditText skillsText = root.findViewById(R.id.input_skills);
        skillsText.setText(essentials.getSkills());
        skillsText.addTextChangedListener(new TextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                essentials.setSkills(s.toString());
                essentialsDatabase.setEssentials(EssentialsDatabase.Companion.getKey_Skills(), s.toString());
            }
        });
        EditText languagesText = root.findViewById(R.id.input_languages);
        languagesText.setText(essentials.getLanguages());
        languagesText.addTextChangedListener(new TextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                essentials.setLanguages(s.toString());
                essentialsDatabase.setEssentials(EssentialsDatabase.Companion.getKey_Languages(), s.toString());
            }
        });
        return root;
    }
}
