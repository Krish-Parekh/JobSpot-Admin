package com.example.jobspotadmin.home.fragment.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobspotadmin.databinding.NotificationCardLayoutBinding
import com.example.jobspotadmin.home.fragment.notification.NotificationFragment
import com.example.jobspotadmin.model.BroadcastNotification
import com.example.jobspotadmin.util.convertTimeStamp

class NotificationAdapter(private val listener: NotificationFragment) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val notifications: MutableList<BroadcastNotification> = mutableListOf()

    inner class NotificationViewHolder(
        private val binding: NotificationCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: BroadcastNotification) {
            with(binding) {
                tvNotificationTitle.text = notification.title
                tvMessage.text = notification.body
                tvTimestamp.text = convertTimeStamp(notification.timestamp.toDate())
                ivDelete.setOnClickListener {
                    listener.deleteNotification(notification = notification)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = NotificationCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size

    fun setData(newNotificationList: List<BroadcastNotification>) {
        notifications.clear()
        notifications.addAll(newNotificationList)
        notifyDataSetChanged()
    }

}