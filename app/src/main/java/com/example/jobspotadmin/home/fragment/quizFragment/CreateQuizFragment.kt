package com.example.jobspotadmin.home.fragment.quizFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentCreateQuizBinding
import com.example.jobspotadmin.databinding.QuestionCardLayoutBinding
import com.example.jobspotadmin.home.fragment.quizFragment.viewmodel.MockViewModel
import com.example.jobspotadmin.model.Mock
import com.example.jobspotadmin.model.MockQuestion
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Status.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "CreateQuizFragmentTAG"

class CreateQuizFragment : Fragment() {
    private var _binding: FragmentCreateQuizBinding? = null
    private val binding get() = _binding!!
    private val options = listOf("A", "B", "C", "D")
    private val mockTestQuestions: MutableList<MockQuestion> = mutableListOf()
    private val mockViewModel by viewModels<MockViewModel>()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateQuizBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        with(binding) {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }
            etQuizTitleContainer.addTextWatcher()
            etDurationContainer.addTextWatcher()
            btnAddQuestion.setOnClickListener {
                if (mockViewModel.mockQuestionCounter <= 10) {
                    addQuestionView()
                    mockViewModel.increment()
                } else {
                    showToast(requireContext(), "Can't add more than 10 questions.")
                }
            }
            tvSubmitQuiz.setOnClickListener {
                submitQuizDialog()
            }
        }
    }

    private fun setupObserver() {
        mockViewModel.mockTestUploadStatus.observe(viewLifecycleOwner){ resource ->
            when(resource.status){
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    mockTestQuestions.clear()
                    val successMessage = resource.data!!
                    showToast(requireContext(), successMessage)
                    findNavController().popBackStack()
                }
                ERROR -> {
                    loadingDialog.dismiss()
                    val errorMessage = resource.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }
    }

    private fun addQuestionView() {
        val questionCard = QuestionCardLayoutBinding.inflate(layoutInflater)
        val childCount = binding.questionContainer.childCount
        questionCard.root.setTag(childCount)
        questionCard.tvQuestionCount.text = getString(R.string.question_count, childCount + 1)
        questionCard.ivDeleteQuestion.setOnClickListener {
            val index = questionCard.root.tag as Int
            deleteQuestionDialog(index)
        }
        binding.questionContainer.addView(questionCard.root)
    }

    private fun deleteQuestionDialog(index: Int) {
        val deleteDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
        deleteDialog.setTitle("Delete Question")
        deleteDialog.setMessage("Are you sure you want to delete the question?")
        deleteDialog.setPositiveButton("Yes") { dialog, _ ->
            deleteView(index)
            dialog.dismiss()
        }
        deleteDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        deleteDialog.show()
    }

    private fun deleteView(index: Int) {
        val questionCard = binding.questionContainer.getChildAt(index)
        binding.questionContainer.removeView(questionCard)
        mockViewModel.decrement()
        if (mockTestQuestions.size > index) {
            mockTestQuestions.removeAt(index)
        }
        updateQuestionView()
    }

    private fun updateQuestionView() {
        val questionCount = binding.questionContainer.childCount
        (0 until questionCount).forEachIndexed { index, _ ->
            val questionCard = binding.questionContainer.getChildAt(index)
            val tvQuestionCount: TextView = questionCard.findViewById(R.id.tvQuestionCount)
            questionCard.setTag(index)
            tvQuestionCount.text = getString(R.string.question_count, index + 1)
        }
    }

    private fun submitQuizDialog() {
        val submitDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
        submitDialog.setTitle("Submit Mock")
        submitDialog.setMessage("Are you sure you want to add this Mock test?")
        submitDialog.setPositiveButton("Yes") { dialog, _ ->
            val questions = getMockTestQuestions()
            submitMockTest(questions)
            dialog.dismiss()
        }
        submitDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        submitDialog.show()
    }

    private fun submitMockTest(questions: MutableList<MockQuestion>) {
        questions.forEachIndexed { index, question ->
            if (isQuestionValid(question)) {
                if (index == questions.lastIndex) {
                    val title = binding.etQuizTitle.getInputValue()
                    val duration = binding.etDuration.getInputValue()
                    if (isMockTestDetailValid(title, duration)) {
                        val mock = Mock(title = title, duration = duration, mockQuestion = questions)
                        mockViewModel.uploadMockTest(mock)
                    }
                }
            } else {
                val questionCard = binding.questionContainer.getChildAt(index)
                val locationX = questionCard.x
                val locationY = questionCard.y
                binding.questionScrollContainer.smoothScrollTo(locationX.toInt(), locationY.toInt())
                return
            }
        }
    }

    private fun getMockTestQuestions(): MutableList<MockQuestion> {
        val childCount = (0 until binding.questionContainer.childCount)
        mockTestQuestions.clear()
        childCount.forEachIndexed { index, _ ->
            val questionView = binding.questionContainer.getChildAt(index)
            val questionCard = QuestionCardLayoutBinding.bind(questionView)
            questionCard.apply {
                val optionOne = etOptionOne.getInputValue()
                val optionTwo = etOptionTwo.getInputValue()
                val optionThree = etOptionThree.getInputValue()
                val optionFour = etOptionFour.getInputValue()
                val question = etQuestion.getInputValue()
                val feedback = etFeedBack.getInputValue()
                var correctOption: String = ""
                if (correctAnsSpinner.selectedIndex != -1) {
                    correctOption = options[correctAnsSpinner.selectedIndex]
                }
                val questionOptions = listOf(optionOne, optionTwo, optionThree, optionFour)
                val mockQuestion = MockQuestion(
                    question = question,
                    options = questionOptions,
                    correctOption = correctOption,
                    feedback = feedback
                )
                mockTestQuestions.add(mockQuestion)
            }
        }
        return mockTestQuestions
    }

    private fun isMockTestDetailValid(title: String, duration: String): Boolean {
        with(binding) {
            val (isMockTitleValid, mockTitleError) = InputValidation.isMockTitleValid(title)
            if (isMockTitleValid.not()) {
                etQuizTitleContainer.error = mockTitleError
                return isMockTitleValid
            }

            val (isDurationValid, durationError) = InputValidation.isDurationValid(duration)
            if (isDurationValid.not()) {
                etDurationContainer.error = durationError
                return isDurationValid
            }
            return true
        }
    }

    private fun isQuestionValid(mockQuestion: MockQuestion): Boolean {
        val (isQuestionValid, questionError) = InputValidation.isQuestionValid(mockQuestion.question)
        if (isQuestionValid.not()) {
            showToast(requireContext(), questionError)
            return isQuestionValid
        }

        val (isOptionsValid, optionsError) = InputValidation.isOptionsValid(mockQuestion.options)
        if (isOptionsValid.not()) {
            showToast(requireContext(), optionsError)
            return isOptionsValid
        }

        if (InputValidation.checkNullity(mockQuestion.correctOption)) {
            showToast(requireContext(), "Please choose correct-answer.")
            return false
        }

        val (isFeedbackValid, feedbackError) = InputValidation.isFeedbackValid(mockQuestion.feedback)
        if (isFeedbackValid.not()) {
            showToast(requireContext(), feedbackError)
            return isFeedbackValid
        }
        return true
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}