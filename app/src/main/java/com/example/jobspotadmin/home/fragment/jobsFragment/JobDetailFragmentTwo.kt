package com.example.jobspotadmin.home.fragment.jobsFragment

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentJobDetailTwoBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.ChipsViewModel
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Status.*

private const val TAG = "JobDetailFragmentTwo"

class JobDetailFragmentTwo : Fragment() {
    private var _binding: FragmentJobDetailTwoBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<JobDetailFragmentTwoArgs>()
    private val job by lazy { args.job }
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val jobsViewModel by viewModels<JobsViewModel>()
    private val chipsViewModel by viewModels<ChipsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobDetailTwoBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        with(binding) {

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            etJobRespContainer.addTextWatcher()

            etSkills.addTextChangedListener { text: Editable? ->
                val value = text.toString()
                if (value.isNotEmpty()) {
                    if (value.last() == ',' && value.length > 1) {
                        val skill = value.replace(",", "")
                        chipsViewModel.addChip(skill)
                        text?.clear()
                    }
                }
            }

            btnSave.setOnClickListener {
                val responsibility = etJobResp.getInputValue()
                val skills = chipsViewModel.chips.value?.toMutableList() ?: mutableListOf()
                if (detailVerification(responsibility, skills)) {
                    job.responsibility = responsibility
                    job.skillSet = skills
                    val imageUri = Uri.parse(job.imageUrl)
                    jobsViewModel.uploadData(imageUri, job)
                }
            }
        }
    }

    private fun setupObserver() {
        chipsViewModel.chips.observe(viewLifecycleOwner) { chips ->
            if (chips.isNotEmpty()) {
                binding.skillChipGroup.removeAllViews()
                chips.forEach { chip ->
                    createChip(chip, requireContext(), binding.skillChipGroup, chipsViewModel::removeChip)
                }
            }
        }


        jobsViewModel.uploadJobStatus.observe(viewLifecycleOwner){ resource ->
            when(resource.status){
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    val successMessage = resource.data!!
                    showToast(requireContext(), successMessage)
                    findNavController().popBackStack(R.id.jobDetailFragmentOne, inclusive = true)
                }
                ERROR -> {
                    loadingDialog.dismiss()
                    val errorMessage = resource.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }
    }

    private fun detailVerification(
        responsibility: String,
        skills: MutableList<String>
    ): Boolean {
        with(binding) {
            val (isResponsibilityValid, responsibilityError) = InputValidation.isResponsibilityValid(responsibility)
            if (isResponsibilityValid.not()){
                etJobRespContainer.error = responsibilityError
                return isResponsibilityValid
            }

            val (isSkillsValid, skillsError) = InputValidation.isSkillSetValid(skills)
            if (isSkillsValid.not()){
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