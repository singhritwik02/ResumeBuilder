package com.ritwik.resumebuilder.datamodel

import android.content.Context
import com.ritwik.resumebuilder.databases.EducationDatabase
import com.ritwik.resumebuilder.databases.EssentialsDatabase
import com.ritwik.resumebuilder.databases.PersonalInformationDatabase
import com.ritwik.resumebuilder.databases.ProjectsDatabase

class ResumeData(val context: Context) {

    var educationList = getEducationDetailsList()
    var projectList = getProjectsList()
    var experienceList = getExperienceDetailsList()
    // personal Information
    val personalInfo = getPersonalInformation()
    var name =personalInfo.name
    var email = personalInfo.email
    var addressLine1 = personalInfo.addressLine1
    var addressLine2 = personalInfo.addressLine2
    var phone = personalInfo.phone
    var jobTitle = personalInfo.jobTitle
    // essential Information
    var essentialData = getEssentialInformation()
    var skills = essentialData.skills
    var languages = essentialData.languages
    // project information

    private fun getPersonalInformation():PersonalInfo
    {
        val personalDatabase = PersonalInformationDatabase(context)
        return personalDatabase.getPersonalInformation()
    }
    private fun getEssentialInformation():Essentials
    {
        val essentialDatabase = EssentialsDatabase(context)
        return essentialDatabase.getEssentials()
    }
    private fun getProjectsList():ArrayList<ProjectsData>
    {
        val projectDatabase = ProjectsDatabase(context)
        return projectDatabase.getProjectsList()
    }
    private fun getEducationDetailsList():ArrayList<Education>
    {
        val educationDatabase = EducationDatabase(context)
        return educationDatabase.getEducationList()
    }
    private fun getExperienceDetailsList():ArrayList<Experience>
    {
        return ArrayList<Experience>()
    }
}