package com.example.jobspotadmin.home.fragment.tpoFragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jobspotadmin.model.Tpo
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_TPO
import com.google.firebase.firestore.FirebaseFirestore

class TpoViewModel : ViewModel() {

    private val mFirestore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _tpoList : MutableLiveData<List<Tpo>> = MutableLiveData(emptyList())
    val tpoList : LiveData<List<Tpo>> = _tpoList

    fun fetchTpo(){
        val tpoRef = mFirestore.collection(COLLECTION_PATH_TPO)
        tpoRef.addSnapshotListener { value, error ->
            if (error != null){
                return@addSnapshotListener
            }
            val documents = value?.documents!!
            val tpoList = documents.map {
                it.toObject(Tpo::class.java)!!
            }
            _tpoList.postValue(tpoList)
        }
    }

}