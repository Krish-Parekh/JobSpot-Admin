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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentSignupBinding
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_TPO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "SignupFragment"
class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private val args by navArgs<SignupFragmentArgs>()
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mFirestore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView(){

        binding.tvLogin.text = createLoginText()

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
    private fun createLoginText() : SpannableString {
        val loginText = SpannableString(getString(R.string.login_prompt))
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val loginColor = ForegroundColorSpan(color)
        loginText.setSpan(UnderlineSpan(), 25, loginText.length, 0)
        loginText.setSpan(loginColor, 25, loginText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return loginText
    }
    private fun authenticateUser(
        username: String,
        email: String,
        password: String
    ) {
        lifecycleScope.launch {
            try{
                mAuth.createUserWithEmailAndPassword(email, password).await()
                val currentUser = mAuth.currentUser!!
                val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(username).build()
                val currentUserRole = hashMapOf("role" to args.roleType)
                mFirestore.collection("role").document(currentUser.uid).set(currentUserRole).await()
                currentUser.updateProfile(profileUpdates).await()
                showToast(requireContext(), getString(R.string.auth_pass))
                navigateToUserDetail(username, email)
            }catch (error : FirebaseAuthUserCollisionException){
                showToast(requireContext(), "Email already exists")
            }catch (error : Exception) {
                showToast(requireContext(), getString(R.string.auth_fail))
                Log.d(TAG, "Exception : ${error.message}")
            }
        }
    }

    private fun navigateToUserDetail(username: String, email: String) {
        if(args.roleType === ROLE_TYPE_TPO){
            val directions = SignupFragmentDirections.actionSignupFragmentToUserDetailFragment(username = username, email =  email)
            findNavController().navigate(directions)
        }else {
            showToast(requireContext(), args.roleType)
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
            if (!InputValidation.checkNullity(username)) {
                binding.etUsernameContainer.error = getString(R.string.field_error_username)
                return false
            } else if (!InputValidation.emailValidation(email)) {
                binding.etEmailContainer.error = getString(R.string.field_error_email)
                return false
            } else if (!InputValidation.passwordValidation(password)) {
                binding.etPasswordContainer.error = getString(R.string.field_error_password)
                return false
            } else {
                return true
            }
        }
    }

}