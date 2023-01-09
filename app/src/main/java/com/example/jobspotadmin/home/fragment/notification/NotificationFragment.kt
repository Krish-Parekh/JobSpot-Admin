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
import com.example.jobspotadmin.model.BroadcastNotification
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton


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
        _notificationAdapter = NotificationAdapter(this@NotificationFragment)

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

    fun deleteNotification(notification : BroadcastNotification){
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_notification, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnRemoveNotification)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}