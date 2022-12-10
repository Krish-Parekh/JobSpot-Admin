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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentLoginBinding
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_ROLE
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_TPO
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_TPO
import com.example.jobspotadmin.util.InputValidation
import com.example.jobspotadmin.util.addTextWatcher
import com.example.jobspotadmin.util.clearText
import com.example.jobspotadmin.util.getInputValue
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
                val email = binding.etEmail.getInputValue()
                val password = binding.etPassword.getInputValue()
                if (detailVerification(email, password)) {
                    authenticateUser(email, password)
                    clearField()
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

    private fun authenticateUser(
        email: String,
        password: String
    ) {
        lifecycleScope.launch {
            try {
                mAuth.signInWithEmailAndPassword(email, password).await()
                val currentUserUid = mAuth.currentUser?.uid!!

                val currentUserDoc =
                    mFirestore.collection(COLLECTION_PATH_TPO).document(currentUserUid)
                val userDocument: DocumentSnapshot = currentUserDoc.get().await()

                val currentUserRole =
                    mFirestore.collection(COLLECTION_PATH_ROLE).document(currentUserUid)
                val roleDocument: DocumentSnapshot = currentUserRole.get().await()
                val roleType: String = roleDocument.get("role") as String

                if (!userDocument.exists() && roleType == ROLE_TYPE_TPO) {
                    Log.d(TAG, "Transfer user to UserDetail Fragment")
                } else if (!userDocument.exists() && roleType == ROLE_TYPE_ADMIN) {
                    Log.d(TAG, "Transfer user to Home Activity")
                } else {
                    Log.d(TAG, "Transfer user to Home Activity")
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.d(TAG, "Error : Invalid email or password")
            } catch (e: FirebaseAuthInvalidUserException) {
                Log.d(TAG, "Error : The specified user does not exist")
            } catch (e: FirebaseNetworkException) {
                Log.d(TAG, "Error : A network error occurred")
            } catch (e: FirebaseTooManyRequestsException) {
                Log.d(TAG, "Error : Too many requests were made to the authentication server")
            } catch (e: Exception) {
                Log.d(TAG, "Error : ${e.message}")
            }
        }
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
}