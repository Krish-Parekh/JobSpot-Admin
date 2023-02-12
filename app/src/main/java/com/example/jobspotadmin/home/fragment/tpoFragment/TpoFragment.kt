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
import com.example.jobspotadmin.R
import com.example.jobspotadmin.databinding.FragmentTpoBinding
import com.example.jobspotadmin.home.fragment.tpoFragment.adapter.TpoAdapter
import com.example.jobspotadmin.home.fragment.tpoFragment.viewmodel.TpoViewModel
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.model.Tpo
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton


class TpoFragment : Fragment() {
    private var _binding: FragmentTpoBinding? = null
    private val binding get() = _binding!!

    private var _tpoAdapter : TpoAdapter? = null
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
        with(binding) {
            tpoViewModel.fetchTpo()
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
        tpoViewModel.tpoList.observe(viewLifecycleOwner){
            tpoAdapter.setData(it)
            tpos.clear()
            tpos.addAll(it)
        }
    }

    fun deleteTpo(tpo: Tpo){
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_delete_tpo, null)
        val btnNot: MaterialButton = bottomSheet.findViewById(R.id.btnNo)
        val btnRemove: MaterialButton = bottomSheet.findViewById(R.id.btnRemoveTpo)
        btnNot.setOnClickListener {
            dialog.dismiss()
        }
        btnRemove.setOnClickListener {
            tpoViewModel.deleteTpo(tpo)
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }

    fun navigateToTpoView(tpo: Tpo) {
        val directions = TpoFragmentDirections.actionTpoFragmentToTpoViewFragment(tpo)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}