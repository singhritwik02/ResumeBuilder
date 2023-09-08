package com.ritwik.resumebuilder.databases;

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ritwik.resumebuilder.datamodel.ProjectsData


class ProjectsDatabase(
    val context: Context?
) : SQLiteOpenHelper(context, "RESUME_BUILDER_PROJECTS.db", null, 1) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE PROJECTS (TITLE VARCHAR(30),DETAILS VARCHAR(100),DESCRIPTION VARCHAR(200));")
    }

    fun deleteData(projectTitle: String): Boolean {
        val selection = "TITLE" + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(projectTitle)
        // Issue SQL statement.
        val deletedRows = writableDatabase.delete("PROJECTS", selection, selectionArgs)
        return deletedRows > 0
    }

    fun insertData(projectName: String?, projectDetails: String?, projectDescription: String?) {
        val values = ContentValues()
        values.put("TITLE", projectName)
        values.put("DETAILS", projectDetails)
        values.put("DESCRIPTION", projectDescription)
        val rowId = this.writableDatabase.insert("PROJECTS", null, values)

    }

    // add to list
    fun getProjectsList(): ArrayList<ProjectsData> {
        val projectsDataList = ArrayList<ProjectsData>()
        val projection = arrayOf(
            "TITTLE",
            "DETAILS",
            "DESCRIPTION"
        )
        val data = this.readableDatabase.rawQuery("SELECT *FROM PROJECTS", null)
        var projectTitle: String?
        var projectDetails: String?
        var projectDescription: String?
        var tempData = ProjectsData()
        while (data.moveToNext()) {
            projectTitle = data.getString(data.getColumnIndexOrThrow("TITLE"))
            projectDetails = data.getString(data.getColumnIndexOrThrow("DETAILS"))
            projectDescription = data.getString(data.getColumnIndexOrThrow("DESCRIPTION"))
            tempData = ProjectsData()
            tempData.projectName = projectTitle
            tempData.projectDescription = projectDescription
            tempData.projectDetails = projectDetails
            // add to list
            projectsDataList.add(tempData)
        }
        return projectsDataList
    }
    fun checkComplete():Boolean
    {
        return getProjectsList().size >= 1
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE  IF EXISTS PROJECTS")
        onCreate(sqLiteDatabase)
    }
}