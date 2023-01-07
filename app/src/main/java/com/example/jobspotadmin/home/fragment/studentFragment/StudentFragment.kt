package com.example.jobspotadmin.home.fragment.studentFragment

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.databinding.FragmentStudentBinding
import com.example.jobspotadmin.home.fragment.studentFragment.adapter.StudentAdapter
import com.example.jobspotadmin.home.fragment.studentFragment.viewModel.StudentViewModel
import com.example.jobspotadmin.model.Student


class StudentFragment : Fragment() {
    private var _binding : FragmentStudentBinding? = null
    private val binding get() = _binding!!

    private var _studentAdapter : StudentAdapter? = null
    private val studentAdapter get() = _studentAdapter!!

    private val studentViewModel by viewModels<StudentViewModel>()
    private val students : MutableList<Student> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentBinding.inflate(inflater, container, false)
        _studentAdapter = StudentAdapter(this@StudentFragment)

        setupUI()
        setupObserver()
        return binding.root
    }

    private fun setupUI() {
        studentViewModel.fetchStudents()
        binding.apply {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            etSearch.addTextChangedListener { text: Editable? ->
                filterStudents(text)
            }

            rvStudent.apply {
                adapter = studentAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun filterStudents(text: Editable?) {
        if (!text.isNullOrEmpty()) {
            val filteredStudent = students.filter { student ->
                val username = student.details?.username?.lowercase()!!
                val inputText = text.toString().lowercase()
                username.contains(inputText)
            }
            studentAdapter.setData(newStudents = filteredStudent)
        } else {
            studentAdapter.setData(newStudents = students)
        }
    }

    private fun setupObserver() {
        studentViewModel.students.observe(viewLifecycleOwner){ studentList ->
            studentAdapter.setData(studentList)
            students.clear()
            students.addAll(studentList)
        }
    }

    fun navigateToStudentView(student : Student){
        val direction = StudentFragmentDirections.actionStudentFragmentToStudentViewFragment(student = student)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        _binding = null
        _studentAdapter = null
        super.onDestroyView()
    }

}