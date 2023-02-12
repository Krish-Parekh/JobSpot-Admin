package com.example.jobspotadmin.home.fragment.jobsFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.jobspotadmin.databinding.PendingStudentsCardLayoutBinding
import com.example.jobspotadmin.model.JobApplication
import com.example.jobspotadmin.model.JobStatus

class PendingStudentAdapter(
    private val setJobStatus: (JobApplication) -> Unit
) : RecyclerView.Adapter<PendingStudentAdapter.PendingStudentViewHolder>() {

    private val pendingStudents = mutableListOf<JobStatus>()

    inner class PendingStudentViewHolder(
        private val binding: PendingStudentsCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(jobStatus: JobStatus) {
            with(binding) {
                val student = jobStatus.student
                val details = student.details!!
                ivStudentProfile.load(details.imageUrl)
                tvStudentName.text = details.username
                tvStudentEmail.text = details.email
                ivAccepted.setOnClickListener {
                    jobStatus.jobApplication.applicationStatus = "Accepted"
                    jobStatus.jobApplication.isEvaluated = true
                    setJobStatus(jobStatus.jobApplication)
                }
                ivRejected.setOnClickListener {
                    jobStatus.jobApplication.applicationStatus = "Declined"
                    jobStatus.jobApplication.isEvaluated = true
                    setJobStatus(jobStatus.jobApplication)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingStudentViewHolder {
        return PendingStudentViewHolder(
            PendingStudentsCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PendingStudentViewHolder, position: Int) {
        holder.bind(jobStatus = pendingStudents[position])
    }

    override fun getItemCount(): Int = pendingStudents.size

    fun setPendingStudent(newPendingStudent: List<JobStatus>) {
        pendingStudents.clear()
        pendingStudents.addAll(newPendingStudent)
        notifyDataSetChanged()
    }
}