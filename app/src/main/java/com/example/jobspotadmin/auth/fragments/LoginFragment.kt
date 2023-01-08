package com.example.jobspotadmin.auth.fragments

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.auth.viewmodel.LoginViewModel
import com.example.jobspotadmin.databinding.FragmentLoginBinding
import com.example.jobspotadmin.home.HomeActivity
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_TPO
import com.example.jobspotadmin.util.Status.*

private const val TAG = "LoginFragmentTAG"

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<LoginFragmentArgs>()
    private val loginViewModel by viewModels<LoginViewModel>()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            tvSignup.text = createSignupText()
            tvSignup.setOnClickListener {
                navigateToSignup(roleType = args.roleType)
            }
            tvForgetPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgetPassFragment)
            }
            etEmailContainer.addTextWatcher()
            etPasswordContainer.addTextWatcher()
            btnLogin.setOnClickListener {
                val email = etEmail.getInputValue()
                val password = etPassword.getInputValue()
                if (detailVerification(email, password)) {
                    loginViewModel.authenticateUser(email, password)
                    clearField()
                }
            }
        }
    }
    /*
    * if (it.data == null || currentUserState.roleType != args.roleType) {
        showToast(requireContext(), "Account doesn't exist")
    } else if (!currentUserState.userInfoExist && currentUserState.roleType == ROLE_TYPE_TPO) {
        navigateToUserDetail(username = currentUserState.username, email = currentUserState.email)
    } else {
        navigateToHomeActivity(roleType = currentUserState.roleType)
    }
    * */
    private fun setupObserver() {
        loginViewModel.loginStatus.observe(viewLifecycleOwner){
            when(it.status){
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    if (it.data != null) {
                        val currentUserState = it.data
                        if (currentUserState.roleType == args.roleType){
                            if (!currentUserState.userInfoExist && currentUserState.roleType == ROLE_TYPE_TPO){
                                navigateToUserDetail(username = currentUserState.username, email = currentUserState.email)
                            } else if(currentUserState.userInfoExist && currentUserState.roleType == ROLE_TYPE_TPO) {
                                navigateToHomeActivity(roleType = currentUserState.roleType)
                            } else if (currentUserState.roleType == ROLE_TYPE_ADMIN){
                                navigateToHomeActivity(roleType = currentUserState.roleType)
                            }
                        } else {
                            showToast(requireContext(), "Account doesn't exist")
                        }
                    }
                }
                ERROR -> {
                    loadingDialog.dismiss()
                    showToast(requireContext(), it.message.toString())
                }
            }
        }
    }

    private fun createSignupText(): SpannableString {
        val signupText = SpannableString(getString(R.string.sign_up_prompt))
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val signupColor = ForegroundColorSpan(color)
        signupText.setSpan(UnderlineSpan(), 31, signupText.length, 0)
        signupText.setSpan(signupColor, 31, signupText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return signupText
    }

    private fun clearField() {
        binding.etEmail.clearText()
        binding.etPassword.clearText()
    }

    private fun navigateToHomeActivity(roleType: String) {
        Log.d(TAG, "RoleType = $roleType")
        val homeActivity = Intent(requireContext(), HomeActivity::class.java )
        homeActivity.putExtra("ROLE_TYPE", roleType)
        startActivity(homeActivity)
        requireActivity().finish()
    }

    private fun navigateToUserDetail(username: String, email: String) {
        val direction =
            LoginFragmentDirections.actionLoginFragmentToUserDetailFragment(username, email)
        findNavController().navigate(direction)
    }

    private fun navigateToSignup(roleType: String) {
        val direction =
            LoginFragmentDirections.actionLoginFragmentToSignupFragment(roleType = roleType)
        findNavController().navigate(direction)
    }

    private fun detailVerification(
        email: String,
        password: String
    ): Boolean {
        binding.apply {
            if (!InputValidation.emailValidation(email)) {
                etEmailContainer.error = getString(R.string.field_error_email)
                return false
            } else if (!InputValidation.passwordValidation(password)) {
                etPasswordContainer.error = getString(R.string.field_error_password)
                return false
            } else {
                return true
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}