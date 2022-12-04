package com.example.jobspotadmin.auth.fragments

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentSignupBinding
import com.example.jobspotadmin.util.*
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "SignupFragment"
class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private val args by navArgs<SignupFragmentArgs>()
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView(){
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
            if (detailVerification(username, email, password)) {
                authenticateUser(username, email, password)
                clearField()
            }
        }
    }

    private fun authenticateUser(
        username: String,
        email: String,
        password: String
    ) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = mAuth.currentUser?.uid
                Log.d(TAG, "UID : $uid")
                showToast(requireContext(), getString(R.string.auth_pass))
//                navigateToUserDetail(username, email)
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Exception: ${error.message}")
                showToast(requireContext(), getString(R.string.auth_fail))
            }
    }

    private fun clearField() {
        binding.etUsername.clearText()
        binding.etEmail.clearText()
        binding.etPassword.clearText()
    }

    // Verify user details and show message if error
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