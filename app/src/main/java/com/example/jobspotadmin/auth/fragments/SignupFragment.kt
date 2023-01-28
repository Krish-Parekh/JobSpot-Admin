package com.example.jobspotadmin.auth.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.auth.viewmodel.AuthViewModel
import com.example.jobspotadmin.databinding.FragmentSignupBinding
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_TPO
import com.example.jobspotadmin.util.Status.*

private const val TAG = "SignupFragment"

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SignupFragmentArgs>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {

        binding.apply {

            tvLogin.text = createLoginText()
            tvLogin.setOnClickListener {
                findNavController().popBackStack(R.id.loginFragment, false)
            }

            etUsernameContainer.addTextWatcher()
            etEmailContainer.addTextWatcher()
            etPasswordContainer.addTextWatcher()

            btnSignup.setOnClickListener {
                val username = etUsername.getInputValue()
                val email = etEmail.getInputValue()
                val password = etPassword.getInputValue()
                if (detailVerification(username, email, password)) {
                    authViewModel.signup(username, email, password, args.roleType)
                    clearField()
                }
            }
        }
    }

    private fun setupObserver() {
        authViewModel.signupStatus.observe(viewLifecycleOwner){ signupState ->
            when(signupState.status){
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    val (username, email) = signupState.data!!
                    navigateToUserDetail(username, email)
                }
                ERROR -> {
                    showToast(requireContext(), signupState.message.toString())
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun createLoginText(): SpannableString {
        val loginText = SpannableString(getString(R.string.login_prompt))
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val loginColor = ForegroundColorSpan(color)
        loginText.setSpan(UnderlineSpan(), 25, loginText.length, 0)
        loginText.setSpan(loginColor, 25, loginText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return loginText
    }

    private fun navigateToUserDetail(username: String, email: String) {
        if (args.roleType == ROLE_TYPE_TPO) {
            val directions = SignupFragmentDirections.actionSignupFragmentToUserDetailFragment(username, email)
            findNavController().navigate(directions)
        } else {
            showToast(requireContext(), getString(R.string.move_to_login))
        }
    }

    private fun clearField() {
        binding.etUsername.clearText()
        binding.etEmail.clearText()
        binding.etPassword.clearText()
    }

    private fun detailVerification(
        username: String,
        email: String,
        password: String
    ): Boolean {
        binding.apply {
            val (isUsernameValid, usernameError) = InputValidation.isUsernameValid(username)
            if (isUsernameValid.not()){
                etUsernameContainer.error = usernameError
                return isUsernameValid
            }

            val (isEmailValid, emailError) = InputValidation.isEmailValid(email)
            if (isEmailValid.not()){
                etEmailContainer.error = emailError
                return isEmailValid
            }

            val (isPasswordValid, passwordError) = InputValidation.isPasswordValid(password)
            if (isPasswordValid.not()){
                etPasswordContainer.error = passwordError
                return isPasswordValid
            }
            return true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}