package com.ritwik.resumebuilder.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.storage.FirebaseStorage
import com.ritwik.resumebuilder.R
import com.ritwik.resumebuilder.databases.PersonalInformationDatabase
import com.ritwik.resumebuilder.databinding.FragmentChooseProfileImageBinding


class ChooseProfileImageFragment : Fragment() {
    private lateinit var binding: FragmentChooseProfileImageBinding
    private var selectedImageUri: Uri? = null
    private var loadingWindow: PopupWindow? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChooseProfileImageBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext())
        val adView = binding.cpifBottomBanner
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        // set status bar color to dark orange
        requireActivity().window.statusBarColor = requireContext().getColor(R.color.darkOrange)
        // set navigation bar color to white
        requireActivity().window.navigationBarColor = requireContext().getColor(R.color.white)
        val imageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                binding.fcpiImageView.setImageURI(it)
                selectedImageUri = it
            }
        }
        // getting profileimage from personal info database
        val personalInfoDatabase = PersonalInformationDatabase(requireContext())
        val personalInfo = personalInfoDatabase.getPersonalInformation()
        //setting profile image
        Log.d(TAG, "onCreateView: Profile Image Link = ${personalInfo.profileImageLink}")
        if (personalInfo.profileImageLink != "") {
            // loading image with glide
            Glide.with(requireContext()).load(personalInfo.profileImageLink)
                .centerCrop()
                .placeholder(R.drawable.wait_time_svgrepo_com).into(binding.fcpiImageView)


            Log.d(TAG, "onCreateView: Profile Image Link = ${personalInfo.profileImageLink}")
        }
        binding.fcpiImageView.setOnClickListener {
            imageLauncher.launch("image/*")
        }
        binding.fcpiSave.setOnClickListener {
            saveImage()

        }
        binding.fcpiDelete.setOnClickListener {

        }
        return binding.root
    }

    fun saveImage() {
        if (selectedImageUri == null) return
        // show loading window
        showLoadingWindow()
        // disable save button
        binding.fcpiSave.isEnabled =false
        // saving image to firebase storage
        FirebaseStorage.getInstance("gs://resumebuilderweb-4ddfa.appspot.com").reference.child("ProfileImages")
            .child(selectedImageUri!!.lastPathSegment.toString()).putFile(selectedImageUri!!)
            .addOnSuccessListener {
                // getting image download url
                FirebaseStorage.getInstance("gs://resumebuilderweb-4ddfa.appspot.com").reference.child(
                    "ProfileImages"
                )
                    .child(selectedImageUri!!.lastPathSegment.toString()).downloadUrl.addOnSuccessListener {
                        // saving image url to database
                        val personalInfoDatabase = PersonalInformationDatabase(requireContext())
                        personalInfoDatabase.setPersonalInfoItem(
                            PersonalInformationDatabase.profileImageLink, it.toString()

                        )
                        // hide loading window
                        Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
                        binding.fcpiSave.isEnabled = true
                        hideLoadingWindow()
                    }
            }
    }



    private fun showLoadingWindow() {
        val loadingView = layoutInflater.inflate(R.layout.popup_loading_view, null, false)
        loadingWindow = PopupWindow(
            loadingView,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            false
        )
        loadingWindow?.showAtLocation(loadingView, Gravity.CENTER, 0, 0)

    }

    private fun hideLoadingWindow() {
        if (loadingWindow != null) {
            if (loadingWindow?.isShowing == true) loadingWindow?.dismiss()
        }
    }

    companion object {
        private const val TAG = "ChooseProfileImageFragm"
    }
}