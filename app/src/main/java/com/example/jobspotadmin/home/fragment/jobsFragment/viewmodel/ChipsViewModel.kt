package com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChipsViewModel : ViewModel() {

    private val _chips = MutableLiveData<List<String>>(emptyList())
    val chips: LiveData<List<String>> = _chips

    fun addChip(chip: String) {
        val newChips = _chips.value?.toMutableList() ?: mutableListOf()
        newChips.add(chip)
        _chips.postValue(newChips)
    }

    fun addChipsList(chips : List<String>){
        _chips.postValue(chips)
    }
    fun removeChip(chip: String) {
        val newChips = _chips.value?.filter { it != chip }
        _chips.postValue(newChips?.toMutableList())
    }

}