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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentJobViewBinding
import com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel.JobsViewModel
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.LoadingDialog
import com.example.jobspotadmin.util.UiState
import com.example.jobspotadmin.util.convertToShortString
import com.example.jobspotadmin.util.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
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

        setupView()

        return binding.root
    }

    private fun setupView() {
        binding.apply {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            ivDeleteJob.setOnClickListener {
                showDeleteDialog(job = job)
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

    private fun showDeleteDialog(job: Job) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_job, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnRemoveFile)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            jobsViewModel.deleteJob(job = job)
            handleDeleteResponse()
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }

    private fun handleDeleteResponse() {
        jobsViewModel.operationStatus.observe(viewLifecycleOwner, Observer { uiState ->
            when (uiState) {
                UiState.LOADING -> {
                    loadingDialog.show()
                }
                UiState.SUCCESS -> {
                    showToast(requireContext(), "Job delete success")
                    loadingDialog.dismiss()
                    findNavController().popBackStack()
                }
                UiState.FAILURE -> {
                    showToast(requireContext(), "Error while deleting job")
                    loadingDialog.dismiss()
                }
                else -> Unit
            }
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}