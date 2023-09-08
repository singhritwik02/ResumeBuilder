package com.ritwik.resumebuilder.databases

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.util.Log
import com.ritwik.resumebuilder.datamodel.PersonalInfo

class PersonalInformationDatabase(val context: Context) {
    fun getPersonalInformation(): PersonalInfo {
        var personalInfo = PersonalInfo()
        val sharedPrefManager = context.getSharedPreferences("RESUME_BUILDER", MODE_PRIVATE)
        with(sharedPrefManager) {
            personalInfo.name = getString(key_Name, "")
            personalInfo.jobTitle = getString(key_Job, "")
            personalInfo.addressLine1 = getString(key_AddressLine1, "")
            personalInfo.addressLine2 = getString(key_AddressLine2, "")
            personalInfo.email = getString(key_Email, "")
            personalInfo.phone = getString(key_Phone, "")
            personalInfo.introLine = getString(key_IntroLine, "")
            personalInfo.profileImageLink = getString(profileImageLink,"")

        }
        return personalInfo
    }
    fun checkProfileImage():Boolean
    {
        val sharedPrefManager = context.getSharedPreferences("RESUME_BUILDER", MODE_PRIVATE)
        if(sharedPrefManager.contains(profileImageLink))
        {
            val profileImageLink = sharedPrefManager.getString(profileImageLink,"")?:""
            if(profileImageLink.isNotEmpty())
            {
                return true
            }
            return false
        }
        else
        {
            return false
        }
    }

    fun setPersonalInfoItem(key: String, value: String) {
        val sharedPrefManager = context.getSharedPreferences("RESUME_BUILDER", MODE_PRIVATE)
        with(sharedPrefManager.edit())
        {
            putString(key, value)
            apply()
            Log.d(TAG, "setPersonalInfoItem: key = $key value = $value")
        }


    }

    fun checkComplete():Boolean
    {
        val sharedPrefManager = context.getSharedPreferences("RESUME_BUILDER", MODE_PRIVATE)
        if(sharedPrefManager.contains(key_Name) && sharedPrefManager.contains(key_Job)
            && sharedPrefManager.contains(key_AddressLine1) && sharedPrefManager.contains(key_AddressLine2)
            && sharedPrefManager.contains(key_Email) && sharedPrefManager.contains(key_Phone)
            && sharedPrefManager.contains(key_IntroLine) )
        {
            return true
        }
        return false

    }


    companion object {
        val key_Name = "NAME"
        val key_Job = "JOB"
        val key_AddressLine1 = "ADDRESS_1"
        val key_AddressLine2 = "ADDRESS_2"
        val key_Email = "EMAIL"
        val key_Phone = "PHONE"
        val key_IntroLine = "INTRO_LINE"
        val profileImageLink = "PROFILE_IMAGE_URI"
        val keyIsFresher = "IS_FRESHER"
        private const val TAG = "PersonalInformationData"
    }
}