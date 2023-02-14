package com.example.jobspotadmin.home.fragment.tpoFragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobspotadmin.databinding.BottomSheetDeleteTpoBinding
import com.example.jobspotadmin.databinding.FragmentTpoBinding
import com.example.jobspotadmin.home.fragment.tpoFragment.adapter.TpoAdapter
import com.example.jobspotadmin.home.fragment.tpoFragment.viewmodel.TpoViewModel
import com.example.jobspotadmin.model.Tpo
import com.google.android.material.bottomsheet.BottomSheetDialog


class TpoFragment : Fragment() {
    private var _binding: FragmentTpoBinding? = null
    private val binding get() = _binding!!
    private var _tpoAdapter: TpoAdapter? = null
    private val tpoAdapter get() = _tpoAdapter!!
    private val tpoViewModel by viewModels<TpoViewModel>()
    private val tpos: MutableList<Tpo> by lazy { mutableListOf() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTpoBinding.inflate(inflater, container, false)
        _tpoAdapter = TpoAdapter(this@TpoFragment)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        tpoViewModel.fetchTpo()
        with(binding) {
            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            etSearch.addTextChangedListener { text: Editable? ->
                filterTpo(text)
            }

            rvPlacementOfficer.adapter = tpoAdapter
            rvPlacementOfficer.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun filterTpo(text: Editable?) {
        if (text.isNullOrEmpty().not()) {
            val filteredJobList = tpos.filter { tpo ->
                val title = tpo.username.lowercase()
                val inputText = text.toString().lowercase()
                title.contains(inputText)
            }
            tpoAdapter.setData(newTpo = filteredJobList)
        } else {
            tpoAdapter.setData(newTpo = tpos)
        }
    }

    private fun setupObserver() {
        tpoViewModel.tpoList.observe(viewLifecycleOwner) { tpoList ->
            tpoAdapter.setData(tpoList)
            tpos.clear()
            tpos.addAll(tpoList)
        }
    }

    fun deleteTpo(tpo: Tpo) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val tpoDeleteSheetBinding = BottomSheetDeleteTpoBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(tpoDeleteSheetBinding.root)
        with(tpoDeleteSheetBinding) {
            btnNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            btnRemoveTpo.setOnClickListener {
                tpoViewModel.deleteTpo(tpo)
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    fun navigateToTpoView(tpo: Tpo) {
        val directions = TpoFragmentDirections.actionTpoFragmentToTpoViewFragment(tpo)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        _binding = null
        _tpoAdapter = null
        super.onDestroyView()
    }

}