package com.example.jobspotadmin.home.fragment.notification.viewmodel

import android.app.Notification
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.BroadcastNotification
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_NOTIFICATION
import com.example.jobspotadmin.util.Resource
import com.example.jobspotadmin.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "NotificationViewModelTAG"

class NotificationViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _uploadStatus: MutableLiveData<Resource<String>> = MutableLiveData()
    val uploadStatus: LiveData<Resource<String>> = _uploadStatus

    private val _notification: MutableLiveData<List<BroadcastNotification>> =
        MutableLiveData(mutableListOf())
    val notification: LiveData<List<BroadcastNotification>> = _notification

    fun fetchNotifications() {
        try {
            viewModelScope.launch {
                mFirestore.collection(COLLECTION_PATH_NOTIFICATION)
                    .whereEqualTo("type", "BROADCAST")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        val documents = value?.documents!!
                        val notificationList = documents.map {
                            it.toObject(BroadcastNotification::class.java)!!
                        }
                        _notification.postValue(notificationList)
                    }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
        }
    }

    fun broadcastNotification(notification: BroadcastNotification) {
        try {
            viewModelScope.launch {
                _uploadStatus.postValue(Resource.loading())
                mFirestore
                    .collection(COLLECTION_PATH_NOTIFICATION)
                    .document(notification.id)
                    .set(notification)
                    .await()
                _uploadStatus.postValue(Resource.success("Notification upload success."))
            }
        } catch (error: Exception) {
            val errorMessage = error.message!!
            _uploadStatus.postValue(Resource.error(errorMessage))
        }
    }

    fun deleteNotification(notification: BroadcastNotification) {
        viewModelScope.launch {
            val notificationId = notification.id
            mFirestore.collection(COLLECTION_PATH_NOTIFICATION).document(notificationId).delete().await()
        }
    }
}