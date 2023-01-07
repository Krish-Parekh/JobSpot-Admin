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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.databinding.FragmentStudentJobBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.adapter.EvaluationStudentAdapter
import com.example.jobspotadmin.home.fragment.jobsFragment.adapter.PendingStudentAdapter
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.StudentJobViewModel
import com.example.jobspotadmin.model.JobApplication
import com.example.jobspotadmin.model.JobStatus

private const val TAG = "StudentJobFragment"
class StudentJobFragment : Fragment() {
    private var _binding: FragmentStudentJobBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<StudentJobFragmentArgs>()
    private val studentJobViewModel: StudentJobViewModel by viewModels()
    private var _pendingStudentAdapter: PendingStudentAdapter ?= null
    private val pendingStudentAdapter get() = _pendingStudentAdapter!!

    private var _evaluationStudentAdapter: EvaluationStudentAdapter ?= null
    private val evaluationStudentAdapter get() = _evaluationStudentAdapter!!
    private val pendingStudents: MutableList<JobStatus> by lazy { mutableListOf() }
    private val evaluatedStudents: MutableList<JobStatus> by lazy { mutableListOf() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentJobBinding.inflate(layoutInflater)
        _pendingStudentAdapter = PendingStudentAdapter(::setJobStatus)
        _evaluationStudentAdapter = EvaluationStudentAdapter()
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        studentJobViewModel.fetchStudents(args.jobId)
        binding.apply {

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            rvApplicants.adapter = pendingStudentAdapter
            rvApplicants.layoutManager = LinearLayoutManager(requireContext())
            rvApplicants.hasFixedSize()
            rvApplicants.setItemViewCacheSize(20)


            rvRecorded.adapter = evaluationStudentAdapter
            rvRecorded.layoutManager = LinearLayoutManager(requireContext())
            rvRecorded.hasFixedSize()
            rvRecorded.setItemViewCacheSize(20)

            etSearch.addTextChangedListener { text: Editable? ->
                filterStudents(text)
            }

            studentJobViewModel.pendingApplications.observe(
                viewLifecycleOwner,
                Observer { pendingStudents ->
                    pendingStudentAdapter.setPendingStudent(pendingStudents)
                    this@StudentJobFragment.pendingStudents.clear()
                    this@StudentJobFragment.pendingStudents.addAll(pendingStudents)
                })

            studentJobViewModel.evaluatedApplication.observe(
                viewLifecycleOwner,
                Observer { evaluatedStudents ->
                    evaluationStudentAdapter.setEvaluatedStudent(evaluatedStudents)
                    this@StudentJobFragment.evaluatedStudents.clear()
                    this@StudentJobFragment.evaluatedStudents.addAll(evaluatedStudents)
                })
        }
    }

    private fun filterStudents(text: Editable?) {
        if (!text.isNullOrEmpty()) {
            if (pendingStudents.isNotEmpty()) {
                val filteredPendingStudents = pendingStudents.filter { jobStatus ->
                    val name = jobStatus.student.details?.username?.lowercase() ?: ""
                    val inputText = text.toString().lowercase()
                    name.contains(inputText)
                }
                pendingStudentAdapter.setPendingStudent(filteredPendingStudents)
            }
            if (evaluatedStudents.isNotEmpty()) {
                val filteredEvaluatedStudents = evaluatedStudents.filter { jobStatus ->
                    val name = jobStatus.student.details?.username?.lowercase() ?: ""
                    val inputText = text.toString().lowercase()
                    name.contains(inputText)
                }
                evaluationStudentAdapter.setEvaluatedStudent(filteredEvaluatedStudents)
            }
        } else {
            pendingStudentAdapter.setPendingStudent(pendingStudents)
            evaluationStudentAdapter.setEvaluatedStudent(evaluatedStudents)
        }
    }

    private fun setJobStatus(jobApplication: JobApplication) {
        studentJobViewModel.setSelectionStatus(jobApplication)
    }

    override fun onDestroyView() {
        _pendingStudentAdapter = null
        _evaluationStudentAdapter = null
        _binding = null
        super.onDestroyView()
    }
}


