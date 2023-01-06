package com.example.jobspotadmin.home.fragment.homeFragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_MOCK
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_NOTIFICATION
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Counts(
    var studentCount: Int = 0,
    var jobCount: Int = 0,
    var mockCount: Int = 0,
    var notificationCount : Int = 0
)

private const val TAG = "HomeViewModelTAG"

class HomeViewModel : ViewModel() {

    init {
        fetchCounts()
    }

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val _metaCounts: MutableLiveData<Counts> = MutableLiveData()
    val metaCounts: LiveData<Counts> = _metaCounts

    private fun fetchCounts() {
        viewModelScope.launch(Dispatchers.IO) {
            val studentCount = mFirestore.collection(COLLECTION_PATH_STUDENT).get().await().count()
            val jobCount = mFirestore.collection(COLLECTION_PATH_COMPANY).get().await().count()
            val mockCount = mFirestore.collection(COLLECTION_PATH_MOCK).get().await().count()
            val notificationCount = mFirestore.collection(COLLECTION_PATH_NOTIFICATION).get().await().count()
            val count = Counts(studentCount, jobCount, mockCount, notificationCount)
            _metaCounts.postValue(count)
        }
    }
}