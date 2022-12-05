package com.example.jobspotadmin.auth.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.databinding.FragmentUserDetailBinding
import com.example.jobspotadmin.util.addTextWatcher
import com.example.jobspotadmin.util.getInputValue
import com.example.jobspotadmin.util.showToast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker

private const val TAG = "UserDetailFragment"
class UserDetailFragment : Fragment() {
    private lateinit var binding : FragmentUserDetailBinding
    private val args by navArgs<UserDetailFragmentArgs>()
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleCapturedImage(result)
        }
    private var gender: String = ""
    private var qualification : String = ""
    private var imageUri: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    private fun setupView() {
        binding.profileImage.setOnClickListener {
            startCrop()
        }

        binding.etDate.isCursorVisible = false
        binding.etDate.keyListener = null
        binding.etDateContainer.setEndIconOnClickListener {
            showCalendar()
        }

        binding.genderSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, selectedGender ->
            binding.genderSpinner.error = null
            gender = selectedGender
        }

        binding.qualificationSpinner.setOnSpinnerItemSelectedListener<String>{ _, _, _, selectedQualification ->
            binding.qualificationSpinner.error = null
            qualification = selectedQualification
        }

        binding.etTpoIdContainer.addTextWatcher()
        binding.etMobileContainer.addTextWatcher()
        binding.etFieldOfStudyContainer.addTextWatcher()
        binding.etYearExperienceContainer.addTextWatcher()
        binding.etBioContainer.addTextWatcher()


        binding.btnSubmit.setOnClickListener {
            val tpoId = binding.etTpoId.getInputValue()
            val mobile = binding.etMobile.getInputValue()
            val dob = binding.etDate.getInputValue()
            val stream = binding.etFieldOfStudy.getInputValue()
            val experience = binding.etYearExperience.getInputValue()

            Log.d(TAG, "$tpoId, $mobile, $dob, $stream, $experience, $imageUri, ${args.username}, ${args.email}")
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
                imageUri = data?.data!!.toString()
                binding.profileImage.setImageURI(data.data!!)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
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


}