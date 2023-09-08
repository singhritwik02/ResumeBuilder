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
import com.ritwik.resumebuilder.databases.ExperienceDatabase
import com.ritwik.resumebuilder.R
import com.ritwik.resumebuilder.databinding.FragmentExperienceBinding
import com.ritwik.resumebuilder.databinding.PopupAddExperienceBinding
import com.ritwik.resumebuilder.datamodel.Experience
import com.ritwik.resumebuilder.helper.DeleteConfirmationPopup


class ExperienceFragment : Fragment() {
    private lateinit var binding: FragmentExperienceBinding
    private var experienceDataList = arrayListOf<Experience>()
    private var recyclerAdapter: ExperienceRecyclerAdapter? = null
    private lateinit var experienceDatabase: ExperienceDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExperienceBinding.inflate(layoutInflater)
        requireActivity().window.statusBarColor = requireContext().getColor(R.color.darkPurple)
        requireActivity().window.navigationBarColor = requireContext().getColor(R.color.white)
        MobileAds.initialize(requireContext())
        val adView = binding.fexBottomBanner
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        experienceDatabase = ExperienceDatabase(requireContext())
        val recycler = binding.feRecyclerView
        recycler.layoutManager = LinearLayoutManager(requireContext())
        //
        recyclerAdapter = ExperienceRecyclerAdapter()
        recycler.adapter = recyclerAdapter
        //get data
        experienceDataList.clear()
        experienceDataList.addAll(experienceDatabase.getExperienceList())
        updateData()

        // Inflate the layout for this fragment

        binding.feAddButton.setOnClickListener {
            showAddExperiencePopup()
        }
        return binding.root
    }



    private fun showAddExperiencePopup() {
        val pBinding = PopupAddExperienceBinding.inflate(layoutInflater)
        val window = PopupWindow(
            pBinding.root,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        window.elevation = 100f
        window.showAtLocation(pBinding.root, Gravity.CENTER, 0, 0)
        pBinding.paeSaveButton.setOnClickListener {
            val experience = Experience()
            experience.company = pBinding.paexCompanyName.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Company Name!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            experience.location = pBinding.paexLocation.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Location!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            experience.jobTitle = pBinding.paexJobTitle.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Job Title!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            experience.description = pBinding.paexDescription.text.toString()
            // inserting data into local database
            experienceDatabase.insertData(experience)
            window.dismiss()
            updateData()

        }

    }
    private fun showUpdateExperiencePopup(itemIndex:Int) {
        val pBinding = PopupAddExperienceBinding.inflate(layoutInflater)
        val window = PopupWindow(
            pBinding.root,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        window.elevation = 100f
        // setting previous data
        pBinding.paexCompanyName.setText(experienceDataList[itemIndex].company)
        pBinding.paexLocation.setText(experienceDataList[itemIndex].location)
        pBinding.paexJobTitle.setText(experienceDataList[itemIndex].jobTitle)
        pBinding.paexDescription.setText(experienceDataList[itemIndex].description)
        // getting previous name
        val preName = experienceDataList[itemIndex].company
        window.showAtLocation(pBinding.root, Gravity.CENTER, 0, 0)
        pBinding.paeSaveButton.setOnClickListener {
            val experience = Experience()
            experience.company = pBinding.paexCompanyName.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Company Name!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            experience.location = pBinding.paexLocation.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Location!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            experience.jobTitle = pBinding.paexJobTitle.text.toString().ifEmpty {
                Toast.makeText(context, "Enter Job Title!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            experience.description = pBinding.paexDescription.text.toString()
            // inserting data into local database
            // deleting previous data
            experienceDatabase.deleteData(preName)
            experienceDatabase.insertData(experience)
            window.dismiss()
            updateData()

        }

    }

    fun updateData() {
        experienceDataList.clear()
        experienceDataList.addAll(experienceDatabase.getExperienceList())
        if (experienceDataList.size > 0) {
            binding.feLongPressLabel.visibility =View.VISIBLE
            binding.feFresherLayout.visibility = View.GONE
            binding.feRecyclerView.visibility = View.VISIBLE
            recyclerAdapter?.notifyDataSetChanged()

        } else {
            binding.feLongPressLabel.visibility =View.GONE
            binding.feFresherLayout.visibility = View.VISIBLE
            binding.feRecyclerView.visibility = View.GONE
            recyclerAdapter?.notifyDataSetChanged()
        }
    }

    inner class ExperienceRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val companyName = itemView.findViewById<TextView>(R.id.sed_CompanyName)
        private val location = itemView.findViewById<TextView>(R.id.sed_CompanyLocation)
        private val jobTitle = itemView.findViewById<TextView>(R.id.sed_JobTitle)
        private val jobDescription = itemView.findViewById<TextView>(R.id.sed_Description)

        fun bindView(experienceData: Experience)
        {
            companyName.text = "Company Name :- ${experienceData.company}"
            location.text = "Location :- ${experienceData.location}"
            jobTitle.text = "Job Title :- ${experienceData.jobTitle}"
            jobDescription.text = "Job Description :- ${experienceData.description}"

        }

    }

    inner class ExperienceRecyclerAdapter() : RecyclerView.Adapter<ExperienceRecyclerViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ExperienceRecyclerViewHolder {
            val view = layoutInflater.inflate(R.layout.single_experience_data, parent, false)
            return ExperienceRecyclerViewHolder(view)
        }

        override fun getItemCount(): Int {
            return experienceDataList.size
        }

        override fun onBindViewHolder(holder: ExperienceRecyclerViewHolder, position: Int) {
            holder.bindView(experienceDataList[position])
            holder.itemView.setOnClickListener {
                showUpdateExperiencePopup(position)
            }
            val deleteFunction:()->Unit= {
                experienceDatabase.deleteData(experienceDataList[position].company)
                notifyItemRemoved(position)
                experienceDataList.removeAt(position)
            }
            holder.itemView.setOnLongClickListener{
                // show delete confirmation popup
                val deleteConfirmation = DeleteConfirmationPopup(experienceDataList[position].jobTitle,requireContext(),deleteFunction)
                deleteConfirmation.showWindow()



                return@setOnLongClickListener false
            }
        }

    }

    companion object {
        val TABLE_NAME = "EXPERIENCE"
        val COLUMN_COMPANY_NAME = "COMPANY_NAME"
        val COLUMN_LOCATION = "LOCATION"
        val COLUMN_JOB_TITLE = "JOB_TITLE"
        val COLUMN_DESCRIPTION = "DESCRIPTION"

    }

}