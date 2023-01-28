package com.example.jobspotadmin.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.auth.viewmodel.AuthViewModel
import com.example.jobspotadmin.databinding.FragmentForgetPassBinding
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Status.SUCCESS
import com.example.jobspotadmin.util.Status.LOADING
import com.example.jobspotadmin.util.Status.ERROR


private const val TAG = "FORGOT_PASSWORD"

class ForgetPassFragment : Fragment() {
    private var _binding: FragmentForgetPassBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetPassBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.btnBackToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etEmailContainer.addTextWatcher()

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.getInputValue()
            val (isEmailValid, emailError) = InputValidation.isEmailValid(email)
            if (isEmailValid.not()) {
                authViewModel.resendPassword(email)
                clearField()
            } else {
                binding.etEmailContainer.error = emailError
            }
        }
    }

    private fun setupObserver() {
        authViewModel.resendPasswordStatus.observe(viewLifecycleOwner) { resendPasswordState ->
            when (resendPasswordState.status) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    val successMessage = resendPasswordState.data!!
                    showToast(requireContext(), successMessage)
                }
                ERROR -> {
                    val errorMessage = resendPasswordState.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }
    }


    private fun clearField() {
        binding.etEmail.clearText()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}