package com.example.jobspotadmin.ui.auth.fragments

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.jobspotadmin.R
import com.example.jobspotadmin.ui.viewmodel.AuthViewModel
import com.example.jobspotadmin.databinding.FragmentUserDetailBinding
import com.example.jobspotadmin.model.TpoDetail
import com.example.jobspotadmin.ui.viewmodel.UserDetailViewModel
import com.example.jobspotadmin.util.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth


private const val TAG = "UserDetailFragment"

class UserDetailFragment : Fragment() {
    private lateinit var binding: FragmentUserDetailBinding
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleCapturedImage(result)
        }
    private var gender: String = ""
    private var qualification: String = ""
    private val authViewModel : AuthViewModel by viewModels()
    private val userDetailViewModel : UserDetailViewModel by viewModels()
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

            authViewModel.getUsername()
            authViewModel.getEmail()

            btnSubmit.setOnClickListener {
                val mobile = etMobile.getInputValue()
                val dob = etDate.getInputValue()
                val stream = etFieldOfStudy.getInputValue()
                val experience = etYearExperience.getInputValue()
                val bio = etBio.getInputValue()
                val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                val imageUri = userDetailViewModel.getImageUri()!!
                if (detailVerification(mobile, dob, gender, imageUri, stream, qualification, experience, bio)) {
                    val tpoDetail = TpoDetail(
                        uid = uid,
                        email = authViewModel.email,
                        username = authViewModel.username,
                        mobile = mobile,
                        dob = dob,
                        gender = gender,
                        stream = stream,
                        qualification = qualification,
                        experience = experience,
                        biography = bio,
                    )
                    val username = tpoDetail.username
                    val userId = tpoDetail.uid
                    val fileName = "$username:$userId"

                    userDetailViewModel.uploadData(
                        imageUri = imageUri,
                        imagePath = "tpo/image/${fileName}",
                        collectionName = "tpo",
                        documentId = userId,
                        tpoDetail = tpoDetail
                    )

                    clearField()
                }
            }
        }

        handleUploadResponse()
    }

    private fun handleUploadResponse() {
        userDetailViewModel.uiState.observe(viewLifecycleOwner, Observer { uploadState ->
            if(uploadState.isUploaded){
                showToast(requireContext(), "Upload Success")
            } else if(uploadState.isLoading){
                showToast(requireContext(), "Loading...")
            } else if(uploadState.error.isNotBlank()){
                showToast(requireContext(), uploadState.error)
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
                userDetailViewModel.setImageUri(imageUri = data?.data!!)
                binding.profileImage.setImageURI(userDetailViewModel.getImageUri())
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
            return when {
                !checkField(
                    mobile,
                    getString(R.string.field_error_mobile),
                    etMobileContainer
                ) -> false
                !checkField(
                    dob,
                    getString(R.string.field_error_dob),
                    etDateContainer
                ) -> {
                    etDateContainer.apply {
                        setErrorIconOnClickListener {
                            error = null
                        }
                    }
                    false
                }
                !checkField(
                    stream,
                    getString(R.string.field_error_stream),
                    etFieldOfStudyContainer
                ) -> false
                !checkField(
                    experience,
                    getString(R.string.field_error_year),
                    etYearExperienceContainer
                ) -> false
                !checkField(
                    bio,
                    getString(R.string.field_error_bio),
                    etBioContainer
                ) -> false
                !InputValidation.checkNullity(gender) -> {
                    genderSpinner.error = ""
                    false
                }
                imageUri == null -> {
                    showToast(requireContext(), getString(R.string.field_error_image))
                    false
                }
                !InputValidation.checkNullity(qualification) -> {
                    qualificationSpinner.error = ""
                    false
                }
                else -> true
            }
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