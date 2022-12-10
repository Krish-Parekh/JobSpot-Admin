package com.example.jobspotadmin.auth.fragments

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.auth.viewmodel.AuthViewModel
import com.example.jobspotadmin.databinding.FragmentUserDetailBinding
import com.example.jobspotadmin.model.User
import com.example.jobspotadmin.util.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth


/*
* Note:
* 4. Implement Test for the validation function
* 6. Implement SingleTop for fragment
* 7. Disable the button once clicked
* 8. Send Data to firebase fire-store
* */
private const val TAG = "UserDetailFragment"

class UserDetailFragment : Fragment() {
    private lateinit var binding: FragmentUserDetailBinding
    private val args by navArgs<UserDetailFragmentArgs>()
    private val authViewModel: AuthViewModel by viewModels()
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleCapturedImage(result)
        }
    private var gender: String = ""
    private var qualification: String = ""
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    private fun setupView() {
        binding.apply {

            if (authViewModel.getImageUri() != null) {
                profileImage.setImageURI(authViewModel.getImageUri())
            }

            profileImage.setOnClickListener {
                startCrop()
            }

            etDate.isCursorVisible = false
            etDate.keyListener = null
            etDateContainer.setEndIconOnClickListener {
                showCalendar()
            }

            genderSpinner.dismissWhenNotifiedItemSelected = true
            genderSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, selectedGender ->
                genderSpinner.error = null
                gender = selectedGender
            }

            qualificationSpinner.dismissWhenNotifiedItemSelected = true
            qualificationSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, selectedQualification ->
                binding.qualificationSpinner.error = null
                qualification = selectedQualification
            }

            etMobileContainer.addTextWatcher()
            etFieldOfStudyContainer.addTextWatcher()
            etYearExperienceContainer.addTextWatcher()
            etBioContainer.addTextWatcher()

            btnSubmit.setOnClickListener {
                val mobile = etMobile.getInputValue()
                val dob = etDate.getInputValue()
                val stream = etFieldOfStudy.getInputValue()
                val experience = etYearExperience.getInputValue()
                val bio = etBio.getInputValue()
                val imageUri = authViewModel.getImageUri()
                if (detailVerification(
                        mobile,
                        dob,
                        gender,
                        imageUri,
                        stream,
                        qualification,
                        experience,
                        bio
                    )
                ) {
                    val user = User(
                        uid = mAuth.currentUser?.uid.toString(),
                        email = args.email,
                        username = args.username,
                        mobile = mobile,
                        dob = dob,
                        gender = gender,
                        stream = stream,
                        qualification = qualification,
                        experience = experience,
                        biography = bio,
                    )
                    Log.d(TAG, "User : $user")
                    authViewModel.uploadData(imageUri = imageUri!!, user = user)
                    handleUploadResponse()
                    clearField()
                }
            }
        }
    }

    private fun handleUploadResponse() {
        authViewModel.uploadDataStatus.observe(viewLifecycleOwner, Observer { uiState ->
            if (uiState.loading) {
                loadingDialog.show()
            } else if (uiState.success) {
                loadingDialog.dismiss()
                authViewModel.setImageUri(null)
            } else if (uiState.failed) {
                loadingDialog.dismiss()
            }
        })
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
                authViewModel.setImageUri(imageUri = data?.data!!)
                binding.profileImage.setImageURI(authViewModel.getImageUri())
            }
            ImagePicker.RESULT_ERROR -> {
                showToast(requireContext(), ImagePicker.getError(data))
            }
            else -> {
                showToast(requireContext(), "Task Cancelled")
            }
        }
    }

    private fun showCalendar() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.addOnPositiveButtonClickListener {
            binding.etDate.setText(datePicker.headerText)
        }
        datePicker.show(childFragmentManager, "Material DatePicker")
    }

    private fun detailVerification(
        mobile: String,
        dob: String,
        gender: String,
        imageUri: Uri?,
        stream: String,
        qualification: String,
        experience: String,
        bio: String,
    ): Boolean {
        binding.apply {
            if(imageUri == null){
                showToast(requireContext(), getString(R.string.field_error_image))
                return false
            }
            else if (!InputValidation.mobileValidation(mobile)){
                etMobileContainer.error = getString(R.string.field_error_mobile)
                return false
            }
            else if(!checkField(dob, getString(R.string.field_error_dob), etDateContainer)){
                etDateContainer.apply {
                    setErrorIconOnClickListener {
                        error = null
                    }
                }
                return false
            }
            else if(!InputValidation.checkNullity(gender)) {
                genderSpinner.error = ""
                return false
            }
            else if(!InputValidation.checkNullity(qualification)) {
                qualificationSpinner.error = ""
                return false
            }
            else if(!checkField(stream, getString(R.string.field_error_stream), etFieldOfStudyContainer)){
                return false
            }
            else if(!checkField(experience, getString(R.string.field_error_stream), etYearExperienceContainer)){
                return false
            }
            else return checkField(bio, getString(R.string.field_error_stream), etBioContainer)
        }
    }

    private fun clearField() {
        binding.apply {
            etMobile.clearText()
            etDate.clearText()
            genderSpinner.clearSelectedItem()
            qualificationSpinner.clearSelectedItem()
            etFieldOfStudy.clearText()
            etYearExperience.clearText()
            etBio.clearText()
            profileImage.setImageResource(R.drawable.ic_image_picker)
        }
    }


}