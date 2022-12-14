package com.example.jobspotadmin.home.fragment.jobsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentJobsBinding


class JobsFragment : Fragment() {
    private lateinit var binding : FragmentJobsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobsBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView() {
        binding.apply {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }
            ivAddJob.setOnClickListener {
                findNavController().navigate(R.id.action_jobsFragment_to_jobDetailFragmentOne)
            }
        }
    }
}