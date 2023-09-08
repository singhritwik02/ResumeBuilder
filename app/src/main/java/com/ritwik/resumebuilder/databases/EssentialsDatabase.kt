package com.ritwik.resumebuilder.databases

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.ritwik.resumebuilder.datamodel.Essentials

class EssentialsDatabase(val context: Context) {
    fun getEssentials(): Essentials {
        val essentials = Essentials()
        with(context.getSharedPreferences("RESUME_BUILDER", Context.MODE_PRIVATE))
        {
            essentials.skills = getString(key_Skills, "") ?: ""
            essentials.languages = getString(key_Languages, "") ?: ""

        }
        return essentials
    }

    fun setEssentials(key: String, value: String) {

        with(context.getSharedPreferences("RESUME_BUILDER", MODE_PRIVATE).edit()) {

            putString(key, value)
            apply()
        }
    }

    companion object {
        val key_Skills = "SKILLS"
        val key_Languages = "LANGUAGES"
    }
}