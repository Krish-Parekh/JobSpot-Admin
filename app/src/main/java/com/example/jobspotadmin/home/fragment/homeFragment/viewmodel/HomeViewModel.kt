package com.example.jobspotadmin.home.fragment.homeFragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_MOCK
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_NOTIFICATION
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_TPO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



private const val TAG = "HomeViewModelTAG"

class HomeViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val _metaCounts: MutableLiveData<Counts> = MutableLiveData()
    val metaCounts: LiveData<Counts> = _metaCounts
    private var notificationCountListener : ListenerRegistration? = null
    private var countListener : ListenerRegistration? = null

    fun fetchCounts() {
        viewModelScope.launch(Dispatchers.IO) {
            val tpoCount = getCount(COLLECTION_PATH_TPO)
            val studentCount = getCount(COLLECTION_PATH_STUDENT)
            val jobCount = getCount(COLLECTION_PATH_COMPANY)
            val mockCount = getCount(COLLECTION_PATH_MOCK)
            val notificationCount = getNotificationCount()
            val count = Counts(tpoCount, studentCount, jobCount, mockCount, notificationCount)
            _metaCounts.postValue(count)
        }
    }

    private suspend fun getNotificationCount(): Int {
        val countDeffered = CompletableDeferred<Int>()
        notificationCountListener = mFirestore
            .collection(COLLECTION_PATH_NOTIFICATION)
            .whereEqualTo("type", "BROADCAST")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    countDeffered.completeExceptionally(error)
                    return@addSnapshotListener
                }
                val count = value?.documents?.count()!!
                countDeffered.complete(count)
            }
        return countDeffered.await()
    }

    private suspend fun getCount(collectionPath: String): Int {
        val countDeffered = CompletableDeferred<Int>()
        countListener = mFirestore.collection(collectionPath)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    countDeffered.completeExceptionally(error)
                    return@addSnapshotListener
                }
                val count = value?.documents?.count()!!
                countDeffered.complete(count)
            }
        return countDeffered.await()
    }

    override fun onCleared() {
        notificationCountListener?.remove()
        countListener?.remove()
        super.onCleared()
    }
}

data class Counts(
    var tpoCount: Int = 0,
    var studentCount: Int = 0,
    var jobCount: Int = 0,
    var mockCount: Int = 0,
    var notificationCount: Int = 0
)