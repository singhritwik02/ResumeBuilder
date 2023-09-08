package com.ritwik.resumebuilder.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ritwik.resumebuilder.datamodel.Certification

class CertificationsDatabase(val context: Context):
    SQLiteOpenHelper(context, "RESUME_BUILDER_CERTIFICATIONS.db", null, 1) {

    private val tableName = "CERTIFICATIONS"


    override fun onCreate(p0: SQLiteDatabase?) {
        p0!!.execSQL("CREATE TABLE $tableName ($columnCertificationTitle VARCHAR(30),$columnCertificationTime VARCHAR(100),$columnCertificationDescription VARCHAR(100));")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.let {sqLiteDatabase->
            sqLiteDatabase.execSQL("DROP TABLE  IF EXISTS $tableName")
            // create table again
            sqLiteDatabase.execSQL("CREATE TABLE $tableName ($columnCertificationTitle VARCHAR(30),$columnCertificationTime VARCHAR(100),$columnCertificationDescription VARCHAR(100));")

        }
    }
    fun delete(title: String): Boolean {
        val selection = "$columnCertificationTitle LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(title)
        // Issue SQL statement.
        val deletedRows = writableDatabase.delete("$tableName", selection, selectionArgs)
        return deletedRows > 0
    }
    fun insertData(certification: Certification)
    {
        val values = ContentValues()
        with(values)
        {
            put(columnCertificationTitle,certification.certificationTitle)
            put(columnCertificationTime,certification.certificationTime)
            put(columnCertificationDescription,certification.certificationDescription)

        }
        writableDatabase.insert(tableName,null,values)
    }
    fun getCertificationList():ArrayList<Certification>
    {
        val certificationList = ArrayList<Certification>()

        val cursor = readableDatabase.rawQuery("SELECT * FROM $tableName",null)
        var tempCertification = Certification()
        if(cursor.count < 1)
        {
            return certificationList
        }
        while (cursor.moveToNext())
        {
            tempCertification = Certification()
            tempCertification.certificationTitle = cursor.getString(cursor.getColumnIndexOrThrow(columnCertificationTitle))
            tempCertification.certificationTime = cursor.getString(cursor.getColumnIndexOrThrow(columnCertificationTime))
            tempCertification.certificationDescription = cursor.getString(cursor.getColumnIndexOrThrow(columnCertificationDescription))
            certificationList.add(tempCertification)
        }
        return certificationList
    }
    companion object
    {
        private val columnCertificationTitle: String = "CERTIFICATION_TITLE"
        private val columnCertificationTime:String = "CERTIFICATION_TIME"
        private val columnCertificationDescription:String = "CERTIFICATION_DESCRIPTION"
    }

}