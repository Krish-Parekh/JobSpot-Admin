package com.example.jobspotadmin.home.fragment.jobsFragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentJobDetailTwoBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.example.jobspotadmin.util.*

private const val TAG = "JobDetailFragmentTwo"

class JobDetailFragmentTwo : Fragment() {
    private lateinit var binding: FragmentJobDetailTwoBinding
    private val args by navArgs<JobDetailFragmentTwoArgs>()
    private val job by lazy { args.job }
    private val loadingDialog : LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val skillList: MutableList<String> = mutableListOf()
    private val jobsViewModel: JobsViewModel by viewModels()
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
                findNavController().popBackStack(R.id.jobDetailFragmentOne, inclusive = true)
            }

            etSkills.getSkillList(requireContext(), skillChipGroup) { skillName ->
                skillList.add(skillName)
            }

            etJobRespContainer.addTextWatcher()

            btnSave.setOnClickListener {
                val responsibility = etJobResp.getInputValue()
                if (detailVerification(responsibility, skillList)) {
                    job.responsibility = responsibility
                    job.skillSet = skillList
                    val imageUri : Uri = Uri.parse(job.imageUrl)
                    jobsViewModel.uploadData(imageUri, job)
                    handleUploadResponse()
                }
            }
        }
    }

    private fun handleUploadResponse() {
        jobsViewModel.uploadDataStatus.observe(viewLifecycleOwner, Observer { uiState ->
            if (uiState.loading) {
                loadingDialog.show()
            } else if (uiState.success) {
                loadingDialog.dismiss()
                findNavController().popBackStack(R.id.jobDetailFragmentOne, inclusive = true)
            } else if (uiState.failed) {
                loadingDialog.dismiss()
            }
        })
    }

    private fun detailVerification(
        responsibility: String,
        skillList: MutableList<String>
    ): Boolean {
        binding.apply {
            if (!InputValidation.checkNullity(responsibility)) {
                etJobRespContainer.error = "Enter valid responsibility"
                return false
            } else if (skillList.isEmpty()) {
                showToast(requireContext(), "Enter skills")
                return false
            } else {
                return true
            }
        }
    }
}