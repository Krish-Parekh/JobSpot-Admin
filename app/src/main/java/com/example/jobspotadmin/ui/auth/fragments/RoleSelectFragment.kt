package com.example.jobspotadmin.ui.auth.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentRoleSelectBinding
import com.example.jobspotadmin.ui.viewmodel.AuthViewModel
import com.example.jobspotadmin.util.Constants.ROLE_TYPE_ADMIN
import com.example.jobspotadmin.util.Constants.ROLE_TYPE_TPO
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "RoleSelectFragmentTAG"
@AndroidEntryPoint
class RoleSelectFragment : Fragment() {
    private lateinit var binding: FragmentRoleSelectBinding
    private val authViewModel : AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoleSelectBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView() {
        binding.apply {
            ivRoleAdmin.setOnClickListener {
                authViewModel.setRoleType(roleType = ROLE_TYPE_ADMIN)
                navigateToLogin()
            }
            ivRoleTpo.setOnClickListener {
                authViewModel.setRoleType(roleType = ROLE_TYPE_TPO)
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_roleSelectFragment_to_loginFragment)
    }
}