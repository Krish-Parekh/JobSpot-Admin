package com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChipsViewModel : ViewModel() {

    private val _chips = MutableLiveData<MutableList<String>>(mutableListOf())
    val chips: LiveData<MutableList<String>> = _chips

    fun addChip(chip: String) {
        _chips.value?.let { chips ->
            chips.add(chip)
            _chips.postValue(chips)
        }
    }

    fun addChipsList(chips : List<String>){
        _chips.postValue(chips.toMutableList())
    }
    fun removeChip(chip: String) {
        val newChips = _chips.value?.filter { it != chip }
        _chips.postValue(newChips?.toMutableList())
    }

}