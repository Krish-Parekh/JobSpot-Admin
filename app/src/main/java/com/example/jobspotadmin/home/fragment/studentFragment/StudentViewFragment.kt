package com.example.jobspotadmin.home.fragment.studentFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentStudentViewBinding


class StudentViewFragment : Fragment() {
    private var _binding : FragmentStudentViewBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<StudentViewFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentViewBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            tvUsername.text = args.student.details?.username
            tvUserEmail.text = args.student.details?.email
            profileImage.load(args.student.details?.imageUrl)
            tvSapId.text = args.student.details?.sapId
            tvMobile.text = args.student.details?.mobile
            tvDob.text = args.student.details?.dob
            tvAvgSGPI.text = args.student.academic?.avgScore
            tvAddress.text = getString(R.string.student_address, args.student.address?.address, args.student.address?.city, args.student.address?.zipCode)
            layoutUploadedPdf.tvFileName.text = args.fileName
            layoutUploadedPdf.tvFileMetaData.text = args.fileMetaData

            layoutUploadedPdf.root.setOnClickListener {
                setPdfIntent(Uri.parse(args.student.academic?.resumeUrl))
            }
        }
    }

    private fun setPdfIntent(pdfUri: Uri) {
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(pdfUri, "application/pdf")
        startActivity(pdfIntent)
    }
}