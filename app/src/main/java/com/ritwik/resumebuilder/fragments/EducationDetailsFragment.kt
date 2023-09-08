package com.ritwik.resumebuilder.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

import com.ritwik.resumebuilder.databases.EducationDatabase

import com.ritwik.resumebuilder.R
import com.ritwik.resumebuilder.databinding.FragmentEducationDetailsBinding
import com.ritwik.resumebuilder.databinding.PopupAddEducationInfoBinding
import com.ritwik.resumebuilder.datamodel.Education
import com.ritwik.resumebuilder.helper.DeleteConfirmationPopup

class EducationDetailsFragment : Fragment() {
    var educationDataList = arrayListOf<Education>()
    var educationDatabase: EducationDatabase? = null
    private lateinit var recyclerAdapter: EducationRecyclerAdapter

    private lateinit var binding: FragmentEducationDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEducationDetailsBinding.inflate(layoutInflater)
        activity?.window?.statusBarColor = context?.getColor(R.color.darkOrange)!!
        requireActivity().window.navigationBarColor = requireContext().getColor(R.color.white)
        MobileAds.initialize(requireContext())
        val ad = binding.fedBottomBanner
        val adRequest = AdRequest.Builder().build()
        ad.loadAd(adRequest)
        // Inflate the layout for this fragment
        val recycler = binding.fedRecyclerView
        recycler.layoutManager = LinearLayoutManager(context)
        recyclerAdapter = EducationRecyclerAdapter()
        recycler.adapter = recyclerAdapter
        educationDatabase = EducationDatabase(requireContext())
        educationDataList.clear()
        educationDatabase?.getEducationList()?.let { educationDataList.addAll(it) }
        updateAdapter()
        binding.fedAddButton.setOnClickListener {
            // start education activity
            showAddEducationInfo()
        }
        return binding.root
    }

    fun updateAdapter() {
        educationDataList.clear()
        educationDatabase?.getEducationList()?.let { educationDataList.addAll(it) }
        if (educationDataList.size > 0) {
            binding.fedLongPressLabel.visibility = View.VISIBLE
            binding.fedEmptyLayout.visibility = View.GONE
        } else {

            binding.fedLongPressLabel.visibility = View.GONE
            binding.fedEmptyLayout.visibility = View.VISIBLE
        }
        if (this::recyclerAdapter.isInitialized) {
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun showAddEducationInfo() {
        val popupBinding: PopupAddEducationInfoBinding =
            PopupAddEducationInfoBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            popupBinding.getRoot(),
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 100f
        popupWindow.showAtLocation(popupBinding.getRoot(), Gravity.CENTER, 0, 0)
        popupBinding.paeSaveButton.setOnClickListener {
            val education = Education()
            education.degreeName = popupBinding.paeQualification.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Degree Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            education.degreeTimePeriod = popupBinding.paeDetails.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Time Period", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            education.schoolUnivName = popupBinding.paeSchoolUnivName.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            education.degreeDescription = popupBinding.paeDescription.text.toString().ifEmpty {
                ""
            }
            if (educationDatabase == null) educationDatabase = EducationDatabase(requireContext())
            educationDatabase!!.insertData(education)
            popupWindow.dismiss()
            updateAdapter()
        }
        // update adapter
        // TODO:   updateAdapter()
        // notifyDataChanged();


    }

    private fun showUpdateEducationInfo(itemIndex: Int) {
        val popupBinding: PopupAddEducationInfoBinding =
            PopupAddEducationInfoBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            popupBinding.getRoot(),
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 100f
        // filling up fields with previous values
        popupBinding.paeTitle.setText("Update ${(educationDataList[itemIndex].degreeName)}")
        val preDegreeName = educationDataList[itemIndex].degreeName
        popupBinding.paeQualification.setText(educationDataList[itemIndex].degreeName)
        popupBinding.paeDetails.setText(educationDataList[itemIndex].degreeTimePeriod)
        popupBinding.paeSchoolUnivName.setText(educationDataList[itemIndex].schoolUnivName)
        popupBinding.paeDescription.setText(educationDataList[itemIndex].degreeDescription)
        popupWindow.showAtLocation(popupBinding.getRoot(), Gravity.CENTER, 0, 0)

        popupBinding.paeSaveButton.setOnClickListener {
            val education = Education()
            education.degreeName = popupBinding.paeQualification.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Degree Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            education.degreeTimePeriod = popupBinding.paeDetails.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Time Period", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            education.schoolUnivName = popupBinding.paeSchoolUnivName.text.toString().ifEmpty {
                Toast.makeText(context, "Enter School/University Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            education.degreeDescription = popupBinding.paeDescription.text.toString().ifEmpty {
                ""
            }
            if (educationDatabase == null) educationDatabase = EducationDatabase(requireContext())
            // deleting the current data
            educationDatabase!!.deleteData(preDegreeName)
            educationDatabase!!.insertData(education)
            popupWindow.dismiss()
            updateAdapter()
        }
        // update adapter
        // TODO:   updateAdapter()
        // notifyDataChanged();


    }


    inner class EducationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val degreeName = itemView.findViewById<TextView>(R.id.sei_QualificationName)
        private val degreeDetails = itemView.findViewById<TextView>(R.id.sei_Details)
        private val degreeLocation = itemView.findViewById<TextView>(R.id.sei_Location)
        private val degreeDescription = itemView.findViewById<TextView>(R.id.sei_Description)

        fun bindView(education: Education) {
            degreeName.text = "Education :- " + education.degreeName
            degreeDetails.text = "Time Period :- " + education.degreeTimePeriod
            degreeLocation.text = "School/Univ :- " + education.schoolUnivName
            degreeDescription.text = "Description :- " + education.degreeDescription

        }
    }

    inner class EducationRecyclerAdapter : RecyclerView.Adapter<EducationViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EducationViewHolder {
            val view = layoutInflater.inflate(R.layout.single_education_item, parent, false)
            return EducationViewHolder(view)
        }

        override fun getItemCount(): Int {
            return educationDataList.size
        }

        override fun onBindViewHolder(holder: EducationViewHolder, position: Int) {
            holder.bindView(educationDataList[position])
            holder.itemView.setOnClickListener {
                showUpdateEducationInfo(position)
            }
            holder.itemView.setOnLongClickListener {
                val deleteFunction: () -> Unit = {
                    educationDatabase?.deleteData(educationDataList[position].degreeName)
                    notifyItemRemoved(position)
                    educationDataList.removeAt(position)
                }
                val deleteConfirmation = DeleteConfirmationPopup(educationDataList[position].degreeName,requireContext(), deleteFunction)
                deleteConfirmation.showWindow()
                return@setOnLongClickListener false
            }
        }

    }


}