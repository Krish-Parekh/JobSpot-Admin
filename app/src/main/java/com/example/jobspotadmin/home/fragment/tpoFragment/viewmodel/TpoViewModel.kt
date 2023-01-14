package com.example.jobspotadmin.home.fragment.tpoFragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.Tpo
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_ROLE
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_TPO
import com.example.jobspotadmin.util.Constants.Companion.TPO_IMAGE_STORAGE_PATH
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TpoViewModel : ViewModel() {

    private val mFirestore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mStorage : StorageReference by lazy { FirebaseStorage.getInstance().reference }
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

    fun deleteTpo(tpo: Tpo){
        viewModelScope.launch {
            val tpoId = tpo.uid
            val tpoImagePath = "$TPO_IMAGE_STORAGE_PATH/$tpoId"
            mFirestore.collection(COLLECTION_PATH_TPO).document(tpoId).delete().await()
            mFirestore.collection(COLLECTION_PATH_ROLE).document(tpoId).delete().await()
            mStorage.child(tpoImagePath).delete().await()
        }
    }

}