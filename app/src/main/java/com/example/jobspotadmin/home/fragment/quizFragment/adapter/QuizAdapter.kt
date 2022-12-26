package com.example.jobspotadmin.home.fragment.quizFragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jobspotadmin.R
import com.example.jobspotadmin.home.fragment.quizFragment.QuizFragment
import com.example.jobspotadmin.model.QuizDetail

class QuizAdapter(private val listener : QuizFragment) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    private val quizDetail: MutableList<QuizDetail> = mutableListOf()

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvQuizName: TextView = itemView.findViewById(R.id.tvQuizName)
        private val tvStudentCount: TextView = itemView.findViewById(R.id.tvQuizStudentCount)
        private val ivDeleteQuiz : ImageView = itemView.findViewById(R.id.ivDeleteQuiz)
        fun bind(quizDetail: QuizDetail) {
            tvQuizName.text = quizDetail.quizName
            tvStudentCount.text = itemView.context.getString(R.string.field_quiz_student_count, quizDetail.studentIds.size)
            ivDeleteQuiz.setOnClickListener {
                listener.showDeleteDialog(quizDetail = quizDetail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.quiz_card_layout, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(quizDetail[position])
    }

    override fun getItemCount(): Int = quizDetail.size

    fun setQuizData(newQuizDetail: List<QuizDetail>) {
        quizDetail.clear()
        quizDetail.addAll(newQuizDetail)
        notifyDataSetChanged()
    }
}