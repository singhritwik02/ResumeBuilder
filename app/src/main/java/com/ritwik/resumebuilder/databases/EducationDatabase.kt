package com.ritwik.resumebuilder.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ritwik.resumebuilder.datamodel.Education


class EducationDatabase(val context: Context) :
    SQLiteOpenHelper(context, "RESUME_BUILDER_EDUCATION.db", null, 1) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE $TABLE_NAME ($COLUMN_TITLE VARCHAR(30),$COLUMN_LOCATION VARCHAR(100),$COLUMN_DETAILS VARCHAR(100),$COLUMN_DESCRIPTION VARCHAR(200));")
    }

    fun deleteData(title: String): Boolean {
        val selection = "$COLUMN_TITLE LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(title)
        // Issue SQL statement.
        val deletedRows = writableDatabase.delete("$TABLE_NAME", selection, selectionArgs)
        return deletedRows > 0
    }

    fun insertData(educationDetails: Education) {
        val values = ContentValues()
        values.put("$COLUMN_TITLE", educationDetails.degreeName)
        values.put("$COLUMN_LOCATION", educationDetails.schoolUnivName)
        values.put("$COLUMN_DETAILS", educationDetails.degreeTimePeriod)
        values.put("$COLUMN_DESCRIPTION", educationDetails.degreeDescription)
        val rowId = this.writableDatabase.insert(TABLE_NAME, null, values)


    }

    // add to list
    fun getEducationList(): ArrayList<Education> {
        var educationList = ArrayList<Education>()
        val projection = arrayOf(
            "$COLUMN_TITLE",
            "$COLUMN_DETAILS",
            "$COLUMN_LOCATION",
            "$COLUMN_DESCRIPTION"
        )

        val data = this.readableDatabase.rawQuery("SELECT *FROM $TABLE_NAME", null)

        var tempData = Education()
        if (data.count < 1) {
            return educationList
        }
        while (data.moveToNext()) {
            tempData = Education()
            tempData.degreeName = data.getString(data.getColumnIndexOrThrow(COLUMN_TITLE))
            tempData.degreeTimePeriod = data.getString(data.getColumnIndexOrThrow(COLUMN_DETAILS))
            tempData.schoolUnivName =
                data.getString(data.getColumnIndexOrThrow(COLUMN_LOCATION))
            tempData.degreeDescription =
                data.getString(data.getColumnIndexOrThrow(COLUMN_DESCRIPTION))


            // add to list
            educationList.add(tempData)


        }
        return educationList
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE  IF EXISTS $TABLE_NAME")
        onCreate(sqLiteDatabase)
    }

    companion object {
        private val TABLE_NAME = "EDUCATION"
        private val COLUMN_TITLE = "TITLE"
        private val COLUMN_DESCRIPTION = "DESCRIPTION"
        private val COLUMN_DETAILS = "DETAILS"
        private val COLUMN_LOCATION = "LOCATION"


    }
    fun checkComplete():Boolean
    {
        val list = getEducationList()
        if (list.size == 0)
        {
            return false
        }
        return true
    }

}