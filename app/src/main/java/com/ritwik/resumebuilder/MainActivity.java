package com.ritwik.resumebuilder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.ritwik.resumebuilder.fragments.HomeScreenFragment;


public class MainActivity extends AppCompatActivity {
    private String currentTitle;
    private String STATE_CURRENT_TITLE = "current title";
    private ImageView menuButton;
    public static final int personalInformationId = R.id.action_personal_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("SerializableObject", "");


        if (savedInstanceState == null) {
            openHomeFragment();
            currentTitle = getString(R.string.navigation_personal_info);
        } else currentTitle = savedInstanceState.getString(STATE_CURRENT_TITLE);


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_TITLE, currentTitle);
    }



    private boolean handleMenuItem(MenuItem item) {


        int menuItemId = item.getItemId();
        switch (menuItemId) {
//            case personalInformationId:
//                openFragment(PersonalInfoFragment.newInstance(resume));
//                break;
//            case R.id.action_essentials:
//                openFragment(EssentialsFragment.newInstance(resume));
//                break;
//            case R.id.action_projects:
//                openFragment(ProjectsFragment.newInstance(resume));
//                break;
//            case R.id.action_education:
//                openFragment(EducationFragment.newInstance(resume));
//                break;
//            case R.id.action_experience:
//                openFragment(ExperienceFragment.newInstance(resume));
//                break;
//            case R.id.action_preview:
//                openFragment(PreviewFragment.newInstance(resume));
//                break;
//            default:
//                return false;
        }
        return true;
    }

    
    private void openHomeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new HomeScreenFragment());
        fragmentTransaction.commit();
    }

    private void hideMenuButton() {
        menuButton.animate().alpha(0f).setDuration(300).withEndAction(new Runnable() {

            @Override
            public void run() {
                menuButton.setVisibility(View.GONE);
            }
        });
    }

    private void showMenuButton() {
        menuButton.animate().alpha(1f).setDuration(300).withEndAction(new Runnable() {

            @Override
            public void run() {
                menuButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
