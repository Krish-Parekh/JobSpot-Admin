package com.example.jobspotadmin.home.fragment.jobsFragment

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentJobsBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.adapter.JobListAdapter
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.LoadingDialog
import com.example.jobspotadmin.util.UiState
import com.example.jobspotadmin.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

private const val TAG = "JobsFragment"

class JobsFragment : Fragment() {
    private lateinit var binding: FragmentJobsBinding
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val jobListAdapter: JobListAdapter by lazy { JobListAdapter(this@JobsFragment) }
    private val jobs: MutableList<Job> by lazy { mutableListOf() }
    private val jobsViewModel: JobsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobsBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView() {
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

            rvJobs.apply {
                adapter = jobListAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            jobsViewModel.jobs.observe(viewLifecycleOwner, Observer { jobs ->
                jobListAdapter.setJobListData(newJobs = jobs)
                this@JobsFragment.jobs.clear()
                this@JobsFragment.jobs.addAll(jobs)
            })
        }
    }

    private fun filterJobs(text: Editable?) {
        if (!text.isNullOrEmpty()) {
            val filteredJobList = jobs.filter { job ->
                val title = job.role.lowercase()
                val inputText = text.toString().lowercase()
                title.contains(inputText)
            }
            jobListAdapter.setJobListData(newJobs = filteredJobList)
        } else {
            jobListAdapter.setJobListData(newJobs = jobs)
        }
    }

    fun showDeleteDialog(job: Job) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_file, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnRemoveFile)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            jobsViewModel.deleteJob(job = job)
            handleDeleteResponse()
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }

    fun navigateToJobViewFragment(job: Job) {
        val direction = JobsFragmentDirections.actionJobsFragmentToJobViewFragment(job = job)
        findNavController().navigate(direction)
    }

    private fun handleDeleteResponse() {
        jobsViewModel.operationStatus.observe(viewLifecycleOwner, Observer { uiState ->
            when (uiState) {
                UiState.LOADING -> {
                    loadingDialog.show()
                }
                UiState.SUCCESS -> {
                    showToast(requireContext(), "Job delete success")
                    loadingDialog.dismiss()
                }
                UiState.FAILURE -> {
                    showToast(requireContext(), "Error while deleting job")
                    loadingDialog.dismiss()
                }
                else -> Unit
            }
        })
    }
}