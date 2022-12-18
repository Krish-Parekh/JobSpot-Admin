package com.example.jobspotadmin.home.fragment.jobsFragment

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
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

private const val TAG = "JobDetailFragmentTwo"

class JobDetailFragmentTwo : Fragment() {
    private lateinit var binding: FragmentJobDetailTwoBinding
    private val args by navArgs<JobDetailFragmentTwoArgs>()
    private val job by lazy { args.job }
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val jobsViewModel: JobsViewModel by viewModels()
    private val chipsViewModel : ChipsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJobDetailTwoBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView() {
        binding.apply {

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

            chipsViewModel.chips.observe(viewLifecycleOwner, Observer { chips ->
                if (chips.isNotEmpty()) {
                    skillChipGroup.removeAllViews()
                    chips.forEach { chip ->
                        createChip(chip, requireContext(), skillChipGroup, chipsViewModel::removeChip)
                    }
                }
            })


            btnSave.setOnClickListener {
                val responsibility = etJobResp.getInputValue()
                val skills = chipsViewModel.chips.value?.toMutableList() ?: mutableListOf()
                if (detailVerification(responsibility, skills)) {
                    job.responsibility = responsibility
                    job.skillSet = skills
                    val imageUri: Uri = Uri.parse(job.imageUrl)
                    jobsViewModel.uploadData(imageUri, job)
                    handleUploadResponse()
                }
            }
        }
    }

    private fun handleUploadResponse() {
        jobsViewModel.operationStatus.observe(viewLifecycleOwner, Observer { uiState ->
            when (uiState) {
                UiState.LOADING -> {
                    loadingDialog.show()
                }
                UiState.SUCCESS -> {
                    loadingDialog.dismiss()
                    findNavController().popBackStack(R.id.jobDetailFragmentOne, inclusive = true)
                }
                UiState.FAILURE -> {
                    loadingDialog.dismiss()
                }
                else -> Unit
            }
        })
    }

    private fun detailVerification(
        responsibility: String,
        skills: MutableList<String>
    ): Boolean {
        binding.apply {
            if (!InputValidation.checkNullity(responsibility)) {
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