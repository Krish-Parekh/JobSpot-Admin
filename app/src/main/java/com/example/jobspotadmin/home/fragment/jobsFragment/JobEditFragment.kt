package com.example.jobspotadmin.home.fragment.jobsFragment

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentJobEditBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.ChipsViewModel
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobEditViewModel
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.UiState.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout

private const val TAG = "JobEditFragmentTAG"

class JobEditFragment : Fragment() {
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleCapturedImage(result)
        }
    private val jobsViewModel: JobsViewModel by viewModels()
    private val chipsViewModel: ChipsViewModel by viewModels()
    private val jobEditViewModel: JobEditViewModel by viewModels()
    private lateinit var binding: FragmentJobEditBinding
    private val args by navArgs<JobViewFragmentArgs>()
    private val job by lazy { args.job }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJobEditBinding.inflate(inflater, container, false)

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        binding.apply {

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            companyImage.load(job.imageUrl)
            if (jobsViewModel.getImageUri() != null) {
                companyImage.setImageURI(jobsViewModel.getImageUri())
            }
            companyImage.setOnClickListener {
                startCrop()
            }

            populateFields(job)

            addTextWatchers(
                etJobTitleContainer,
                etCompanyNameContainer,
                etCityNameContainer,
                etSalaryContainer,
                etJobDescContainer,
                etJobRespContainer
            )

            etSkills.addTextChangedListener { text: Editable? ->
                handleSkillInput(text)
            }

            chipsViewModel.chips.observe(viewLifecycleOwner, Observer { chips ->
                if (chips.isNotEmpty()) {
                    skillChipGroup.removeAllViews()
                    chips.forEach { chip ->
                        createChip(
                            chip,
                            requireContext(),
                            skillChipGroup,
                            chipsViewModel::removeChip
                        )
                    }
                }
            })

            btnSave.setOnClickListener {
                handleSaveClick()
            }
        }
    }

    private fun populateFields(job: Job) {
        binding.apply {
            etJobTitle.setText(job.role)
            etCompanyName.setText(job.name)
            etCityName.setText(job.city)
            etSalary.setText(job.salary)
            etJobDesc.setText(job.description)
            etJobResp.setText(job.responsibility)
            chipsViewModel.addChipsList(job.skillSet)
        }
    }

    private fun addTextWatchers(vararg textInputContainer: TextInputLayout) {
        textInputContainer.forEach { it.addTextWatcher() }
    }

    private fun handleSkillInput(text: Editable?) {
        val value = text.toString()
        if (value.isNotEmpty()) {
            if (value.last() == ',' && value.length > 1) {
                val skill = value.replace(",", "")
                chipsViewModel.addChip(skill)
                text?.clear()
            }
        }
    }

    private fun handleSaveClick() {
        binding.apply {
            val jobRole = etJobTitle.getInputValue()
            val companyName = etCompanyName.getInputValue()
            val city = etCityName.getInputValue()
            val salary = etSalary.getInputValue()
            val jobDescription = etJobDesc.getInputValue()
            val responsibility = etJobResp.getInputValue()
            val imageUrl = jobsViewModel.getImageUri() ?: job.imageUrl
            val skills = chipsViewModel.chips.value?.toMutableList() ?: mutableListOf()

            if (detailVerification(
                    jobRole,
                    companyName,
                    city,
                    salary,
                    jobDescription,
                    responsibility,
                    skills
                )
            ) {
                job.role = jobRole
                job.name = companyName
                job.city = city
                job.salary = salary
                job.description = jobDescription
                job.responsibility = responsibility
                job.imageUrl = imageUrl.toString()
                job.skillSet = skills

                jobEditViewModel.editJob(job = job)
                handleEditResponse()
            }
        }
    }

    private fun handleEditResponse() {
        jobEditViewModel.editOperationStatus.observe(viewLifecycleOwner, Observer { uiState ->
            when (uiState) {
                LOADING -> {
                    showToast(requireContext(), "Loading...")
                }
                SUCCESS -> {
                    showToast(requireContext(), "Success...")
                }
                FAILURE -> {
                    showToast(requireContext(), "Failure...")
                }
                else -> Unit
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
                jobsViewModel.setImageUri(imageUri = data?.data!!)
                binding.companyImage.setImageURI(jobsViewModel.getImageUri())
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
        title: String,
        company: String,
        city: String,
        salary: String,
        description: String,
        responsibility: String,
        skills: MutableList<String>
    ): Boolean {
        binding.apply {
            return if (!InputValidation.checkNullity(title)) {
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
            } else if (!InputValidation.checkNullity(description)) {
                etJobDescContainer.error = getString(R.string.field_error_description)
                return false
            } else if (!InputValidation.checkNullity(responsibility)) {
                etJobRespContainer.error = "Enter valid responsibility"
                return false
            } else if (skills.isEmpty()) {
                showToast(requireContext(), "Enter skills")
                return false
            } else {
                return true
            }
        }
    }
}