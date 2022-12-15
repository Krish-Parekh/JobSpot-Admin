package com.example.jobspotadmin.home.fragment.jobsFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentJobsBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.adapter.JobListAdapter
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.launch

private const val TAG = "JobsFragment"
class JobsFragment : Fragment() {
    private lateinit var binding : FragmentJobsBinding
    private val mFireStore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val jobListAdapter : JobListAdapter by lazy { JobListAdapter(this@JobsFragment) }
    private val jobs : MutableList<Job> by lazy { mutableListOf() }
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

            etSearch.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty()){
                       val filteredList = jobs.filter { job -> job.title.contains(s) }
                        jobListAdapter.setJobListData(filteredList)
                    } else {
                        jobListAdapter.setJobListData(newJobs = jobs)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}

            })

            ivAddJob.setOnClickListener {
                findNavController().navigate(R.id.action_jobsFragment_to_jobDetailFragmentOne)
            }

            rvJobs.adapter = jobListAdapter
            rvJobs.layoutManager = LinearLayoutManager(requireContext())

            jobs.clear()

            lifecycleScope.launch {
                jobs.clear()
                mFireStore
                    .collection(COLLECTION_PATH_COMPANY)
                    .addSnapshotListener { value, error ->
                    if (error == null){
                        val documents = value!!.documents
                        Log.d(TAG, "Job : ${jobs}")
                        for (document in documents){
                            val job = document.toObject(Job::class.java)!!
                            Log.d(TAG, "$job")
                            Log.d(TAG, "$jobs")
                            jobs.add(job)
                        }
                        jobListAdapter.setJobListData(jobs)
                    }
                }
            }
        }
    }

    fun showDeleteDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_file, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnRemoveFile)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }
}