package com.example.jobspotadmin.home.fragment.jobsFragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.BottomSheetDeleteJobBinding
import com.example.jobspotadmin.databinding.FragmentJobViewBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.LoadingDialog
import com.example.jobspotadmin.util.Status
import com.example.jobspotadmin.util.convertToShortString
import com.example.jobspotadmin.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip

private const val TAG = "JobViewFragment"

class JobViewFragment : Fragment() {
    private var _binding: FragmentJobViewBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<JobViewFragmentArgs>()
    private val jobsViewModel : JobsViewModel by viewModels()
    private val loadingDialog : LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val job by lazy { args.job }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJobViewBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            ivDeleteJob.setOnClickListener {
                deleteJobDialog(job = job)
            }

            ivEditJob.setOnClickListener {
                val direction = JobViewFragmentDirections.actionJobViewFragmentToJobEditFragment(job = job)
                findNavController().navigate(direction)
            }

            cvStudentApplied.setOnClickListener {
                val direction = JobViewFragmentDirections.actionJobViewFragmentToStudentJobFragment(jobId = job.uid)
                findNavController().navigate(direction)
            }

            ivCompanyLogo.load(job.imageUrl)

            tvRole.text = job.role
            tvCompanyLocation.text = getString(R.string.field_company_and_location, job.name, job.city)
            tvJobDescription.text = job.description
            tvResponsibility.text = job.responsibility
            tvSalary.text = createSalaryText(job.salary)

            job.skillSet.forEach { job ->
                createSkillSetChip(job)
            }
        }
    }

    private fun setupObserver() {
        jobsViewModel.deleteJobStatus.observe(viewLifecycleOwner){ deleteState ->
            when(deleteState.status){
                Status.LOADING -> {
                    loadingDialog.show()
                }
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    val successMessage = deleteState.data!!
                    showToast(requireContext(), successMessage)
                }
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    val errorMessage = deleteState.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }
    }

    private fun createSkillSetChip(job: String) {
        val chip = Chip(requireContext())
        chip.text = job
        chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_background_color)
        chip.setTextColor(requireContext().getColor(R.color.chip_text_color))
        chip.chipCornerRadius = 8f
        binding.requiredSkillSetChipGroup.addView(chip)
    }

    private fun createSalaryText(salary: String): SpannableString {
        val shortSalary = convertToShortString(salary.toLong())
        val salaryText = SpannableString("₹$shortSalary/year")
        val orangeColor = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val greyColor = ContextCompat.getColor(requireActivity(), R.color.grey)
        val salaryColor = ForegroundColorSpan(orangeColor)
        val durationColor = ForegroundColorSpan(greyColor)
        salaryText.setSpan(salaryColor, 0, salaryText.length - 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        salaryText.setSpan(durationColor, salaryText.length - 5, salaryText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return salaryText
    }

    fun deleteJobDialog(job: Job) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val jobDeleteSheetBinding = BottomSheetDeleteJobBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(jobDeleteSheetBinding.root)
        jobDeleteSheetBinding.apply {
            btnNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            btnRemoveJob.setOnClickListener {
                jobsViewModel.deleteJob(job)
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}