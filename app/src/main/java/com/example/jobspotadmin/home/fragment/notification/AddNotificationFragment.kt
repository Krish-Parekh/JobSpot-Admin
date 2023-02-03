package com.example.jobspotadmin.home.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.databinding.FragmentAddNotificationBinding
import com.example.jobspotadmin.home.fragment.notification.viewmodel.NotificationViewModel
import com.example.jobspotadmin.model.BroadcastNotification
import com.example.jobspotadmin.util.*
import com.example.jobspotadmin.util.Status.LOADING
import com.example.jobspotadmin.util.Status.SUCCESS
import com.example.jobspotadmin.util.Status.ERROR


class AddNotificationFragment : Fragment() {
    private var _binding: FragmentAddNotificationBinding? = null
    private val binding get() = _binding!!

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    private val notificationViewModel by viewModels<NotificationViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNotificationBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            etTitleContainer.addTextWatcher()
            etMessageContainer.addTextWatcher()

            binding.btnSubmit.setOnClickListener {
                val title = etTitle.getInputValue()
                val message = etMessage.getInputValue()

                if (detailVerification(title, message)) {
                    val notification = BroadcastNotification(title = title, body = message)
                    notificationViewModel.broadcastNotification(notification)
                }
            }
        }
    }

    private fun setupObserver() {
        notificationViewModel.uploadStatus.observe(viewLifecycleOwner) { notificationState ->
            when(notificationState.status) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    loadingDialog.dismiss()
                    val successMessage = notificationState.data!!
                    showToast(requireContext(), successMessage)
                }
                ERROR -> {
                    loadingDialog.dismiss()
                    val errorMessage = notificationState.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }
    }

    private fun detailVerification(
        title: String,
        message: String
    ): Boolean {

        val (isNotificationTitleValid, notificationTitleError) = InputValidation.isNotificationTitleValid(title)
        if (isNotificationTitleValid.not()) {
            binding.etTitleContainer.error = notificationTitleError
            return isNotificationTitleValid
        }

        val (isNotificationBodyValid, notificationBodyError) = InputValidation.isNotificationBodyValid(message)
        if (isNotificationBodyValid.not()) {
            binding.etMessageContainer.error = notificationBodyError
            return isNotificationBodyValid
        }

        return true
    }
}