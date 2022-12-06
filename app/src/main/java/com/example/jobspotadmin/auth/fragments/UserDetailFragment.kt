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
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.auth.viewmodel.AuthViewModel
import com.example.jobspotadmin.databinding.FragmentUserDetailBinding
import com.example.jobspotadmin.util.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout


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
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleCapturedImage(result)
        }
    private var gender: String = ""
    private var qualification: String = ""
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
                if(detailVerification(mobile, dob, gender, imageUri, stream ,qualification, experience, bio)) {
                    showToast(requireContext(), "Validation Success")
                    Log.d(TAG, "$mobile, $dob, $gender, ${imageUri}, $stream, $qualification, $experience, $bio")
                }
            }
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
        stream : String,
        qualification: String,
        experience: String,
        bio: String,
    ): Boolean {
        binding.apply {
            return when {
                !checkField(mobile, getString(R.string.field_error_mobile), etMobileContainer) -> false
                !checkField(dob, getString(R.string.field_error_dob), etDateContainer) -> {
                    etDateContainer.apply {
                        setErrorIconOnClickListener{
                            error = null
                        }
                    }
                    false
                }
                !checkField(stream, getString(R.string.field_error_stream), etFieldOfStudyContainer) -> false
                !checkField(experience, getString(R.string.field_error_year), etYearExperienceContainer) -> false
                !checkField(bio, getString(R.string.field_error_bio), etBioContainer) -> false
                !InputValidation.checkNullity(gender) -> {
                    genderSpinner.error = ""
                    false
                }
                imageUri != null -> {
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


}