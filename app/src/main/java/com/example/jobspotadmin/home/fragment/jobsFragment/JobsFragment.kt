package com.example.jobspotadmin.home.fragment.jobsFragment

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentJobsBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.adapter.JobListAdapter
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.example.jobspotadmin.util.LoadingDialog
import com.example.jobspotadmin.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

private const val TAG = "JobsFragment"

class JobsFragment : Fragment() {
    private lateinit var binding: FragmentJobsBinding
    private val mFireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
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

            fetchJobs()
        }
    }

    private fun filterJobs(text: Editable?) {
        if (!text.isNullOrEmpty()) {
            val filteredJobList = jobs.filter { job ->
                val title = job.title.lowercase()
                val inputText = text.toString().lowercase()
                title.contains(inputText)
            }
            jobListAdapter.setJobListData(newJobs = filteredJobList)
        } else {
            jobListAdapter.setJobListData(newJobs = jobs)
        }
    }

    private fun fetchJobs() {
        lifecycleScope.launch {
            mFireStore.collection(COLLECTION_PATH_COMPANY)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.d(TAG, "Error fetching jobs : ${error.message} ")
                        return@addSnapshotListener
                    }
                    jobs.clear()
                    val documents = value!!.documents
                    val jobList = documents.map { document ->
                        document.toObject(Job::class.java)!!
                    }
                    jobs.addAll(jobList)
                    jobListAdapter.setJobListData(newJobs = jobs)
                }
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
            jobsViewModel.deleteData(job = job)
            handleDeleteResponse()
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }

    private fun handleDeleteResponse() {
        jobsViewModel.deleteDataStatus.observe(viewLifecycleOwner, Observer { uiState ->
            if (uiState.loading) {
                Log.d(TAG, "Loading....")
                loadingDialog.show()
            } else if (uiState.success) {
                showToast(requireContext(), "Job delete success")
                loadingDialog.dismiss()
            } else if (uiState.failed) {
                loadingDialog.dismiss()
            }
        })
    }
}