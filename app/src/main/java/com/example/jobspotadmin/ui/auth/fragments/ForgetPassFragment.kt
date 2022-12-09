package com.example.jobspotadmin.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.R
import com.example.jobspotadmin.ui.viewmodel.AuthViewModel
import com.example.jobspotadmin.databinding.FragmentForgetPassBinding
import com.example.jobspotadmin.util.*
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "FORGOT_PASSWORD"
@AndroidEntryPoint
class ForgetPassFragment : Fragment() {
    private lateinit var binding : FragmentForgetPassBinding
    private val authViewModel : AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPassBinding.inflate(inflater, container, false)

        binding.btnBackToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etEmailContainer.addTextWatcher()

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.getInputValue()
            if(InputValidation.emailValidation(email)){
                authViewModel.resetPassword(email)
                clearField()
            }else{
                binding.etEmailContainer.error = getString(R.string.field_error_email)
            }
        }

        authViewModel.uiState.observe(viewLifecycleOwner, Observer { uiState ->
            if(uiState.isAuthenticated){
                showToast(requireContext(), "Email sent success")
            }else if(uiState.isLoading){
                showToast(requireContext(), "Loading..")
            }else if(uiState.error.isNotBlank()){
                showToast(requireContext(), uiState.error)
            }
        })
        return binding.root
    }
    private fun clearField() {
        binding.etEmail.clearText()
    }
}