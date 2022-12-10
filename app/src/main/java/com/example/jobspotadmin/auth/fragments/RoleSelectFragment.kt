package com.example.jobspotadmin.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentRoleSelectBinding
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_TPO


class RoleSelectFragment : Fragment() {
    private lateinit var binding: FragmentRoleSelectBinding
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
                navigateToLogin(roleType = ROLE_TYPE_ADMIN)
            }
            ivRoleTpo.setOnClickListener {
                navigateToLogin(roleType = ROLE_TYPE_TPO)
            }
        }
    }

    private fun navigateToLogin(roleType : String) {
        val direction = RoleSelectFragmentDirections.actionRoleSelectFragmentToLoginFragment(roleType = roleType)
        findNavController().navigate(direction)
    }
}