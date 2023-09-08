package com.ritwik.resumebuilder.fragments

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.ritwik.resumebuilder.R
import com.ritwik.resumebuilder.databases.CertificationsDatabase
import com.ritwik.resumebuilder.databinding.FragmentCertificationsBinding
import com.ritwik.resumebuilder.databinding.PopupAddCertificationBinding
import com.ritwik.resumebuilder.datamodel.Certification
import com.ritwik.resumebuilder.helper.DeleteConfirmationPopup


class CertificationsFragment : Fragment() {

    private lateinit var binding: FragmentCertificationsBinding
    private lateinit var recyclerView: RecyclerView
    private var recyclerAdapter = CertificationRecyclerAdapter()
    private lateinit var certificationDatabase: CertificationsDatabase
    private val certificationList = ArrayList<Certification>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCertificationsBinding.inflate(layoutInflater)
        requireActivity().window.statusBarColor = resources.getColor(R.color.darkYellow, null)
        prepareRecyclerView()
        binding.fcAddCertificationButton.setOnClickListener {
            showAddCertificationPopup()
        }
        MobileAds.initialize(requireContext())
        val adView = binding.fcBottomBanner
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        return binding.root
    }

    private fun prepareRecyclerView() {
        recyclerView = binding.fcRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        certificationDatabase = CertificationsDatabase(requireContext())
        // getting list
        certificationList.clear()
        certificationList.addAll(certificationDatabase.getCertificationList())

        recyclerView.adapter = recyclerAdapter

        recyclerAdapter.notifyDataChanged()

    }

    inner class CertificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val certificationTitle = itemView.findViewById<TextView>(R.id.sc_CertificationTitle)
        private val certificationTime = itemView.findViewById<TextView>(R.id.sc_CertificationTime)
        private val certificationDescription =
            itemView.findViewById<TextView>(R.id.sc_CertificationDescription)

        fun bindCertification(certification: Certification) {
            certificationTitle.text = certification.certificationTitle
            certificationTime.text = "Time Period :- " + certification.certificationTime
            certificationDescription.text =
                "Description :- " + certification.certificationDescription

        }

    }

    inner class CertificationRecyclerAdapter : RecyclerView.Adapter<CertificationViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificationViewHolder {
            val view = layoutInflater.inflate(R.layout.single_certficiation, parent, false)
            return CertificationViewHolder(view)
        }

        override fun getItemCount(): Int {
            return certificationList.size
        }

        override fun onBindViewHolder(holder: CertificationViewHolder, position: Int) {
            holder.bindCertification(certificationList[position])
            holder.itemView.setOnLongClickListener {
                val deleteConfirmationPopup = DeleteConfirmationPopup(
                    certificationList[position].certificationTitle,
                    requireContext()
                ) {
                    val toDeleteTitle = certificationList[position].certificationTitle
                    if (certificationDatabase.delete(toDeleteTitle)) {
                        Toast.makeText(
                            requireContext(),
                            "Deleted ${toDeleteTitle}",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateData()
                    }

                }
                deleteConfirmationPopup.showWindow()
                return@setOnLongClickListener true
            }
            holder.itemView.setOnClickListener {
                showUpdatePopup(position)
            }

        }

        fun notifyDataChanged() {
            if (itemCount < 1) {
                // show empty layout
                showEmptyLayout()
            } else {
                // hide empty layout
                hideEmptyLayout()

            }
            notifyDataSetChanged()
        }

    }


    companion object {
        private const val TAG = "CertificationsFragment"
    }

    private fun updateData() {
        certificationList.clear()
        certificationList.addAll(certificationDatabase.getCertificationList())
        recyclerAdapter.notifyDataChanged()
    }

    private fun showAddCertificationPopup() {
        val viewBinding = PopupAddCertificationBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            viewBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        val titleField = viewBinding.pacCertificationTitle
        val timeField = viewBinding.pacCertificationTime
        val descriptionField = viewBinding.pacCertificationDescription
        popupWindow.showAtLocation(viewBinding.root, Gravity.CENTER, 0, 0)
        val certification = Certification()
        titleField.addTextChangedListener {
            certification.certificationTitle = titleField.text.toString()
        }
        timeField.addTextChangedListener {
            certification.certificationTime = timeField.text.toString()
        }
        descriptionField.addTextChangedListener {
            certification.certificationDescription = descriptionField.text.toString()
        }
        viewBinding.pacSaveButton.setOnClickListener {
            if (certification.checkComplete()) {
                certificationDatabase.insertData(certification)
                updateData()
                popupWindow.dismiss()
            }
        }


    }

    private fun showUpdatePopup(position: Int) {
        val viewBinding = PopupAddCertificationBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            viewBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        val titleField = viewBinding.pacCertificationTitle
        val timeField = viewBinding.pacCertificationTime
        val descriptionField = viewBinding.pacCertificationDescription
        // getting current
        val currentCertification = certificationList[position]
        // filling up certification
        val prevTitle = currentCertification.certificationTitle
        titleField.setText(currentCertification.certificationTitle)
        timeField.setText(currentCertification.certificationTime)
        descriptionField.setText(currentCertification.certificationDescription)

        popupWindow.showAtLocation(viewBinding.root, Gravity.CENTER, 0, 0)

        titleField.addTextChangedListener {
            currentCertification.certificationTitle = titleField.text.toString()
        }
        timeField.addTextChangedListener {
            currentCertification.certificationTime = timeField.text.toString()
        }
        descriptionField.addTextChangedListener {
            currentCertification.certificationDescription = descriptionField.text.toString()
        }
        viewBinding.pacSaveButton.setOnClickListener {
            // delete current entry
            certificationDatabase.delete(prevTitle)
            // adding new data
            certificationDatabase.insertData(currentCertification)
            updateData()
            // dismiss
            popupWindow.dismiss()
        }


    }

    private fun hideEmptyLayout() {
        // hide empty layout
        binding.fcEmptyLayout.visibility = View.GONE
        binding.fcRecyclerView.visibility = View.VISIBLE
    }

    private fun showEmptyLayout() {
        // show empty layout
        binding.fcEmptyLayout.visibility = View.VISIBLE
        binding.fcRecyclerView.visibility = View.GONE
    }
}