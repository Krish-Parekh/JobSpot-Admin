package com.example.jobspotadmin.home.fragment.studentFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentStudentViewBinding


class StudentViewFragment : Fragment() {
    private var _binding : FragmentStudentViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentViewBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    private fun setupUI() {

    }
}