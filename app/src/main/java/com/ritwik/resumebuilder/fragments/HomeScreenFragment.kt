package com.ritwik.resumebuilder.fragments


import android.os.Bundle
import android.preference.PreferenceManager
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson

import com.ritwik.resumebuilder.R
import com.ritwik.resumebuilder.databases.EducationDatabase
import com.ritwik.resumebuilder.databases.EssentialsDatabase
import com.ritwik.resumebuilder.databases.ExperienceDatabase
import com.ritwik.resumebuilder.databases.PersonalInformationDatabase
import com.ritwik.resumebuilder.databases.ProjectsDatabase
import com.ritwik.resumebuilder.databinding.FragmentHomeScreenBinding


class HomeScreenFragment : Fragment() {
    private lateinit var binding: FragmentHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentHomeScreenBinding.inflate(layoutInflater)
        with(activity)
        {
            if(this == null)
            {
                return@with
            }

        }
        // checking personal infos


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeScreenBinding.inflate(layoutInflater)
        with(activity)
        {
            if(this == null)
            {
                return@with
            }
            window.statusBarColor = getColor(R.color.white)
            window.navigationBarColor = getColor(R.color.white)
        }
        MobileAds.initialize(requireActivity())
        val mAdView = binding.fhsBottomBanner
        val adRequest: AdRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        val gson = Gson()
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val json = mPrefs.getString("SerializableObject", "")
        binding.fhsChooseProfileImageCard.setOnClickListener {
            openFragment(ChooseProfileImageFragment())
        }
        binding.fhsPersonalDetailsBanner.setOnClickListener {
            openFragment(PersonalInfoFragment())
        }
        //
        binding.fhsEssentialsBanner.setOnClickListener {
            openFragment(EssentialsFragment())
        }
        //
        binding.fhsProjectsBanner.setOnClickListener {
            openFragment(ProjectsFragment())
        }
        binding.fhsSchoolsBanner.setOnClickListener {
            openFragment(EducationDetailsFragment())
        }
        binding.fhsPreviewBanner.setOnClickListener {

//           if(!(checkPersonalInfo() || checkEssentials()|| checkEducation()))
//           {
//               Toast.makeText(context, "Details Incomplete", Toast.LENGTH_SHORT).show()
//               return@setOnClickListener
//           }
           openFragment(ChooseLayoutFragment())
        }
        binding.fhsQRBanner.setOnClickListener {
            openFragment(QRFragment())
        }
        binding.fhsExperienceBanner.setOnClickListener {
            openFragment(ExperienceFragment())
        }
        binding.fhsCertificationsBanner.setOnClickListener {
            openFragment(CertificationsFragment())
        }

        if(checkPersonalInfo())
        {
            setCompleteStatus(true,binding.fhsPersonalDetailsProgress)
        }
        else
        {
            setCompleteStatus(false,binding.fhsPersonalDetailsProgress)
        }
        // checking essential info
        if(checkEssentials())
        {
            setCompleteStatus(true,binding.fhsEssentialsProgress)
        }
        else
        {
            setCompleteStatus(false,binding.fhsEssentialsProgress)
        }
        // checking experience info
        if(checkExperience())
        {
            setCompleteStatus(true,binding.fhsExperienceProgress)
        }
        else
        {
            setCompleteStatus(false,binding.fhsExperienceProgress)
        }
        // checking education info
        if(checkEducation())
        {
            setCompleteStatus(true,binding.fhsEducationProgress)
        }
        else
        {
            setCompleteStatus(false,binding.fhsEducationProgress)
        }
        if(checkProjects())
        {
            setCompleteStatus(true,binding.fhsProjectsProgress)
        }
        else
        {
            setCompleteStatus(false,binding.fhsProjectsProgress)
        }
        // checking for profile image
        if(checkProfileImage())
        {
            setCompleteStatus(true,binding.fhsProfileImageProgress)
        }
        else
        {
            setCompleteStatus(false,binding.fhsProfileImageProgress)
        }

        return binding.root
    }
    private fun openFragment(fragment: Fragment) {
//        fragment.setSharedElementEnterTransition(DetailsTransition())
        fragment.setEnterTransition(Fade())
        exitTransition = Fade()
//        fragment.setSharedElementReturnTransition(DetailsTransition())
        val fragmentTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.addSharedElement(binding.fhsPersonalDetailsImage,"transition_Image")
        fragmentTransaction.addSharedElement(binding.fhsPersonalDetailsLabel,"transition_Title")
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun checkPersonalInfo():Boolean
    {
        val personalInfo = PersonalInformationDatabase(requireContext())
        return personalInfo.checkComplete()
    }
    private fun checkEssentials():Boolean
    {
        val essentialsDatabase = EssentialsDatabase(requireContext())
        return essentialsDatabase.getEssentials().skills!="" && essentialsDatabase.getEssentials().languages!=""

    }
    private fun checkEducation():Boolean
    {
        val educationDatabase = EducationDatabase(requireContext())
        return educationDatabase.checkComplete()
    }
    fun checkProjects():Boolean
    {
        val projectsDatabase = ProjectsDatabase(requireContext())
        return projectsDatabase.checkComplete()

    }
    private fun checkProfileImage():Boolean
    {
        val personalInformationDatabase = PersonalInformationDatabase(requireContext())
        return personalInformationDatabase.checkProfileImage()
    }

    fun checkExperience():Boolean
    {
        val experienceDatabase = ExperienceDatabase(requireContext())
        return experienceDatabase.checkComplete()
    }
    private fun setCompleteStatus(isComplete:Boolean,textView: TextView)
    {
        if(isComplete) {
            textView.setBackgroundResource(R.drawable.background_green_box)
            textView.setText("COMPLETE")
        }
        else
        {
            textView.setBackgroundResource(R.drawable.background_red_box)
            textView.setText("INCOMPLETE")
        }
    }

    companion object {
        private const val TAG = "HomeScreenFragment"
    }
}