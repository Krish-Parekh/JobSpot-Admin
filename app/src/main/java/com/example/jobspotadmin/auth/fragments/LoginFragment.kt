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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentLoginBinding
import com.example.jobspotadmin.home.HomeActivity
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_ROLE
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_TPO
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.example.jobspotadmin.util.Constants.Companion.ROLE_TYPE_TPO
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "LoginFragmentTAG"

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<LoginFragmentArgs>()
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

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
                val email = etEmail.getInputValue()
                val password = etPassword.getInputValue()
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
                loadingDialog.show()
                mAuth.signInWithEmailAndPassword(email, password).await()
                val currentUserUid = mAuth.currentUser?.uid!!
                val currentUsername = mAuth.currentUser?.displayName!!

                val currentUserRole = mFirestore.collection(COLLECTION_PATH_ROLE).document(currentUserUid)
                val roleDocument: DocumentSnapshot = currentUserRole.get().await()
                if (!roleDocument.exists()){
                    showToast(requireContext(), getString(R.string.invalid_credentials))
                    return@launch
                }
                val roleType: String = roleDocument.get("role") as String

                /*
                * We are fetching TPO documents because we want to check
                * if TPO has filled all the details.
                * */
                val currentUserDoc = mFirestore.collection(COLLECTION_PATH_TPO).document(currentUserUid)
                val userDocument: DocumentSnapshot = currentUserDoc.get().await()

                if (roleType == args.roleType) {
                    if (!userDocument.exists() && roleType == ROLE_TYPE_TPO) {
                        navigateToUserDetail(username = currentUsername, email = email)
                    } else if (userDocument.exists() && roleType == ROLE_TYPE_TPO) {
                        navigateToHomeActivity(roleType, currentUsername)
                    } else if (roleType == ROLE_TYPE_ADMIN) {
                        navigateToHomeActivity(roleType, currentUsername)
                    }
                } else {
                    showToast(requireContext(), "Account doesn't exist")
                }

            } catch (e: FirebaseAuthInvalidCredentialsException) {
                showToast(requireContext(), getString(R.string.invalid_credentials))
            } catch (e: FirebaseAuthInvalidUserException) {
                showToast(requireContext(), getString(R.string.invalid_user))
            } catch (e: FirebaseNetworkException) {
                showToast(requireContext(), getString(R.string.network_error))
            } catch (e: Exception) {
                Log.d(TAG, "Error : ${e.message}")
                showToast(requireContext(), e.message.toString())
            } finally {
                loadingDialog.dismiss()
            }
        }
    }

    private fun navigateToHomeActivity(roleType: String, username: String) {
        Log.d(TAG, "RoleType = $roleType")
        val homeActivity = Intent(requireContext(), HomeActivity::class.java )
        homeActivity.putExtra("ROLE_TYPE", roleType)
        homeActivity.putExtra("USERNAME", username)
        requireActivity().startActivity(homeActivity)
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