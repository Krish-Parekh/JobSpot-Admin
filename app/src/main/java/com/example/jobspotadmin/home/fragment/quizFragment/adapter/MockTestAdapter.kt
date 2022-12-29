package com.example.jobspotadmin.home.fragment.quizFragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.jobspotadmin.R
import com.example.jobspotadmin.home.fragment.quizFragment.QuizFragment
import com.example.jobspotadmin.home.fragment.quizFragment.adapter.diff_util.MockDetailDiffCallBack
import com.example.jobspotadmin.model.MockDetail

class MockTestAdapter(private val listener: QuizFragment) :
    RecyclerView.Adapter<MockTestAdapter.MockTestViewHolder>() {

    private val mockDetail: MutableList<MockDetail> = mutableListOf()

    inner class MockTestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvQuizName: TextView = itemView.findViewById(R.id.tvQuizName)
        private val tvStudentCount: TextView = itemView.findViewById(R.id.tvQuizStudentCount)
        private val ivDeleteQuiz: ImageView = itemView.findViewById(R.id.ivDeleteQuiz)
        fun bind(mockDetail: MockDetail) {
            tvQuizName.text = mockDetail.mockName
            tvStudentCount.text = itemView.context.getString(
                R.string.field_quiz_student_count,
                mockDetail.studentIds.size
            )
            ivDeleteQuiz.setOnClickListener {
                listener.showDeleteDialog(mockDetail = mockDetail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MockTestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mock_test_card_layout, parent, false)
        return MockTestViewHolder(view)
    }

    override fun onBindViewHolder(holder: MockTestViewHolder, position: Int) {
        holder.bind(mockDetail[position])
    }

    override fun getItemCount(): Int = mockDetail.size

    fun setQuizData(newMockDetail: List<MockDetail>) {
        val diffCallBack = MockDetailDiffCallBack(oldList = mockDetail, newList = newMockDetail)
        val diffMockDetail = DiffUtil.calculateDiff(diffCallBack)
        mockDetail.clear()
        mockDetail.addAll(newMockDetail)
        diffMockDetail.dispatchUpdatesTo(this)
    }
}