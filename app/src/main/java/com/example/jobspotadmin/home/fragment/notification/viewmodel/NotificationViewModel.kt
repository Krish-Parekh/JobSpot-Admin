package com.example.jobspotadmin.home.fragment.notification.viewmodel

import android.app.Notification
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.BroadcastNotification
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_NOTIFICATION
import com.example.jobspotadmin.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "NotificationViewModelTAG"
class NotificationViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _uploadStatus: MutableLiveData<UiState> = MutableLiveData(UiState.LOADING)
    val uploadStatus: LiveData<UiState> = _uploadStatus

    private val _notification : MutableLiveData<List<BroadcastNotification>> = MutableLiveData(mutableListOf())
    val notification : LiveData<List<BroadcastNotification>> = _notification

    fun fetchNotifications() {
        try {
            viewModelScope.launch {

                mFirestore.collection(COLLECTION_PATH_NOTIFICATION)
                    .whereEqualTo("type", "BROADCAST")
                    .addSnapshotListener { value, error ->
                        if (error != null){
                            return@addSnapshotListener
                        }
                        val documents = value?.documents!!
                        val notificationList = documents.map {
                            it.toObject(BroadcastNotification::class.java)!!
                        }
                        val latestNotification = notificationList.sortedByDescending { it.timestamp }
                        _notification.postValue(latestNotification)
                    }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
        }
    }

    fun broadcastNotification(notification: BroadcastNotification) {
        try {
            viewModelScope.launch {
                _uploadStatus.postValue(UiState.LOADING)
                mFirestore
                    .collection(COLLECTION_PATH_NOTIFICATION)
                    .document(notification.id)
                    .set(notification)
                    .await()
                _uploadStatus.postValue(UiState.SUCCESS)
            }
        } catch (e: Exception) {
            _uploadStatus.postValue(UiState.FAILURE)
        }
    }
}