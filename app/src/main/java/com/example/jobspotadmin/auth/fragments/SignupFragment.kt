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
import com.example.jobspotadmin.databinding.FragmentSignupBinding
import com.example.jobspotadmin.model.Admin
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_ADMIN
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_ROLE
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_TPO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "SignupFragment"

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SignupFragmentArgs>()
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView() {

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
                    authenticateUser(username, email, password)
                    clearField()
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

    private fun authenticateUser(
        username: String,
        email: String,
        password: String
    ) {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                mAuth.createUserWithEmailAndPassword(email, password).await()
                val currentUser = mAuth.currentUser!!
                val profileUpdates =
                    UserProfileChangeRequest.Builder().setDisplayName(username).build()
                val currentUserRole = hashMapOf("role" to args.roleType)
                mFirestore.collection(COLLECTION_PATH_ROLE).document(currentUser.uid).set(currentUserRole).await()
                if(args.roleType == ROLE_TYPE_ADMIN) {
                    val admin = Admin(uid = currentUser.uid, username = username, email = email)
                    mFirestore.collection(COLLECTION_PATH_ADMIN).document(currentUser.uid).set(admin).await()
                }
                currentUser.updateProfile(profileUpdates).await()
                showToast(requireContext(), getString(R.string.auth_pass))
                navigateToUserDetail(username, email)
            } catch (error: FirebaseAuthUserCollisionException) {
                showToast(requireContext(), getString(R.string.email_exists))
            } catch (error: Exception) {
                showToast(requireContext(), getString(R.string.auth_fail))
                Log.d(TAG, "Exception : ${error.message}")
            } finally {
                loadingDialog.dismiss()
            }
        }
    }

    private fun navigateToUserDetail(username: String, email: String) {
        if (args.roleType == ROLE_TYPE_TPO) {
            val directions = SignupFragmentDirections.actionSignupFragmentToUserDetailFragment(
                username = username,
                email = email
            )
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}