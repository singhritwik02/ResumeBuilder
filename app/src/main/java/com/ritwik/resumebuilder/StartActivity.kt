package com.ritwik.resumebuilder

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ritwik.resumebuilder.databinding.ActivityStartBinding


class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.enterTransition = Fade()
        window.exitTransition = Fade()
        window?.statusBarColor = getColor(R.color.white)
        window?.navigationBarColor = Color.WHITE
        binding.fssContinueButton.animation =
            AnimationUtils.loadAnimation(this, R.anim.create_button_animation)
        binding.fssContinueButton.animation.repeatCount = Animation.INFINITE
        binding.fssContinueButton.animation.start()
        binding.fssContinueButton.setOnClickListener {
            // check login
            // create node with logged in


            startActivity(Intent(this, MainActivity::class.java))


        }


    }


}
