package com.example.jobspotadmin.home.fragment.homeFragment

import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        counterAnimation(0, 50, binding.tvStudentCount)
        counterAnimation(0, 50, binding.tvJobCount)
        counterAnimation(0, 50, binding.tvMockTestCount)
        counterAnimation(0, 50, binding.tvNotificationCount)

        binding.cvJob.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_jobsFragment)
        }

        return binding.root
    }

    private fun counterAnimation(start : Int, end : Int, textView : TextView){
        val animator = ValueAnimator.ofInt(start, end)
        animator.duration = 5000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener {
            val counter = it.animatedValue as Int
            textView.text = counter.toString()
        }
        animator.start()
    }

}