package com.ritwik.resumebuilder.fragments

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintAttributes.Resolution
import android.print.PrintManager
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ritwik.resumebuilder.R
import com.ritwik.resumebuilder.databases.CertificationsDatabase
import com.ritwik.resumebuilder.databases.EducationDatabase
import com.ritwik.resumebuilder.databases.EssentialsDatabase
import com.ritwik.resumebuilder.databases.ExperienceDatabase
import com.ritwik.resumebuilder.databases.ProjectsDatabase
import com.ritwik.resumebuilder.databinding.FragmentPreviewBinding
import com.ritwik.resumebuilder.datamodel.Essentials
import com.ritwik.resumebuilder.datamodel.ResumeData
import com.ritwik.resumebuilder.helper.LoadingView
import java.net.URLEncoder


class ExportFragment : Fragment() {
    private lateinit var binding: FragmentPreviewBinding
    private lateinit var htmlContent: StringBuilder
    private var mInterstitialAd: InterstitialAd? = null
    private var readyToSave = false
    private var baseUrl = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreviewBinding.inflate(layoutInflater)
        val loadingView = LoadingView(requireContext())
        binding.root.post {
            loadingView.showWindow()
        }
        val webView = binding.webView
        // getting base url from arguments
        baseUrl = arguments?.getString(
            "layoutLink",
            "https://resumebuilderweb-4ddfa.web.app/resume_android.html"
        ) ?: "https://resumebuilderweb-4ddfa.web.app/resume_android.html"
        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),
            "ca-app-pub-7757208400023961/6005826215",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.i(TAG, "onAdLoaded")
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(requireActivity())
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.d(TAG, loadAdError.toString())
                    mInterstitialAd = null
                }
            })
        htmlContent = StringBuilder()
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

        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        WebStorage.getInstance().deleteAllData();
        // loading view setup

        webView.apply {
            // Configure related browser settings
            this.settings.loadsImagesAutomatically = true
            this.settings.javaScriptEnabled = true
            webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            // Configure the client to use when opening URLs
            webView.webChromeClient = webChromeClient

            webView.webViewClient = object:WebViewClient()
            {
                override fun onPageFinished(view: WebView?, url: String?) {
                    loadingView.hideWindow()
                }
            }

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

        webView.reload()
        Log.d(TAG, "onCreateView: Resume Url = ${finalUrl}")

        binding.fpvLoadingView.visibility = View.GONE
        binding.fpvSaveButton.setOnClickListener {

            createWebPrintJob(binding.webView)

        }
        return binding.root
    }



    private fun createWebPrintJob(webView: WebView) {

        // Get a PrintManager instance
        val printManager = requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager

        // Get a print adapter instance

        val printAdapter = webView.createPrintDocumentAdapter("resume")

        // Create a print job with name and adapter instance
        val jobName = getString(R.string.app_name) + " Document"
        printManager.print(jobName, printAdapter, null)


    }



    companion object {
        private const val TAG = "ExportFragment"
    }
}