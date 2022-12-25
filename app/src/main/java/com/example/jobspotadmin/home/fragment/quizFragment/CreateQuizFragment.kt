package com.example.jobspotadmin.home.fragment.quizFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentCreateQuizBinding
import com.example.jobspotadmin.model.Question
import com.example.jobspotadmin.util.getInputValue
import com.google.android.material.textfield.TextInputEditText
import com.skydoves.powerspinner.PowerSpinnerView

private const val TAG = "CreateQuizFragmentTAG"

class CreateQuizFragment : Fragment() {
    private lateinit var binding: FragmentCreateQuizBinding
    private val options = listOf("A", "B", "C", "D")
    private val quizQuestions: MutableList<Question> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateQuizBinding.inflate(inflater, container, false)

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        binding.apply {

            btnAddQuestion.setOnClickListener {
                addQuestionView()
            }
            tvSubmitQuiz.setOnClickListener {
                val questions = getQuizQuestions()
                submitQuestions(questions)
            }
        }
    }

    private fun addQuestionView() {
        val questionCard =
            layoutInflater.inflate(R.layout.question_card_layout, binding.questionContainer, false)
        val questionTextView: TextView = questionCard.findViewById(R.id.tvQuestionCount)
        val deleteBtn: ImageView = questionCard.findViewById(R.id.ivDeleteQuestion)
        val childCount = binding.questionContainer.childCount
        questionTextView.text = getString(R.string.question_count, childCount + 1)
        binding.questionContainer.addView(questionCard)
        deleteBtn.setOnClickListener {
            deleteQuestion(childCount)
            updateView()
        }
    }

    private fun submitQuestions(questions: MutableList<Question>) {
        Log.d(TAG, "Questions : ${questions}")
        questions.forEachIndexed { index, question ->
            if (verifyQuestion(question)) {
                Log.d(TAG, "Submit Questions")
            } else {
                val questionCard = binding.questionContainer.getChildAt(index)
                val locationX = questionCard.x
                val locationY = questionCard.y
                Log.d(TAG, "Index : ${index}, Question : ${question}")
                Log.d(TAG, "LocationX : ${locationX}, LocationY: ${locationY}")
                Toast.makeText(requireContext(), "Question ${index + 1}", Toast.LENGTH_SHORT).show()
                binding.root.smoothScrollTo(locationX.toInt(), locationY.toInt())
                return
            }
        }
    }

    private fun deleteQuestion(index: Int) {
        Log.d(TAG, "Delete Card Index : ${index}")
        Log.d(TAG, "Questions Size : ${quizQuestions.size}")
        val questionCard = binding.questionContainer.getChildAt(index)
        binding.questionContainer.removeView(questionCard)
        if (quizQuestions.size > index) {
            quizQuestions.removeAt(index)
        }
    }

    private fun updateView() {
        val newCount = (0 until binding.questionContainer.childCount)
        newCount.forEachIndexed { index, value ->
            val questionCard = binding.questionContainer.getChildAt(index)
            val questionCount: TextView = questionCard.findViewById(R.id.tvQuestionCount)
            questionCount.text = getString(R.string.question_count, index + 1)
        }
    }

    private fun getQuizQuestions(): MutableList<Question> {
        val childCount = (0 until binding.questionContainer.childCount)
        quizQuestions.clear()
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
            val quizQuestion = Question(
                question = question.getInputValue(),
                options = questionOptions,
                correctOption = correctOption,
                feedback = feedBack.getInputValue()
            )
            quizQuestions.add(index, quizQuestion)
            Log.d(TAG, "Index : ${index}, Quiz Questions : ${quizQuestion}")
        }
        return quizQuestions
    }

    private fun verifyQuestion(question: Question): Boolean {
        val fields = question::class.java.declaredFields
        for (field in fields) {
            field.isAccessible = true
            when (val value = field.get(question)) {
                "" -> return false
                is List<*> -> if (value.isEmpty()) return false
            }
        }
        return true
    }
}