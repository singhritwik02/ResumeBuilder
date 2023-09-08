package com.ritwik.resumebuilder.datamodel

class Certification {
    var certificationTitle = ""
    var certificationTime = ""
    var certificationDescription = ""
    fun checkComplete():Boolean
    {
        return certificationTitle.isNotEmpty() && certificationTime.isNotEmpty() && certificationDescription.isNotEmpty()
    }
}