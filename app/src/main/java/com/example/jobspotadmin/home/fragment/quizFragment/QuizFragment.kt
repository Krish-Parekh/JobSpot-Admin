package com.example.jobspotadmin.home.fragment.quizFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentQuizBinding
import com.example.jobspotadmin.model.Job
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton


class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizBinding.inflate(inflater, container, false)

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        binding.apply {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAddQuiz.setOnClickListener {
                findNavController().navigate(R.id.action_quizFragment_to_createQuizFragment)
            }
        }
    }

    fun showDeleteDialog(job: Job) {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_file, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnRemoveFile)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }
}