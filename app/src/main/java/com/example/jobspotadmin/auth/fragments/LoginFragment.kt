package com.example.jobspotadmin.auth.fragments

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentLoginBinding
import com.example.jobspotadmin.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code

private const val TAG = "LoginFragmentTAG"

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val args by navArgs<LoginFragmentArgs>()
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
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
        binding.tvSignup.setOnClickListener {
            navigateToSignup(args.roleType)
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
                authenticateUser(email, password)
                clearField()
            }
        }
    }

    private fun clearField() {
        binding.etEmail.clearText()
        binding.etPassword.clearText()
    }

    private fun authenticateUser(
        email: String,
        password: String
    ) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = mAuth.currentUser?.uid
                showToast(requireContext(), getString(R.string.auth_pass))
                checkUserDetailFilled(uid!!, email)
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Exception: ${error.message}")
                showToast(requireContext(), getString(R.string.auth_fail))
            }

    }

    private fun checkUserDetailFilled(uid: String, email : String) {
        val documentReference = mFirestore.collection("tpo").document(uid)
        documentReference.get()
            .addOnSuccessListener { document ->
                val username = mAuth.currentUser?.displayName!!
                if(document.exists()){
                    Log.d(TAG, "Go to home activity")
                }else{
                    navigateToUserDetail(username = username, email = email)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, exception.message.toString())
            }
    }

    private fun navigateToUserDetail(username : String, email : String){
        val direction = LoginFragmentDirections.actionLoginFragmentToUserDetailFragment(username, email)
        findNavController().navigate(direction)
    }

    private fun navigateToSignup(roleType: String) {
        val direction = LoginFragmentDirections.actionLoginFragmentToSignupFragment(roleType = roleType)
        findNavController().navigate(direction)
    }

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