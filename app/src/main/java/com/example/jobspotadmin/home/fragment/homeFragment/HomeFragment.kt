package com.example.jobspotadmin.home.fragment.homeFragment

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.auth.AuthActivity
import com.example.jobspotadmin.databinding.BottomSheetLogoutBinding
import com.example.jobspotadmin.databinding.FragmentHomeBinding
import com.example.jobspotadmin.home.fragment.homeFragment.viewmodel.HomeViewModel
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.example.jobspotadmin.util.LoadingDialog
import com.example.jobspotadmin.util.Status.*
import com.example.jobspotadmin.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()
    private val user: FirebaseUser? by lazy { Firebase.auth.currentUser }
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            homeViewModel.fetchCounts()
            val bundle: Bundle? = requireActivity().intent.extras
            if (
                bundle != null
                && user != null
                && bundle.containsKey("ROLE_TYPE")
                && bundle.containsKey("USERNAME")
            ) {
                val roleType = bundle.getString("ROLE_TYPE").toString()
                val username = bundle.getString("USERNAME").toString()
                if (roleType == ROLE_TYPE_ADMIN) {
                    ivProfileImage.visibility = View.GONE
                    ivLogout.visibility = View.VISIBLE
                } else {
                    ivProfileImage.load(user?.photoUrl)
                    cvPlacementOfficer.visibility = View.GONE
                }
                binding.tvWelcomeHeading.text =
                    getString(R.string.field_welcome_heading, user?.displayName)
            }

            if (ivLogout.visibility == View.VISIBLE) {
                ivLogout.setOnClickListener {
                    showLogoutBottomSheet()
                }
            }

            ivProfileImage.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            }

            cvJob.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_jobsFragment)
            }

            cvMockTest.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_quizFragment)
            }

            cvStudent.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_studentFragment)
            }

            cvNotification.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            }

            cvPlacementOfficer.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_tpoFragment)
            }
        }
    }

    private fun setupObserver() {
        homeViewModel.metaCounts.observe(viewLifecycleOwner) { countState ->
            when (countState.status) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    val counts = countState.data!!
                    counterAnimation(counts.studentCount, binding.tvStudentCount)
                    counterAnimation(counts.jobCount, binding.tvJobCount)
                    counterAnimation(counts.mockCount, binding.tvMockTestCount)
                    counterAnimation(counts.notificationCount, binding.tvNotificationCount)
                    if (binding.cvPlacementOfficer.visibility == View.VISIBLE){
                        counterAnimation(counts.tpoCount, binding.tvPlacementOfficerCount)
                    }
                    loadingDialog.dismiss()
                }
                ERROR -> {
                    val errorMessage = countState.message!!
                    showToast(requireContext(), errorMessage)
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun counterAnimation(count: Int, textView: TextView) {
        if (count == 0) {
            textView.text = "0"
        } else {
            val animator = ValueAnimator.ofInt(0, count)
            animator.duration = 500
            animator.interpolator = LinearInterpolator()
            animator.addUpdateListener {
                val counter = it.animatedValue as Int
                textView.text = counter.toString()
            }
            animator.start()
        }
    }

    private fun showLogoutBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val logoutSheetBinding = BottomSheetLogoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(logoutSheetBinding.root)
        logoutSheetBinding.apply {
            btnNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            btnLogout.setOnClickListener {
                bottomSheetDialog.dismiss()
                logout()
            }
        }
        bottomSheetDialog.show()
    }

    private fun logout() {
        Firebase.auth.signOut()
        requireActivity().finishAffinity()
        val loginIntent = Intent(requireContext(), AuthActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(loginIntent)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}