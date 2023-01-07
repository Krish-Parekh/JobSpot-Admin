package com.example.jobspotadmin.home.fragment.homeFragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentHomeBinding
import com.example.jobspotadmin.home.fragment.homeFragment.viewmodel.HomeViewModel
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()
    private val currentUser by lazy { FirebaseAuth.getInstance().currentUser }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val roleType: Bundle? = requireActivity().intent.extras
        if (
            roleType != null
            && roleType.containsKey("ROLE_TYPE")
            && roleType.containsKey("USERNAME")
        ){
            val role = requireActivity().intent.extras?.getString("ROLE_TYPE").toString()
            val username = requireActivity().intent.extras?.getString("USERNAME").toString()
            if (role == ROLE_TYPE_ADMIN){
                binding.ivProfileImage.visibility = View.GONE
            } else {
                binding.ivProfileImage.load(currentUser?.photoUrl)
            }
            binding.tvWelcomeHeading.text = getString(R.string.field_welcome_heading, currentUser?.displayName)
        }
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        homeViewModel.metaCounts.observe(viewLifecycleOwner){ count ->
            counterAnimation(0, count.studentCount, binding.tvStudentCount)
            counterAnimation(0, count.jobCount, binding.tvJobCount)
            counterAnimation(0, count.mockCount, binding.tvMockTestCount)
            counterAnimation(0, count.notificationCount, binding.tvNotificationCount)
        }

        binding.ivProfileImage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.cvJob.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_jobsFragment)
        }

        binding.cvMockTest.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_quizFragment)
        }

        binding.cvStudent.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_studentFragment)
        }
    }

    private fun counterAnimation(start: Int, end: Int, textView: TextView) {
        if (start == end) {
            textView.text = start.toString()
        } else {
            val animator = ValueAnimator.ofInt(start, end)
            animator.duration = 500
            animator.interpolator = LinearInterpolator()
            animator.addUpdateListener {
                val counter = it.animatedValue as Int
                textView.text = counter.toString()
            }
            animator.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}