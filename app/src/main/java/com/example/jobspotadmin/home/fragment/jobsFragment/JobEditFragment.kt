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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.jobspotadmin.databinding.FragmentJobEditBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.ChipsViewModel
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobEditViewModel
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Status.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout

private const val TAG = "JobEditFragmentTAG"

class JobEditFragment : Fragment() {
    private var _binding: FragmentJobEditBinding? = null
    private val binding get() = _binding!!
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            handleCapturedImage(result)
        }
    private val chipsViewModel by viewModels<ChipsViewModel>()
    private val jobEditViewModel by viewModels<JobEditViewModel>()
    private val args by navArgs<JobViewFragmentArgs>()
    private val job by lazy { args.job }
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobEditBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {

            populateFields(job)

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            companyImage.load(job.imageUrl)

            if (jobEditViewModel.getImageUri() != null) {
                companyImage.setImageURI(jobEditViewModel.getImageUri())
            }

            companyImage.setOnClickListener {
                startCrop()
            }

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
            val imageUrl = jobEditViewModel.getImageUri() ?: job.imageUrl
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

            }
        }
    }

    private fun setupObserver() {
        chipsViewModel.chips.observe(viewLifecycleOwner) { chips ->
            if (chips.isNotEmpty()) {
                binding.skillChipGroup.removeAllViews()
                chips.forEach { chip ->
                    createChip(
                        chip,
                        requireContext(),
                        binding.skillChipGroup,
                        chipsViewModel::removeChip
                    )
                }
            }
        }
        jobEditViewModel.editJobStatus.observe(viewLifecycleOwner) { editJobStatus ->
            when (editJobStatus.status) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    val successMessage = editJobStatus.data!!
                    showToast(requireContext(), successMessage)
                }
                ERROR -> {
                    loadingDialog.dismiss()
                    val errorMessage = editJobStatus.message!!
                    showToast(requireContext(), errorMessage)
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
                jobEditViewModel.setImageUri(imageUri = data?.data!!)
                binding.companyImage.setImageURI(jobEditViewModel.getImageUri())
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
            val (isJobTitleValid, jobTitleError) = InputValidation.isJobTitleValid(title)
            if (isJobTitleValid.not()) {
                etJobTitleContainer.error = jobTitleError
                return isJobTitleValid
            }

            val (isCompanyNameValid, companyNameError) = InputValidation.isCompanyNameValid(company)
            if (isCompanyNameValid.not()) {
                etCompanyNameContainer.error = companyNameError
                return isCompanyNameValid
            }

            val (isCityValid, cityError) = InputValidation.isCityValid(city)
            if (isCityValid.not()) {
                etCityNameContainer.error = cityError
                return isCityValid
            }

            val (isSalaryValid, salaryError) = InputValidation.isSalaryValid(salary)
            if (isSalaryValid.not()) {
                etSalaryContainer.error = salaryError
                return isSalaryValid
            }

            val (isDescriptionValid, descriptionError) = InputValidation.isJobDescriptionValid(
                description
            )
            if (isDescriptionValid.not()) {
                etJobDescContainer.error = descriptionError
                return isDescriptionValid
            }

            val (isResponsibilityValid, responsibilityError) = InputValidation.isResponsibilityValid(
                responsibility
            )
            if (isResponsibilityValid.not()) {
                etJobRespContainer.error = responsibilityError
                return isResponsibilityValid
            }

            val (isSkillsValid, skillsError) = InputValidation.isSkillSetValid(skills)
            if (isSkillsValid.not()) {
                etSkillsContainer.error = skillsError
                return isSkillsValid
            }
            return true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}