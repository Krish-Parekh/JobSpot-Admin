package com.example.jobspotadmin.home.fragment.jobsFragment.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.model.JobApplication
import com.example.jobspotadmin.model.JobStatus

private const val TAG = "PendingStudentAdapterTAG"
class PendingStudentAdapter(private val setJobStatus: (JobApplication) -> Unit) : RecyclerView.Adapter<PendingStudentAdapter.PendingStudentViewHolder>() {

    private val pendingStudents = mutableListOf<JobStatus>()

    inner class PendingStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val studentProfile : ImageView = itemView.findViewById(R.id.ivStudentProfile)
        private val tvStudentName : TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvStudentEmail : TextView = itemView.findViewById(R.id.tvStudentEmail)
        private val ivAccepted : ImageView = itemView.findViewById(R.id.ivAccepted)
        private val ivRejected : ImageView = itemView.findViewById(R.id.ivRejected)

        fun bind(jobStatus : JobStatus){
            val student = jobStatus.student
            val details = student.details!!
            studentProfile.load(details.imageUrl)
            tvStudentName.text = details.username
            tvStudentEmail.text = details.email
            ivAccepted.setOnClickListener {
                Log.d(TAG, "Accepted ${jobStatus.jobApplication.jobId}")
                jobStatus.jobApplication.applicationStatus = "Accepted"
                jobStatus.jobApplication.isEvaluated = true
                setJobStatus(jobStatus.jobApplication)
            }
            ivRejected.setOnClickListener {
                Log.d(TAG, "Rejected ${jobStatus.jobApplication.jobId}")
                jobStatus.jobApplication.applicationStatus = "Declined"
                jobStatus.jobApplication.isEvaluated = true
                setJobStatus(jobStatus.jobApplication)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingStudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pending_students_card_layout, parent, false)
        return PendingStudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingStudentViewHolder, position: Int) {
        holder.bind(jobStatus = pendingStudents[position])
    }

    override fun getItemCount(): Int = pendingStudents.size

    fun setPendingStudent(newPendingStudent : MutableList<JobStatus>){
        pendingStudents.clear()
        pendingStudents.addAll(newPendingStudent)
        notifyDataSetChanged()
    }
}