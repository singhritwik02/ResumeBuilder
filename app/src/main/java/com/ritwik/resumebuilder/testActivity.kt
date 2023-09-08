package com.ritwik.resumebuilder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.File
import java.io.FileWriter

class testActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }
    private fun createFile(){
        val file = File("test.html")
        val writer = FileWriter(file)
        writer.append("vrbvkbekv")
        writer.close()
        writer.flush()

    }
}