package com.ritwik.resumebuilder

import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ritwik.resumebuilder.databinding.FragmentStartScreenBinding
import com.ritwik.resumebuilder.fragments.HomeScreenFragment


class StartScreen : Fragment() {
    lateinit var binding: FragmentStartScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        activity?.window?.statusBarColor = context?.getColor(R.color.white)!!
        activity?.window?.navigationBarColor = Color.WHITE
        binding = FragmentStartScreenBinding.inflate(layoutInflater)
        binding.fssContinueButton.animation = AnimationUtils.loadAnimation(context,R.anim.create_button_animation)
        binding.fssContinueButton.animation.repeatCount = Animation.INFINITE
        binding.fssContinueButton.animation.start()
        binding.fssContinueButton.setOnClickListener {
            // check login

                openFragment(HomeScreenFragment())

        }

        return binding.root

    }
    private fun openFragment(fragment: Fragment) {
        fragment.setSharedElementEnterTransition(Fade())
        fragment.setEnterTransition(Fade())
        exitTransition = Fade()
        fragment.setSharedElementReturnTransition(Fade())
        val fragmentTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.addSharedElement(binding.fssBuildText,"transition_create_your_resume")
        fragmentTransaction.addSharedElement(binding.fssAnimation,"transition_homeAnimation")
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        const val TAG = "StartScreen"
    }
}