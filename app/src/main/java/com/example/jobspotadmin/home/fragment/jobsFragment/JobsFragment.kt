package com.example.jobspotadmin.home.fragment.jobsFragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.BottomSheetDeleteJobBinding
import com.example.jobspotadmin.databinding.FragmentJobsBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.adapter.JobAdapter
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.LoadingDialog
import com.example.jobspotadmin.util.Status.*
import com.example.jobspotadmin.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog


class JobsFragment : Fragment() {
    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!
    private var _jobAdapter: JobAdapter? = null
    private val jobAdapter get() = _jobAdapter!!
    private val jobs: MutableList<Job> by lazy { mutableListOf() }
    private val jobsViewModel by viewModels<JobsViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJobsBinding.inflate(inflater, container, false)
        _jobAdapter = JobAdapter(this@JobsFragment)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            jobsViewModel.fetchJobs()

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            etSearch.addTextChangedListener { text: Editable? ->
                filterJobs(text)
            }
            ivAddJob.setOnClickListener {
                findNavController().navigate(R.id.action_jobsFragment_to_jobDetailFragmentOne)
            }

            rvJobs.adapter = jobAdapter
            rvJobs.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObserver() {
        jobsViewModel.jobs.observe(viewLifecycleOwner){ jobState ->
            when(jobState.status){
                LOADING -> {
                    // Loading
                }
                SUCCESS -> {
                    val jobList = jobState.data!!
                    jobAdapter.setJobListData(jobList)
                    jobs.clear()
                    jobs.addAll(jobList)
                }
                ERROR -> {
                    val errorMessage = jobState.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }

        jobsViewModel.deleteJobStatus.observe(viewLifecycleOwner){ deleteState ->
            when(deleteState.status){
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    val successMessage = deleteState.data!!
                    showToast(requireContext(), successMessage)
                }
                ERROR -> {
                    loadingDialog.dismiss()
                    val errorMessage = deleteState.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }

    }

    private fun filterJobs(text: Editable?) {
        if (text.isNullOrEmpty().not()) {
            val filteredJobList = jobs.filter { job ->
                val title = job.role.lowercase()
                val inputText = text.toString().lowercase()
                title.contains(inputText)
            }
            jobAdapter.setJobListData(newJobs = filteredJobList)
        } else {
            jobAdapter.setJobListData(newJobs = jobs)
        }
    }

    fun deleteJobDialog(job: Job) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val jobDeleteSheetBinding = BottomSheetDeleteJobBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(jobDeleteSheetBinding.root)
        jobDeleteSheetBinding.apply {
            btnNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            btnRemoveJob.setOnClickListener {
                jobsViewModel.deleteJob(job)
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    fun navigateToJobViewFragment(job: Job) {
        val direction = JobsFragmentDirections.actionJobsFragmentToJobViewFragment(job = job)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        _jobAdapter = null
        _binding = null
        super.onDestroyView()
    }
}