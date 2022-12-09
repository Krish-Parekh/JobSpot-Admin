package com.example.jobspotadmin.ui.auth.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentSignupBinding
import com.example.jobspotadmin.ui.viewmodel.AuthViewModel
import com.example.jobspotadmin.util.*
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SignupFragment"

@AndroidEntryPoint
class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private val args by navArgs<SignupFragmentArgs>()
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView() {
        val loginText = SpannableString(getString(R.string.login_prompt))
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val loginColor = ForegroundColorSpan(color)
        loginText.setSpan(UnderlineSpan(), 25, loginText.length, 0)
        loginText.setSpan(loginColor, 25, loginText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        binding.tvLogin.text = loginText

        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack(R.id.loginFragment, false)
        }
        binding.etUsernameContainer.addTextWatcher()
        binding.etEmailContainer.addTextWatcher()
        binding.etPasswordContainer.addTextWatcher()

        binding.btnSignup.setOnClickListener {
            val username = binding.etUsername.getInputValue()
            val email = binding.etEmail.getInputValue()
            val password = binding.etPassword.getInputValue()
            authViewModel.signUp(username, email, password)
        }
        handleAuthResponse()
    }

    private fun handleAuthResponse() {
        authViewModel.uiState.observe(viewLifecycleOwner, Observer { uiState ->
            if (uiState.isAuthenticated) {
                showToast(requireContext(), getString(R.string.auth_pass))
            } else if (uiState.isLoading) {
                showToast(requireContext(), "Loading..")
            } else if (uiState.error.isNotBlank()) {
                showToast(requireContext(), uiState.error)
            }
        })
    }

    private fun navigateToUserDetail(username: String, email: String) {
        val direction = SignupFragmentDirections.actionSignupFragmentToUserDetailFragment(username, email)
        findNavController().navigate(direction)
    }

    private fun detailVerification(
        username: String,
        email: String,
        password: String
    ): Boolean {
        binding.apply {
            return if (!InputValidation.checkNullity(username)) {
                binding.etUsernameContainer.error = getString(R.string.field_error_username)
                false
            } else if (!InputValidation.emailValidation(email)) {
                binding.etEmailContainer.error = getString(R.string.field_error_email)
                false
            } else if (!InputValidation.passwordValidation(password)) {
                binding.etPasswordContainer.error = getString(R.string.field_error_password)
                false
            } else {
                true
            }
        }
    }
}