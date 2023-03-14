package com.example.jobspotadmin.home.fragment.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentNotificationBinding
import com.example.jobspotadmin.home.fragment.notification.adapter.NotificationAdapter
import com.example.jobspotadmin.home.fragment.notification.viewmodel.NotificationViewModel
import com.example.jobspotadmin.model.BroadcastNotification
import com.example.jobspotadmin.util.LoadingDialog
import com.example.jobspotadmin.util.Status
import com.example.jobspotadmin.util.Status.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter

private const val TAG = "SOME_VALUE"

class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private var _notificationAdapter: NotificationAdapter? = null
    private val notificationAdapter get() = _notificationAdapter!!

    private val notificationViewModel by viewModels<NotificationViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
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
        with(binding) {

            lifecycleScope.launchWhenStarted {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    notificationViewModel.fetchNotifications()
                }
            }

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAddNotification.setOnClickListener {
                findNavController().navigate(R.id.action_notificationFragment_to_addNotificationFragment)
            }

            val alphaAdapter = AlphaInAnimationAdapter(notificationAdapter)
            rvNotification.adapter = ScaleInAnimationAdapter(alphaAdapter).apply {
                setDuration(1000)
                setHasStableIds(false)
                setFirstOnly(false)
                setInterpolator(OvershootInterpolator(.100f))
            }
            rvNotification.layoutManager = LinearLayoutManager(requireContext())

        }
    }

    private fun setupObserver() {
        notificationViewModel.notification.observe(viewLifecycleOwner) {
            notificationAdapter.setData(it)
        }

        notificationViewModel.deleteStatus.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    notificationViewModel.fetchNotifications()
                    loadingDialog.dismiss()
                }
                ERROR -> {
                    loadingDialog.dismiss() 
                }
            }
        }
    }

    fun deleteNotification(notification: BroadcastNotification) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_notification, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnRemoveNotification)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            notificationViewModel.deleteNotification(notification)
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