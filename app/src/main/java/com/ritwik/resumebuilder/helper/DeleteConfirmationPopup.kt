package com.ritwik.resumebuilder.helper

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupWindow
import com.ritwik.resumebuilder.databinding.PopupDeleteConfirmationBinding

class DeleteConfirmationPopup {
    private lateinit var binding:PopupDeleteConfirmationBinding
    private lateinit var popupWindow: PopupWindow
    private var onDelete:()->Unit = {}
    constructor(title:String,context: Context,deleteFunction:()->Unit)
    {
        binding = PopupDeleteConfirmationBinding.inflate(LayoutInflater.from(context))
        onDelete = deleteFunction

        binding.pdcConfirmationText.setText("Sure to delete $title ?")
        popupWindow = PopupWindow(binding.root,WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true)
        popupWindow.elevation = 100f
    }

    // showWindow
    fun showWindow(){
        popupWindow.showAtLocation(binding.root,Gravity.CENTER,0,0)
        binding.pdcDeleteButton.setOnClickListener {
            onDelete()
            // dismiss popup window
            popupWindow.dismiss()
        }
        binding.pdcCancelButton.setOnClickListener {
            popupWindow.dismiss()
        }

    }



}