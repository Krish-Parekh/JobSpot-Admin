package com.example.jobspotadmin.ui.auth.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.ui.viewmodel.AuthViewModel
import com.example.jobspotadmin.databinding.FragmentLoginBinding
import com.example.jobspotadmin.util.*
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LoginFragmentTAG"
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private val authViewModel : AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        val signupText = SpannableString(getString(R.string.sign_up_prompt))
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val signupColor = ForegroundColorSpan(color)
        signupText.setSpan(UnderlineSpan(), 31, signupText.length, 0)
        signupText.setSpan(signupColor, 31, signupText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        binding.tvSignup.text = signupText
        authViewModel.getRoleType()
        authViewModel.getUserDetailStatus()
        authViewModel.getUsername()
        authViewModel.getEmail()
        authViewModel.getUserAuthStatus()

        Log.d(TAG, "Login RoleType : ${authViewModel.userRoleType}")
        Log.d(TAG, "UserDetail Status : ${authViewModel.userDetailStatus}")
        Log.d(TAG, "Username  : ${authViewModel.username}")
        Log.d(TAG, "Email : ${authViewModel.email}")
        Log.d(TAG, "Auth Status : ${authViewModel.userAuthStatus} ")

        binding.tvSignup.setOnClickListener {
            navigateToSignup(roleType = authViewModel.userRoleType)
        }
        binding.tvForgetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgetPassFragment)
        }
        binding.etEmailContainer.addTextWatcher()
        binding.etPasswordContainer.addTextWatcher()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.getInputValue()
            val password = binding.etPassword.getInputValue()
            if (detailVerification(email, password)) {
                authViewModel.signIn(email, password)
                clearField()
            }

            if(detailVerification(email, password) && authViewModel.userDetailStatus){
                // navigateUserToHomeScreen
                Log.d(TAG, "Yes, user has filled all his detail transfer him to HomeScreen")
                clearField()
            }else if(detailVerification(email, password) && !authViewModel.userDetailStatus){
                // navigateUserToUserDetailScreen
                Log.d(TAG, "No, user has not filled all his detail transfer him to UserDetail Screen")
            }
        }

        handleAuthResponse()
    }

    private fun handleAuthResponse() {
        authViewModel.uiState.observe(viewLifecycleOwner, Observer { uiState ->
            if(uiState.isAuthenticated){
                showToast(requireContext(), getString(R.string.auth_pass))
            }else if(uiState.isLoading){
                showToast(requireContext(), "Loading..")
            }else if(uiState.error.isNotBlank()){
                showToast(requireContext(), uiState.error)
            }
        })
    }

    private fun navigateToSignup(roleType: String) {
        val direction = LoginFragmentDirections.actionLoginFragmentToSignupFragment(roleType = roleType)
        findNavController().navigate(direction)
    }

    private fun clearField() {
        binding.etEmail.clearText()
        binding.etPassword.clearText()
    }

    // Verify user details and show message if error
    private fun detailVerification(
        email: String,
        password: String
    ): Boolean {
        binding.apply {
            return if (!InputValidation.emailValidation(email)) {
                etEmailContainer.error = getString(R.string.field_error_email)
                false
            } else if (!InputValidation.passwordValidation(password)) {
                etPasswordContainer.error = getString(R.string.field_error_password)
                false
            } else {
                true
            }
        }
    }
}