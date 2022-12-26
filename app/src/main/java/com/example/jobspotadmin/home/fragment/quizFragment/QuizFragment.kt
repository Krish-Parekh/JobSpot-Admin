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
import com.example.jobspotadmin.home.fragment.quizFragment.adapter.QuizAdapter
import com.example.jobspotadmin.home.fragment.quizFragment.viewmodel.QuizViewModel
import com.example.jobspotadmin.model.QuizDetail
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

private const val TAG = "QuizFragment"
class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding
    private val quizViewModel: QuizViewModel by viewModels()
    private val quizAdapter: QuizAdapter by lazy { QuizAdapter(this@QuizFragment) }
    private val quizDetails : MutableList<QuizDetail> by lazy { mutableListOf() }
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

            quizViewModel.fetchQuiz()

            rvQuiz.adapter = quizAdapter
            rvQuiz.layoutManager = LinearLayoutManager(requireContext())

            quizViewModel.quizDetails.observe(viewLifecycleOwner, Observer { quizDetails ->
                quizAdapter.setQuizData(quizDetails.toMutableList())
                this@QuizFragment.quizDetails.clear()
                this@QuizFragment.quizDetails.addAll(quizDetails)
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
            val filteredQuizList = quizDetails.filter { quizDetail ->
                val title = quizDetail.quizName.lowercase()
                val inputText = text.toString().lowercase()
                title.contains(inputText)
            }
            quizAdapter.setQuizData(newQuizDetail = filteredQuizList)
        } else {
            quizAdapter.setQuizData(newQuizDetail = quizDetails)
        }
    }

    fun showDeleteDialog(quizDetail: QuizDetail) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_quiz, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnRemoveFile)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            Log.d(TAG, "Quiz Detail : ${quizDetail}")
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }
}