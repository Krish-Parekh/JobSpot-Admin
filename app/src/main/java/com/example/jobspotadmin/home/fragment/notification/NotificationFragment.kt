package com.example.jobspotadmin.home.fragment.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentNotificationBinding
import com.example.jobspotadmin.home.fragment.notification.adapter.NotificationAdapter
import com.example.jobspotadmin.home.fragment.notification.viewmodel.NotificationViewModel


class NotificationFragment : Fragment() {
    private var _binding : FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private var _notificationAdapter : NotificationAdapter? = null
    private val notificationAdapter get() = _notificationAdapter!!

    private val notificationViewModel by viewModels<NotificationViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        _notificationAdapter = NotificationAdapter()

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            notificationViewModel.fetchNotifications()

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAddNotification.setOnClickListener {
                findNavController().navigate(R.id.action_notificationFragment_to_addNotificationFragment)
            }

            rvNotification.adapter = notificationAdapter
            rvNotification.layoutManager = LinearLayoutManager(requireContext())

        }
    }

    private fun setupObserver() {
        notificationViewModel.notification.observe(viewLifecycleOwner){
            notificationAdapter.setData(it)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}