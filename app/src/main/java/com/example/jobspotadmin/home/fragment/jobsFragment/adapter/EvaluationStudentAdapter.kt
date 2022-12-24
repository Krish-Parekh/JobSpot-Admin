package com.example.jobspotadmin.home.fragment.jobsFragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.model.JobStatus

class EvaluationStudentAdapter : RecyclerView.Adapter<EvaluationStudentAdapter.EvaluationStudentViewHolder>() {

    private val evaluatedStudent = mutableListOf<JobStatus>()

    inner class EvaluationStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val studentProfile : ImageView = itemView.findViewById(R.id.ivStudentProfile)
        private val tvStudentName : TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvStudentEmail : TextView = itemView.findViewById(R.id.tvStudentEmail)
        private val tvApplicationResult : TextView = itemView.findViewById(R.id.tvApplicantResult)
        fun bind(jobStatus : JobStatus){
            val student = jobStatus.student
            val details = student.details!!
            studentProfile.load(details.imageUrl)
            tvStudentName.text = details.username
            tvStudentEmail.text = details.email
            tvApplicationResult.text = jobStatus.jobApplication.applicationStatus
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluationStudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.evaluated_student_card_layout, parent, false)
        return EvaluationStudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvaluationStudentViewHolder, position: Int) {
        holder.bind(evaluatedStudent[position])
    }

    override fun getItemCount(): Int = evaluatedStudent.size

    fun setEvaluatedStudent(newEvaluatedStudents : List<JobStatus>){
        evaluatedStudent.clear()
        evaluatedStudent.addAll(newEvaluatedStudents)
        notifyDataSetChanged()
    }
}