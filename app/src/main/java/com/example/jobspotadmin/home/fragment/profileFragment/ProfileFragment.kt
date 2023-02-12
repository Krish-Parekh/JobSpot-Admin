package com.example.jobspotadmin.home.fragment.profileFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.jobspotadmin.R
import com.example.jobspotadmin.auth.AuthActivity
import com.example.jobspotadmin.databinding.FragmentProfileBinding
import com.example.jobspotadmin.home.fragment.profileFragment.viewmodel.ProfileViewModel
import com.example.jobspotadmin.model.Tpo
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "ProfileFragmentTAG"
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val mFirebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val profileViewModel by viewModels<ProfileViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupView()
        return binding.root
    }

    private fun setupView() {
        with(binding) {
            val currentUser = mFirebaseAuth.currentUser!!
            val image = currentUser.photoUrl
            val username = currentUser.displayName
            val email = currentUser.email

            profileImage.load(image)
            tvUsername.text = username
            tvUserEmail.text = email

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            cvManageAccount.setOnClickListener {
                profileViewModel.fetchUser(currentUser.uid)
                setupObserver()
            }

            cvLogout.setOnClickListener {
                logoutBottomSheet()
            }
        }
    }

    private fun setupObserver() {
        profileViewModel.currentUser.observe(viewLifecycleOwner) { tpo ->
            if (tpo.uid.isNotEmpty()) {
                Log.d(TAG, "Tpo : $tpo")
                navigateToUserEdit(tpo)
            }
        }
    }

    private fun navigateToUserEdit(tpo: Tpo) {
        val direction = ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment(tpo = tpo)
        Log.d(TAG, "navigateToUserEdit: $direction")
        findNavController().navigate(direction)
    }

    private fun logoutBottomSheet(){
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_logout, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnLogout)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            dialog.dismiss()
            FirebaseAuth.getInstance().signOut()
            requireActivity().finishAffinity()
            val loginIntent = Intent(requireContext(), AuthActivity::class.java)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(loginIntent)
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}