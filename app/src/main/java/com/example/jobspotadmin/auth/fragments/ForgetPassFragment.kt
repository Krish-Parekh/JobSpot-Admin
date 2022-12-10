package com.example.jobspotadmin.auth.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentForgetPassBinding
import com.example.jobspotadmin.util.*
import com.google.firebase.auth.FirebaseAuth


private const val TAG = "FORGOT_PASSWORD"
class ForgetPassFragment : Fragment() {
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var binding : FragmentForgetPassBinding
    private val loadingDialog : LoadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPassBinding.inflate(inflater, container, false)

        binding.btnBackToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etEmailContainer.addTextWatcher()

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.getInputValue()
            if(InputValidation.emailValidation(email)){
                loadingDialog.show()
                mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        loadingDialog.dismiss()
                        showToast(requireContext(), getString(R.string.reset_pass))
                        findNavController().navigate(R.id.action_forgetPassFragment_to_emailFragment)
                    }
                    .addOnFailureListener { error ->
                        loadingDialog.dismiss()
                        Log.d(TAG, "Exception: ${error.message}")
                        showToast(requireContext(), getString(R.string.reset_fail))
                    }
                clearField()
            }else{
                binding.etEmailContainer.error = getString(R.string.field_error_email)
                loadingDialog.dismiss()
            }
        }

        return binding.root
    }
    private fun clearField() {
        binding.etEmail.clearText()
    }
}