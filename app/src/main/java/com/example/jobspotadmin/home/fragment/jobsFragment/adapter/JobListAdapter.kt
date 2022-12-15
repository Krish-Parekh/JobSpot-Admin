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
import com.example.jobspotadmin.home.fragment.jobsFragment.JobsFragment
import com.example.jobspotadmin.model.Job

private const val TAG = "JobAdapterTAG"

class JobListAdapter(private val listener : JobsFragment) : RecyclerView.Adapter<JobListAdapter.JobListAdapterViewHolder>() {

    private var jobs: MutableList<Job> = mutableListOf()

    inner class JobListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.ivCompanyLogo)
        val role: TextView = itemView.findViewById(R.id.tvJobRole)
        val name: TextView = itemView.findViewById(R.id.tvCompanyName)
        val delete: ImageView = itemView.findViewById(R.id.ivDeleteJob)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobListAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.job_card_layout, parent, false)
        return JobListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobListAdapterViewHolder, position: Int) {
        val job = jobs[position]
        holder.apply {
            logo.load(job.imageUrl){
                error(R.drawable.ic_apple_logo)
                placeholder(R.drawable.ic_jobs)
                build()
            }
            role.text = job.title
            name.text = job.name
            delete.setOnClickListener {
                listener.showDeleteDialog()
            }
        }
    }

    override fun getItemCount(): Int = jobs.size

    fun setJobListData(newJobs: List<Job>) {
        jobs.clear()
        jobs.addAll(newJobs)
        notifyDataSetChanged()
    }
}