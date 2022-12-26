package com.example.jobspotadmin.home.fragment.quizFragment

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentQuizBinding
import com.example.jobspotadmin.home.fragment.quizFragment.adapter.MockTestAdapter
import com.example.jobspotadmin.home.fragment.quizFragment.viewmodel.MockViewModel
import com.example.jobspotadmin.model.MockDetail
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

private const val TAG = "QuizFragment"
class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding
    private val mockViewModel: MockViewModel by viewModels()
    private val mockTestAdapter: MockTestAdapter by lazy { MockTestAdapter(this@QuizFragment) }
    private val mockDetails : MutableList<MockDetail> by lazy { mutableListOf() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizBinding.inflate(inflater, container, false)

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        binding.apply {

            mockViewModel.fetchMockTest()

            rvQuiz.adapter = mockTestAdapter
            rvQuiz.layoutManager = LinearLayoutManager(requireContext())

            mockViewModel.mockDetails.observe(viewLifecycleOwner, Observer { quizDetails ->
                mockTestAdapter.setQuizData(quizDetails.toMutableList())
                this@QuizFragment.mockDetails.clear()
                this@QuizFragment.mockDetails.addAll(quizDetails)
            })

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            etSearch.addTextChangedListener { text: Editable? ->
                filterQuiz(text)

            }

            ivAddQuiz.setOnClickListener {
                findNavController().navigate(R.id.action_quizFragment_to_createQuizFragment)
            }
        }
    }

    private fun filterQuiz(text: Editable?) {
        if (!text.isNullOrEmpty()) {
            val filteredQuizList = mockDetails.filter { quizDetail ->
                val title = quizDetail.quizName.lowercase()
                val inputText = text.toString().lowercase()
                title.contains(inputText)
            }
            mockTestAdapter.setQuizData(newMockDetail = filteredQuizList)
        } else {
            mockTestAdapter.setQuizData(newMockDetail = mockDetails)
        }
    }

    fun showDeleteDialog(mockDetail: MockDetail) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_quiz, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnRemoveFile)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            Log.d(TAG, "Mock Detail : ${mockDetail}")
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }
}