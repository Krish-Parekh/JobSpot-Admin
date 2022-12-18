package com.example.jobspotadmin.home.fragment.jobsFragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentJobViewBinding
import com.google.android.material.chip.Chip

private const val TAG = "JobViewFragment"

class JobViewFragment : Fragment() {
    private lateinit var binding: FragmentJobViewBinding
    private val args by navArgs<JobViewFragmentArgs>()
    private val job by lazy { args.job }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobViewBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView() {
        binding.apply {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            ivDeleteJob.setOnClickListener {
                Log.d(TAG, "Delete Job")
            }

            ivCompanyLogo.load(job.imageUrl)

            tvRole.text = job.role
            tvCompanyLocation.text = getString(R.string.field_company_and_location, job.name, job.city)
            tvJobDescription.text = job.description
            tvResponsibility.text = job.responsibility
            tvSalary.text = createSalaryText(job.salary)

            job.skillSet.forEach { job ->
                val chip = Chip(requireContext())
                chip.text = job
                chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_background_color)
                chip.setTextColor(requireContext().getColor(R.color.chip_text_color))
                chip.chipCornerRadius = 8f
                requiredSkillSetChipGroup.addView(chip)
            }
        }
    }

    private fun createSalaryText(salary: String): SpannableString {
        val salaryText = SpannableString("₹$salary/year")
        val orangeColor = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val greyColor = ContextCompat.getColor(requireActivity(), R.color.grey)
        val salaryColor = ForegroundColorSpan(orangeColor)
        val durationColor = ForegroundColorSpan(greyColor)
        salaryText.setSpan(salaryColor, 0, salaryText.length - 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        salaryText.setSpan(durationColor, salaryText.length - 5, salaryText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return salaryText
    }
}