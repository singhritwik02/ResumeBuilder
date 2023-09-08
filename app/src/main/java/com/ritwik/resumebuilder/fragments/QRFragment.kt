package com.ritwik.resumebuilder.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.ritwik.resumebuilder.R
import com.ritwik.resumebuilder.databases.CertificationsDatabase
import com.ritwik.resumebuilder.databases.EducationDatabase
import com.ritwik.resumebuilder.databases.EssentialsDatabase
import com.ritwik.resumebuilder.databases.ExperienceDatabase
import com.ritwik.resumebuilder.databases.ProjectsDatabase
import com.ritwik.resumebuilder.databinding.FragmentQrBinding
import com.ritwik.resumebuilder.datamodel.Essentials
import com.ritwik.resumebuilder.datamodel.ResumeData
import com.ritwik.resumebuilder.datamodel.ResumeLayout
import java.net.URLEncoder


class QRFragment : Fragment() {
    private var layoutList = ArrayList<ResumeLayout>()
    private lateinit var binding: FragmentQrBinding
    private var currentQRLink = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQrBinding.inflate(layoutInflater)
        // set status bar color to darkpurple
        requireActivity().window.statusBarColor = requireContext().getColor(R.color.darkPurple)

        val layoutsRecycler = binding.fqrLayoutRecycler
        layoutsRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        layoutsRecycler.adapter = LayoutRecyclerAdapter()
        // getting layout links
        getLayouts {
            layoutList.clear()
            layoutList.addAll(it)
            binding.fqrLayoutRecycler.adapter?.notifyDataSetChanged()
        }
        binding.fqrPreviewButton.setOnClickListener {
            if(currentQRLink.isEmpty())
            {
                Toast.makeText(requireContext(), "Choose Layout", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // open preview fragment
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val fragment = PreviewQRFragment()
            val bundles = Bundle()
            bundles.putString("qrLink", currentQRLink)
            fragment.arguments = bundles
            container?.id?.let { it1 -> transaction.replace(it1, fragment) }
            transaction.commit()
        }
        binding.root.post {
            showQRIntro()
        }
        return binding.root
    }

    companion object {
        private const val TAG = "QRFragment"
    }

    inner class LayoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val previewImage = itemView.findViewById<ImageView>(R.id.slph_PreviewImage)
        private val loadingText = itemView.findViewById<TextView>(R.id.slph_LoadingImage)


        // function to set image
        fun setImage(previewLink: String) {
            // loading image with glide
            Glide.with(requireContext()).load(previewLink).fitCenter().into(previewImage)
            loadingText.visibility = View.GONE
        }
    }

    inner class LayoutRecyclerAdapter : RecyclerView.Adapter<LayoutViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutViewHolder {
            val view = layoutInflater.inflate(R.layout.single_layout_preview_horizontal, parent, false)
            return LayoutViewHolder(view)
        }

        override fun getItemCount(): Int {
            return if(layoutList.size>0) layoutList.size else 5
        }

        override fun onBindViewHolder(holder: LayoutViewHolder, position: Int) {
            if (layoutList.size == 0)
                return
            holder.setImage(layoutList[position].layoutPreviewLink)
            holder.itemView.setOnClickListener {
                binding.fqrWebView.clearHistory()
                currentQRLink =   buildResume(layoutList[position].layoutLink)

            }
        }

    }

    private fun getLayouts(function: (ArrayList<ResumeLayout>) -> Unit) {
        FirebaseDatabase.getInstance("https://resumebuilderweb-4ddfa-default-rtdb.firebaseio.com/")
            .reference.child("Layouts")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            return
                        }
                        val layouts = ArrayList<ResumeLayout>()
                        var tempLayout = ResumeLayout()
                        for (layout in snapshot.children) {
                            tempLayout = ResumeLayout()
                            if (layout.hasChild("LayoutLink"))
                                tempLayout.layoutLink = layout.child("LayoutLink").value.toString()
                            if (layout.hasChild("LayoutPreviewLink"))
                                tempLayout.layoutPreviewLink =
                                    layout.child("LayoutPreviewLink").value.toString()
                            layouts.add(tempLayout)


                        }
                        function(layouts)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG, "onCancelled: ${error.message}")
                    }

                }
            )

    }
    private fun buildResume(baseUrl:String):String
    {
        binding.fqrChooseLayoutTag.visibility = View.GONE
        var htmlContent = StringBuilder()
        val resumeData = ResumeData(requireContext())
        val personalInfo = resumeData.personalInfo
        var essentials = Essentials()
        val essentialsDatabase = EssentialsDatabase(requireContext())
        essentials = essentialsDatabase.getEssentials()
        val educationList = EducationDatabase(requireContext()).getEducationList()
        val experienceDatabase = ExperienceDatabase(requireContext())
        val experienceList = experienceDatabase.getExperienceList()
        val projectList = ProjectsDatabase(requireContext()).getProjectsList()
        val resumeUrl = StringBuilder()
        resumeUrl.append(baseUrl)

        // adding name
        resumeUrl.append("?name=" + personalInfo.name)
        // adding email
        resumeUrl.append("&Email=" + personalInfo.email)
        // adding address 1
        resumeUrl.append("&address_1=" + personalInfo.addressLine1)
        // adding address 2
        resumeUrl.append("&address_2=" + personalInfo.addressLine2)
        // adding phone
        resumeUrl.append("&phone=" + personalInfo.phone)
        // adding intro line
        resumeUrl.append("&introLine=" + personalInfo.introLine)
        // adding profile image link
        resumeUrl.append(
            "&profileImageLink=" + URLEncoder.encode(
                personalInfo.profileImageLink,
                "UTF-8"
            )
        )
        // getting education count
        val educationCount = educationList.size
        resumeUrl.append("&educationCount=" + educationCount)
        for (i in 1..educationCount) {
            resumeUrl.append("&education_title_" + i + "=" + educationList[i - 1].degreeName)
            resumeUrl.append("&education_school_" + i + "=" + educationList[i - 1].schoolUnivName)
            resumeUrl.append("&education_time" + i + "=" + educationList[i - 1].degreeTimePeriod)
            resumeUrl.append("&education_description_" + i + "=" + educationList[i - 1].degreeDescription)
        }
        // adding skills
        resumeUrl.append("&skills=" + essentials.skills)
        // adding languages
        resumeUrl.append("&languages=" + essentials.languages)
        // getting experience count
        val experienceCount = experienceList.size
        // appending experience count
        resumeUrl.append("&experienceCount=" + experienceCount)

        for (i in 1..experienceCount) {
            resumeUrl.append("&job_title_" + i + "=" + experienceList[i - 1].jobTitle)
            resumeUrl.append("&company_name_" + i + "=" + experienceList[i - 1].company)
            resumeUrl.append("&time_period_" + i + "=" + experienceList[i - 1].location)
            resumeUrl.append("&description_" + i + "=" + experienceList[i - 1].description)

        }
        // getting projects count
        val projectCount = projectList.size
        // appending projects count
        resumeUrl.append("&projectsCount=" + projectCount)
        for (i in 1..projectCount) {
            resumeUrl.append("&project_name_" + i + "=" + projectList[i - 1].projectName)
            // adding project details
            resumeUrl.append("&project_details_" + i + "=" + projectList[i - 1].projectDetails)
            resumeUrl.append("&description_" + i + "=" + projectList[i - 1].projectDescription)

        }
        // checking certifications
        val certificationDatabase = CertificationsDatabase(requireContext())
        val certificationList = certificationDatabase.getCertificationList()
        if (certificationList.isNotEmpty()) {
            // appending certifications count
            resumeUrl.append("&certificationCount=" + certificationList.size)
            // adding certifications
            for (i in 1..certificationList.size) {
                resumeUrl.append("&certification_title_" + i + "=" + certificationList[i - 1].certificationTitle)
                resumeUrl.append("&certification_time_" + i + "=" + certificationList[i - 1].certificationTime)
                resumeUrl.append("&certification_description_" + i + "=" + certificationList[i - 1].certificationDescription)

            }
        }
        resumeUrl.append("&submitButton=Submit")
        var finalUrl = resumeUrl.toString()
        // replacing every space with + in finalUrl
        finalUrl = finalUrl.replace(" ", "+")
        val webView = binding.fqrWebView
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        WebStorage.getInstance().deleteAllData();
        webView.apply {
            // Configure related browser settings
            this.settings.loadsImagesAutomatically = true
            this.settings.javaScriptEnabled = true
            webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            // Configure the client to use when opening URLs
            webView.webChromeClient = webChromeClient
            webView.webViewClient = WebViewClient()
            // Load the initial URL
        }
        // Enable responsive layout
        webView.settings.useWideViewPort = true
        // Zoom out if the content width is greater than the width of the viewport
        webView.settings.loadWithOverviewMode = true
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        val newUA = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0"
        webView.getSettings().setUserAgentString(newUA)

        webView.loadUrl(finalUrl)

        Log.d(TAG, "onCreateView: Resume Url = ${finalUrl}")
        return finalUrl
    }
    private fun showQRIntro()
    {
        val sharedPref = requireActivity().getSharedPreferences("QRIntro", Context.MODE_PRIVATE)
        if(!sharedPref.contains("FirstRun"))
        {
         showQRIntroPopup()
        }


    }
    private fun showQRIntroPopup()
    {
        val sharedPref = requireActivity().getSharedPreferences("QRIntro", Context.MODE_PRIVATE)
       with(sharedPref.edit())
       {
           putBoolean("FirstRun",false)
           commit()
       }
        val view = layoutInflater.inflate(R.layout.popup_qr_intro,null,false)
        val popupWindow = PopupWindow(
            view,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 100f
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val continueButton = view.findViewById<TextView>(R.id.pqri_ContinueButton)
        continueButton.setOnClickListener {
            popupWindow.dismiss()
        }


    }

}