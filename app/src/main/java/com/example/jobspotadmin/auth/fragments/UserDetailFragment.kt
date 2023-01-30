package com.example.jobspotadmin.auth.fragments


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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.auth.viewmodel.AuthViewModel
import com.example.jobspotadmin.auth.viewmodel.UserDetailViewModel
import com.example.jobspotadmin.databinding.FragmentUserDetailBinding
import com.example.jobspotadmin.model.Tpo
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Status.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class UserDetailFragment : Fragment() {
    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UserDetailFragmentArgs>()
    private val userDetailViewModel by viewModels<UserDetailViewModel>()
    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        handleCapturedImage(result)
    }
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private var gender: String = ""
    private var qualification: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {

            if(userDetailViewModel.getImageUri() != null){
                val profileUri = userDetailViewModel.getImageUri()
                profileImage.setImageURI(profileUri)
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
            etStreamContainer.addTextWatcher()
            etYearExperienceContainer.addTextWatcher()
            etBioContainer.addTextWatcher()

            btnSubmit.setOnClickListener {
                val mobile = etMobile.getInputValue()
                val dob = etDate.getInputValue()
                val stream = etStream.getInputValue()
                var experience = etYearExperience.getInputValue()
                val bio = etBio.getInputValue()
                val imageUri = userDetailViewModel.getImageUri()

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
                    val df = DecimalFormat("#.##")
                    experience = df.format(experience.toDouble())
                    val tpo = Tpo(
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
                    userDetailViewModel.uploadUserDetail(imageUri!!, tpo)
                }

            }

        }
    }

    private fun setupObserver() {
        userDetailViewModel.userUploadStatus.observe(viewLifecycleOwner) { uploadState ->
            when(uploadState.status){
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    findNavController().popBackStack(R.id.signupFragment, true)
                    userDetailViewModel.setImageUri(null)
                }
                ERROR -> {
                    loadingDialog.dismiss()
                }
            }

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

    private fun startCrop() {
        ImagePicker.with(this)
            .galleryOnly()
            .crop()
            .compress(1024)
            .maxResultSize(300, 300)
            .createIntent { intent ->
                imagePicker.launch(intent)
            }
    }

    private fun showCalendar() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.addOnPositiveButtonClickListener {
            val date = Date(it)
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val dateString = formatter.format(date)
            binding.etDate.setText(dateString)
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
            if (imageUri == null) {
                showToast(requireContext(), getString(R.string.field_error_image))
                return false
            }

            val (isMobileNumberValid, mobileNumberError) = InputValidation.isMobileNumberValid(mobile)
            if (isMobileNumberValid.not()){
                etMobileContainer.error = mobileNumberError
                return isMobileNumberValid
            }

            val (isDOBValid, dobError) = InputValidation.isDOBValid(dob)
            if (isDOBValid.not()){
                etDateContainer.apply {
                    error = dobError
                    setErrorIconOnClickListener {
                        error = null
                    }
                }
                return isDOBValid
            }

            if (InputValidation.checkNullity(gender)){
                genderSpinner.error = ""
                return false
            }

            if (InputValidation.checkNullity(qualification)){
                qualificationSpinner.error = ""
                return false
            }

            val (isStreamValid, streamError) = InputValidation.isStreamValid(stream)
            if (isStreamValid.not()){
                etStreamContainer.error = streamError
                return isStreamValid
            }

            val (isExperienceValid, experienceError) = InputValidation.isExperienceValid(experience)
            if (isExperienceValid.not()){
                etYearExperienceContainer.error = experienceError
                return isExperienceValid
            }

            val (isBiographyValid, biographyError) = InputValidation.isBiographyValid(bio)
            if (isBiographyValid.not()){
                etBioContainer.error = biographyError
                return isBiographyValid
            }

            return true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}