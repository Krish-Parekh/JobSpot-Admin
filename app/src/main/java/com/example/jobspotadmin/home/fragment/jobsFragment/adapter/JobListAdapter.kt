package com.example.jobspotadmin.home.fragment.jobsFragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.home.fragment.jobsFragment.JobsFragment
import com.example.jobspotadmin.model.Job
import com.google.android.material.card.MaterialCardView

class JobListAdapter(private val listener: JobsFragment) :
    RecyclerView.Adapter<JobListAdapter.JobListAdapterViewHolder>() {

    private var jobs: MutableList<Job> = mutableListOf()

    inner class JobListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val companyLogo: ImageView = itemView.findViewById(R.id.ivCompanyLogo)
        private val jobRole: TextView = itemView.findViewById(R.id.tvJobRole)
        private val companyName: TextView = itemView.findViewById(R.id.tvCompanyName)
        private val deleteBtn: ImageView = itemView.findViewById(R.id.ivDeleteJob)
        private val jobCardView: MaterialCardView = itemView.findViewById(R.id.cvJob)

        fun bind(job: Job) {
            companyLogo.load(job.imageUrl) {
                error(R.drawable.ic_apple_logo)
                placeholder(R.drawable.ic_jobs)
                build()
            }
            jobRole.text = job.role
            companyName.text = job.name
            deleteBtn.setOnClickListener {
                listener.showDeleteDialog(job = job)
            }
            jobCardView.setOnClickListener {
                listener.navigateToJobViewFragment(job = job)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobListAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.job_card_layout, parent, false)
        return JobListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobListAdapterViewHolder, position: Int) {
        holder.bind(jobs[position])
    }

    override fun getItemCount(): Int = jobs.size

    fun setJobListData(newJobs: List<Job>) {
        jobs.clear()
        jobs.addAll(newJobs)
        notifyDataSetChanged()
    }
}