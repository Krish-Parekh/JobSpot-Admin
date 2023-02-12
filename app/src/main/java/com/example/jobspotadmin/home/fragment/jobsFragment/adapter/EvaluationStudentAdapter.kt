package com.example.jobspotadmin.home.fragment.jobsFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.jobspotadmin.databinding.EvaluatedStudentCardLayoutBinding
import com.example.jobspotadmin.model.JobStatus

class EvaluationStudentAdapter : RecyclerView.Adapter<EvaluationStudentAdapter.EvaluationStudentViewHolder>() {

    private val evaluatedStudent = mutableListOf<JobStatus>()

    inner class EvaluationStudentViewHolder(
        private val binding: EvaluatedStudentCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(jobStatus: JobStatus) {
            val student = jobStatus.student
            val details = student.details!!
            with(binding) {
                ivStudentProfile.load(details.imageUrl)
                tvStudentName.text = details.username
                tvStudentEmail.text = details.email
                tvApplicantResult.text = jobStatus.jobApplication.applicationStatus
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluationStudentViewHolder {
        return EvaluationStudentViewHolder(
            EvaluatedStudentCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EvaluationStudentViewHolder, position: Int) {
        holder.bind(evaluatedStudent[position])
    }

    override fun getItemCount(): Int = evaluatedStudent.size

    fun setEvaluatedStudent(newEvaluatedStudents: List<JobStatus>) {
        evaluatedStudent.clear()
        evaluatedStudent.addAll(newEvaluatedStudents)
        notifyDataSetChanged()
    }
}