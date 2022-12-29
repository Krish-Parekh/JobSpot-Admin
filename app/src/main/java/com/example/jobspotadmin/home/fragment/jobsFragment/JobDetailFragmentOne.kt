package com.example.jobspotadmin.home.fragment.jobsFragment

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
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentJobDetailOneBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.InputValidation
import com.example.jobspotadmin.util.addTextWatcher
import com.example.jobspotadmin.util.getInputValue
import com.example.jobspotadmin.util.showToast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth


class JobDetailFragmentOne : Fragment() {
    private var _binding: FragmentJobDetailOneBinding? = null
    private val binding get() = _binding!!
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleCapturedImage(result)
        }
    private val jobsViewModel: JobsViewModel by viewModels()
    private var workType: String = ""
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobDetailOneBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView() {
        binding.apply {

            if (jobsViewModel.getImageUri() != null) {
                ivCompany.setImageURI(jobsViewModel.getImageUri())
            }

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            ivCompany.setOnClickListener {
                startCrop()
            }

            etJobTitleContainer.addTextWatcher()
            etCompanyNameContainer.addTextWatcher()
            etCityNameContainer.addTextWatcher()
            etSalaryContainer.addTextWatcher()
            etJobDescContainer.addTextWatcher()

            workTypeSpinner.dismissWhenNotifiedItemSelected = true
            workTypeSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, selectedWorkType ->
                workTypeSpinner.error = null
                workType = selectedWorkType
            }

            btnNext.setOnClickListener {
                val jobRole = etJobTitle.getInputValue()
                val companyName = etCompanyName.getInputValue()
                val city = etCityName.getInputValue()
                val salary = etSalary.getInputValue()
                val jobDescription = etJobDesc.getInputValue()
                val imageUrl = jobsViewModel.getImageUri()
                val currentUid = mAuth.currentUser?.uid.toString()

                if (detailVerification(
                        imageUrl,
                        jobRole,
                        companyName,
                        city,
                        salary,
                        workType,
                        jobDescription
                    )
                ) {
                    val job = Job(
                        authorUid = currentUid,
                        imageUrl = imageUrl.toString(),
                        role = jobRole,
                        name = companyName,
                        city = city,
                        salary = salary,
                        workType = workType,
                        description = jobDescription
                    )
                    navigateToDetailFragmentTwo(job = job)
                }
            }
        }
    }

    private fun navigateToDetailFragmentTwo(job: Job) {
        val direction =
            JobDetailFragmentOneDirections.actionJobDetailFragmentOneToJobDetailFragmentTwo(job = job)
        findNavController().navigate(direction)
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
                jobsViewModel.setImageUri(imageUri = data?.data!!)
                binding.ivCompany.setImageURI(jobsViewModel.getImageUri())
            }
            ImagePicker.RESULT_ERROR -> {
                showToast(requireContext(), ImagePicker.getError(data))
            }
            else -> {
                showToast(requireContext(), "Task Cancelled")
            }
        }
    }

    private fun detailVerification(
        imageUrl: Uri?,
        title: String,
        company: String,
        city: String,
        salary: String,
        workType: String,
        description: String
    ): Boolean {
        binding.apply {
            if (imageUrl == null) {
                showToast(requireContext(), getString(R.string.field_error_image))
                return false
            } else if (!InputValidation.checkNullity(title)) {
                etJobTitleContainer.error = getString(R.string.field_error_job_title)
                return false
            } else if (!InputValidation.checkNullity(company)) {
                etCompanyNameContainer.error = getString(R.string.field_error_company_name)
                return false
            } else if (!InputValidation.checkNullity(city)) {
                etCityNameContainer.error = getString(R.string.field_error_city)
                return false
            } else if (!InputValidation.salaryValidation(salary)) {
                etSalaryContainer.error = getString(R.string.field_error_salary)
                return false
            } else if (!InputValidation.checkNullity(workType)) {
                workTypeSpinner.error = ""
                return false
            } else if (!InputValidation.checkNullity(description)) {
                etJobDescContainer.error = getString(R.string.field_error_description)
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