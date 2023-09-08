package com.ritwik.resumebuilder.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.ritwik.resumebuilder.datamodel.Experience


class ExperienceDatabase(val context: Context) :
    SQLiteOpenHelper(context, "RESUME_BUILDER_EXPERIENCE.DB", null, 1) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE $TABLE_NAME($COLUMN_COMPANY_NAME VARCHAR(30),$COLUMN_LOCATION VARCHAR(30),$COLUMN_JOB_TITLE VARCHAR(30),$COLUMN_DESCRIPTION VARCHAR(200));")
    }

    companion object {
        val TABLE_NAME = "EXPERIENCE"
        val COLUMN_COMPANY_NAME = "COMPANY_NAME"
        val COLUMN_LOCATION = "LOCATION"
        val COLUMN_JOB_TITLE = "JOB_TITLE"
        val COLUMN_DESCRIPTION = "DESCRIPTION"

    }
    fun deleteData(title: String): Boolean {
        val selection = "$COLUMN_COMPANY_NAME LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(title)
        // Issue SQL statement.
        val deletedRows = writableDatabase.delete("$TABLE_NAME", selection, selectionArgs)
        return deletedRows > 0
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, p1: Int, p2: Int) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ${TABLE_NAME}")
        onCreate(sqLiteDatabase)
    }

    fun insertData(experienceData: Experience) {
        val values = ContentValues()
        values.put("${COLUMN_COMPANY_NAME}", experienceData.company)
        values.put("${COLUMN_LOCATION}", experienceData.location)
        values.put("${COLUMN_JOB_TITLE}", experienceData.jobTitle)
        values.put("${COLUMN_DESCRIPTION}", experienceData.description)
        val rowId = this.writableDatabase.insert(TABLE_NAME, null, values)

    }

    // add to list
    fun getExperienceList(): ArrayList<Experience> {
        var experienceList = ArrayList<Experience>()
        val projection = arrayOf(
            "${COLUMN_COMPANY_NAME}",
            "${COLUMN_LOCATION}",
            "${COLUMN_JOB_TITLE}",
            "${COLUMN_DESCRIPTION}"
        )
        val data =
            this.readableDatabase.rawQuery("SELECT *FROM $TABLE_NAME", null)

        var tempData = Experience()
        if (data.count < 1) {
            return experienceList
        }
        while (data.moveToNext()) {
            tempData = Experience()
            tempData.company = data.getString(
                data.getColumnIndexOrThrow(
                    COLUMN_COMPANY_NAME
                )
            )
            tempData.location = data.getString(
                data.getColumnIndexOrThrow(
                    COLUMN_LOCATION
                )
            )
            tempData.jobTitle =
                data.getString(data.getColumnIndexOrThrow(COLUMN_JOB_TITLE))
            tempData.description =
                data.getString(data.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            // add to list
            experienceList.add(tempData)


        }
        // checking and setting fresher status
        val personalInformationDatabase = PersonalInformationDatabase(context)
        if(experienceList.size == 0)
        {
            // setting isFresher to yes
            personalInformationDatabase.setPersonalInfoItem(PersonalInformationDatabase.keyIsFresher, "YES")


        }
        else
        {
            // setting isFresher to no
            personalInformationDatabase.setPersonalInfoItem(PersonalInformationDatabase.keyIsFresher, "NO")
        }
        return experienceList

    }
    fun checkComplete():Boolean
    {
        val list = getExperienceList()
        if(list.size == 0)
        {
            return false
        }
        return true
    }
}