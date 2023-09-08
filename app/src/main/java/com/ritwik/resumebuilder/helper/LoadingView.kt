package com.ritwik.resumebuilder.helper

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupWindow
import com.ritwik.resumebuilder.databinding.PopupLoadingViewBinding

class LoadingView(context: Context) {
    private val view = PopupLoadingViewBinding.inflate(LayoutInflater.from(context))
    private val window = PopupWindow(
        view.root,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        true
    )

    fun showWindow() {
        window.showAtLocation(view.root, Gravity.CENTER, 0, 0)
    }

    fun hideWindow() {
        if (window.isShowing)
            window.dismiss()
    }

}