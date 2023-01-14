package com.example.jobspotadmin.home.fragment.quizFragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentCreateQuizBinding
import com.example.jobspotadmin.databinding.QuestionCardLayoutBinding
import com.example.jobspotadmin.home.fragment.quizFragment.viewmodel.MockViewModel
import com.example.jobspotadmin.model.Mock
import com.example.jobspotadmin.model.MockQuestion
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.UiState.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.skydoves.powerspinner.PowerSpinnerView

private const val TAG = "CreateQuizFragmentTAG"

class CreateQuizFragment : Fragment() {
    private var _binding: FragmentCreateQuizBinding? = null
    private val binding get() = _binding!!
    private val options = listOf("A", "B", "C", "D")
    private val mockTestQuestions: MutableList<MockQuestion> = mutableListOf()
    private val mockViewModel: MockViewModel by viewModels()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateQuizBinding.inflate(inflater, container, false)

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        binding.apply {

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
                    showToast(requireContext(), "Can't add more than 10")
                }
            }
            tvSubmitQuiz.setOnClickListener {
                submitQuizDialog("Submit Mock", "Are you sure you want to add this Mock test?")
            }
        }
    }

    private fun submitQuizDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                val questions = getMockTestQuestions()
                submitMockTestQuestions(questions)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addQuestionView() {
        val questionCard =
            layoutInflater.inflate(R.layout.question_card_layout, binding.questionContainer, false)
        val questionTextView: TextView = questionCard.findViewById(R.id.tvQuestionCount)
        val deleteBtn: ImageView = questionCard.findViewById(R.id.ivDeleteQuestion)
        val childCount = binding.questionContainer.childCount
        questionCard.setTag(childCount)
        questionTextView.text = getString(R.string.question_count, childCount + 1)
        binding.questionContainer.addView(questionCard)
        deleteBtn.setOnClickListener {
            val index = questionCard.tag as Int
            showDeleteDialog(index)
        }
    }

    private fun submitMockTestQuestions(mockQuestions: MutableList<MockQuestion>) {
        mockQuestions.forEachIndexed { index, question ->
            if (isQuestionValid(question)) {
                if (index == mockQuestions.lastIndex) {
                    val title = binding.etQuizTitle.getInputValue()
                    val duration = binding.etDuration.getInputValue()
                    if (areMockTestDetailValid(title, duration)) {
                        val mock = Mock(
                            title = title,
                            duration = duration,
                            mockQuestion = mockQuestions
                        )
                        Log.d(TAG, "Mock : $mock")
                        mockViewModel.uploadMockTest(mock)
                        handleMockTestUpload()
                    }
                }
            } else {
                val questionCard = binding.questionContainer.getChildAt(index)
                val locationX = questionCard.x
                val locationY = questionCard.y
                showToast(requireContext(), "MockQuestion ${index + 1}")
                binding.questionScrollContainer.smoothScrollTo(locationX.toInt(), locationY.toInt())
                return
            }
        }
    }

    private fun deleteQuestion(index: Int) {
        val questionCard = binding.questionContainer.getChildAt(index)
        binding.questionContainer.removeView(questionCard)
        mockViewModel.decrement()
        if (mockTestQuestions.size > index) {
            mockTestQuestions.removeAt(index)
        }
    }

    //Once a view is deleted we need to update the mockQuestion count
    private fun updateView() {
        val newCount = (0 until binding.questionContainer.childCount)
        newCount.forEachIndexed { index, _ ->
            val questionCard = binding.questionContainer.getChildAt(index)
            val questionCount: TextView = questionCard.findViewById(R.id.tvQuestionCount)
            questionCard.setTag(index)
            questionCount.text = getString(R.string.question_count, index + 1)
        }
    }

    private fun getMockTestQuestions(): MutableList<MockQuestion> {
        val childCount = (0 until binding.questionContainer.childCount)
        mockTestQuestions.clear()
        childCount.forEach { index ->
            val questionCard = binding.questionContainer.getChildAt(index)
            val question: TextInputEditText = questionCard.findViewById(R.id.etQuestion)
            val optionOne: TextInputEditText = questionCard.findViewById(R.id.etOptionOne)
            val optionTwo: TextInputEditText = questionCard.findViewById(R.id.etOptionTwo)
            val optionThree: TextInputEditText = questionCard.findViewById(R.id.etOptionThree)
            val optionFour: TextInputEditText = questionCard.findViewById(R.id.etOptionFour)
            val correctAns: PowerSpinnerView = questionCard.findViewById(R.id.correctAnsSpinner)
            val feedBack: TextInputEditText = questionCard.findViewById(R.id.etFeedBack)

            var correctOption: String = ""
            if (correctAns.selectedIndex != -1) {
                correctOption = options[correctAns.selectedIndex]
            }
            val questionOptions = listOf(
                optionOne.getInputValue(),
                optionTwo.getInputValue(),
                optionThree.getInputValue(),
                optionFour.getInputValue()
            )
            val quizMockQuestion = MockQuestion(
                question = question.getInputValue(),
                options = questionOptions,
                correctOption = correctOption,
                feedback = feedBack.getInputValue()
            )
            mockTestQuestions.add(index, quizMockQuestion)
        }
        return mockTestQuestions
    }

    private fun handleMockTestUpload() {
        mockViewModel.mockTestUploadStatus.observe(viewLifecycleOwner, Observer { uiState ->
            when (uiState) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    mockTestQuestions.clear()
                    findNavController().popBackStack()
                    showToast(requireContext(), "Mock Uploaded")
                }
                FAILURE -> {
                    loadingDialog.dismiss()
                }
                else -> Unit
            }
        })
    }

    private fun isQuestionValid(mockQuestion: MockQuestion): Boolean {
        binding.apply {
            val (isQuestionValid, questionError) = InputValidation.isQuestionValid(mockQuestion.question)
            if (isQuestionValid.not()){
                return isQuestionValid
            }

            val (isOptionsValid, optionsError) = InputValidation.isOptionsValid(mockQuestion.options)
            if (isOptionsValid.not()){
                return isQuestionValid
            }

            if (InputValidation.checkNullity(mockQuestion.correctOption)) {
                return false
            }

            val (isFeedbackValid, feedbackError) = InputValidation.isFeedbackValid(mockQuestion.feedback)
            if (isFeedbackValid.not()){
                return isFeedbackValid
            }
            return true
        }
    }

    private fun areMockTestDetailValid(title: String, duration: String): Boolean {
        binding.apply {
            val (isMockTitleValid, mockTitleError) = InputValidation.isMockTitleValid(title)
            if (isMockTitleValid.not()){
                etQuizTitleContainer.error = mockTitleError
                return isMockTitleValid
            }

            val (isDurationValid, durationError) = InputValidation.isDurationValid(duration)
            if(isDurationValid.not()){
                etDurationContainer.error = durationError
                return isDurationValid
            }
            return true
        }
    }

    private fun showDeleteDialog(index: Int) {
        val dialog = Dialog(requireContext())
        val deleteDialog = layoutInflater.inflate(R.layout.delete_dialog, null)
        deleteDialog.apply {
            val deleteBtn: TextView = findViewById(R.id.tvDelete)
            val noBtn: TextView = findViewById(R.id.tvNo)
            deleteBtn.setOnClickListener {
                deleteQuestion(index)
                updateView()
                dialog.dismiss()
            }
            noBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setContentView(deleteDialog)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}