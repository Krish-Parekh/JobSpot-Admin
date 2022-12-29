package com.example.jobspotadmin.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentEmailBinding


class EmailFragment : Fragment() {

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView() {

        binding.apply {

            tvEmailResend.text = createResendText()

            btnBackToLogin.setOnClickListener {
                findNavController().popBackStack(R.id.loginFragment, false)
            }

            btnOpenEmail.setOnClickListener {
                val mailIntent = Intent(Intent.ACTION_MAIN)
                mailIntent.addCategory(Intent.CATEGORY_APP_EMAIL)
                mailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity?.startActivity(mailIntent)
            }
        }
    }

    private fun createResendText() : SpannableString {
        val resendText = SpannableString(getString(R.string.email_resend_prompt))
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val resendColor = ForegroundColorSpan(color)

        resendText.setSpan(UnderlineSpan(), 33, resendText.length, 0)
        resendText.setSpan(resendColor, 33, resendText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return resendText
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}