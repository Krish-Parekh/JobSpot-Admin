package com.example.jobspotadmin.home.fragment.jobsFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.jobspotadmin.databinding.FragmentJobDetailTwoBinding
import com.example.jobspotadmin.util.*

private const val TAG = "JobDetailFragmentTwo"

class JobDetailFragmentTwo : Fragment() {
    private lateinit var binding: FragmentJobDetailTwoBinding
    private val args by navArgs<JobDetailFragmentTwoArgs>()
    private val skillList: MutableList<String> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJobDetailTwoBinding.inflate(inflater, container, false)

        setupView()

        return binding.root
    }

    private fun setupView() {
        binding.apply {

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            etSkills.getSkillList(requireContext(), skillChipGroup) { skillName ->
                skillList.add(skillName)
            }

            etJobRespContainer.addTextWatcher()

            btnSave.setOnClickListener {
                val responsibilities = etJobResp.getInputValue()
                if (detailVerification(responsibilities, skillList)) {
                    args.job.responsibilities = responsibilities
                    args.job.skillSet = skillList
                    Log.d(TAG, "Jobs : ${args.job}")
                }
            }
        }
    }

    private fun detailVerification(
        responsibility: String,
        skillList: MutableList<String>
    ): Boolean {
        binding.apply {
            if (!InputValidation.checkNullity(responsibility)) {
                etJobRespContainer.error = "Enter valid responsibility"
                return false
            } else if (skillList.isEmpty()) {
                showToast(requireContext(), "Enter skills")
                return false
            } else {
                return true
            }
        }
    }
}