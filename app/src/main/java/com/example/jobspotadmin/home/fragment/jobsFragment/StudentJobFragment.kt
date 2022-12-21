package com.example.jobspotadmin.home.fragment.jobsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.databinding.FragmentStudentJobBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.adapter.EvaluationStudentAdapter
import com.example.jobspotadmin.home.fragment.jobsFragment.adapter.PendingStudentAdapter
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.StudentJobViewModel
import com.example.jobspotadmin.model.JobApplication

private const val TAG = "StudentJobFragment"
class StudentJobFragment : Fragment() {
    private lateinit var binding: FragmentStudentJobBinding
    private val args by navArgs<StudentJobFragmentArgs>()
    private val studentJobViewModel : StudentJobViewModel by viewModels()
    private val pendingStudentAdapter : PendingStudentAdapter by lazy { PendingStudentAdapter(::setJobStatus) }
    private val evaluationStudentAdapter : EvaluationStudentAdapter by lazy { EvaluationStudentAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentJobBinding.inflate(layoutInflater)
        studentJobViewModel.fetchStudents(args.jobId)

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        binding.apply {
            rvApplicants.adapter = pendingStudentAdapter
            rvApplicants.layoutManager = LinearLayoutManager(requireContext())
            rvApplicants.hasFixedSize()
            rvApplicants.setItemViewCacheSize(20)

            rvRecorded.adapter = evaluationStudentAdapter
            rvRecorded.layoutManager = LinearLayoutManager(requireContext())
            rvRecorded.hasFixedSize()
            rvRecorded.setItemViewCacheSize(20)

            studentJobViewModel.pendingApplications.observe(viewLifecycleOwner, Observer { pendingStudents ->
                pendingStudentAdapter.setPendingStudent(pendingStudents)
            })

            studentJobViewModel.evaluatedApplication.observe(viewLifecycleOwner, Observer { evaluatedStudents ->
                evaluationStudentAdapter.setEvaluatedStudent(evaluatedStudents)
            })
        }
    }

    private fun setJobStatus(jobApplication: JobApplication){
        studentJobViewModel.setSelectionStatus(jobApplication)
    }

}


