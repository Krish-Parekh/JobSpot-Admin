package com.example.jobspotadmin.home.fragment.tpoFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.jobspotadmin.databinding.FragmentTpoViewBinding


class TpoViewFragment : Fragment() {
    private var _binding: FragmentTpoViewBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<TpoViewFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTpoViewBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            val tpo = args.tpo
            tvUsername.text = tpo.username
            tvUserEmail.text = tpo.email
            profileImage.load(tpo.imageUri)
            tvMobile.text = tpo.mobile
            tvGender.text = tpo.gender
            tvQualification.text = tpo.qualification
            tvStream.text = tpo.stream
            tvDob.text = tpo.dob
            tvExperience.text = tpo.experience
            tvBio.text = tpo.biography
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}