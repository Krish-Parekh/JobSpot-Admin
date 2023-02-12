package com.example.jobspotadmin.home.fragment.profileFragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.auth.AuthActivity
import com.example.jobspotadmin.databinding.FragmentProfileEditBinding
import com.example.jobspotadmin.home.fragment.profileFragment.viewmodel.ProfileViewModel
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.UiState.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

private const val TAG = "ProfileEditFragmentTAG"

class ProfileEditFragment : Fragment() {
    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ProfileEditFragmentArgs>()
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleCapturedImage(result)
        }
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        with(binding) {
            profileImage.load(args.tpo.imageUri)
            etUsername.setText(args.tpo.username)
            etEmail.setText(args.tpo.email)
            etMobile.setText(args.tpo.mobile)

            if (profileViewModel.getImageUri() != null) {
                profileImage.setImageURI(profileViewModel.getImageUri())
            }

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            profileImage.setOnClickListener {
                startCrop()
            }

            etUsernameContainer.addTextWatcher()
            etEmailContainer.addTextWatcher()
            etMobileContainer.addTextWatcher()

            btnSubmit.setOnClickListener {
                val username = etUsername.getInputValue()
                val email = etEmail.getInputValue()
                val mobile = etMobile.getInputValue()
                val imageUrl = profileViewModel.getImageUri() ?: Uri.parse(args.tpo.imageUri)
                if (detailVerification(imageUrl, username, email, mobile)) {
                    args.tpo.username = username
                    args.tpo.email = email
                    args.tpo.mobile = mobile
                    args.tpo.imageUri = imageUrl.toString()
                    profileViewModel.updateUser(args.tpo)
                    setupObserver()
                }
            }

            ivDeleteAccount.setOnClickListener {
                deleteAccountBottomSheet()
            }
        }
    }

    private fun deleteAccountBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_tpo_account, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnDeleteAccount: MaterialButton = bottomSheet.findViewById(R.id.btnDeleteAccount)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnDeleteAccount.setOnClickListener {
            profileViewModel.deleteAccount(args.tpo)
            profileViewModel.deleteStatus.observe(viewLifecycleOwner) { uiState ->
                when (uiState) {
                    LOADING -> {
                        loadingDialog.show()
                    }
                    SUCCESS -> {
                        requireActivity().finishAffinity()
                        val loginIntent = Intent(requireContext(), AuthActivity::class.java)
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(loginIntent)
                        loadingDialog.dismiss()
                    }
                    FAILURE -> {
                        loadingDialog.dismiss()
                    }
                    else -> Unit
                }
            }
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }

    private fun setupObserver() {
        profileViewModel.updateStatus.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    showToast(requireContext(), "Profile update success")
                    findNavController().popBackStack()
                    loadingDialog.dismiss()
                }
                FAILURE -> {
                    loadingDialog.dismiss()
                }
                else -> Unit
            }
        }
    }

    private fun detailVerification(
        imageUri: Uri?,
        username: String,
        email: String,
        mobile: String
    ): Boolean {
        binding.apply {
            if (imageUri == null) {
                showToast(requireContext(), getString(R.string.field_error_image))
                return false
            }

            val (isUsernameValid, usernameError) = InputValidation.isUsernameValid(username)
            if (isUsernameValid.not()) {
                etUsernameContainer.error = usernameError
                return isUsernameValid
            }

            val (isEmailValid, emailError) = InputValidation.isEmailValid(email)
            if (isEmailValid.not()) {
                etEmailContainer.error = emailError
                return isEmailValid
            }

            val (isMobileNumberValid, mobileNumberError) = InputValidation.isMobileNumberValid(
                mobile
            )
            if (isMobileNumberValid.not()) {
                etMobileContainer.error = mobileNumberError
                return isMobileNumberValid
            }

            return true
        }
    }

    private fun startCrop() {
        ImagePicker.with(this)
            .galleryOnly()
            .crop()
            .compress(1024)
            .maxResultSize(300, 300)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private fun handleCapturedImage(result: ActivityResult) {
        val resultCode = result.resultCode
        val data = result.data

        when (resultCode) {
            Activity.RESULT_OK -> {
                profileViewModel.setImageUri(imageUri = data?.data!!)
                binding.profileImage.setImageURI(profileViewModel.getImageUri())
            }
            ImagePicker.RESULT_ERROR -> {
                showToast(requireContext(), ImagePicker.getError(data))
            }
            else -> {
                showToast(requireContext(), "Task Cancelled")
            }
        }
    }
}