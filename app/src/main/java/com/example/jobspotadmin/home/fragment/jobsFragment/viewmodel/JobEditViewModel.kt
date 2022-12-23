package com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.example.jobspotadmin.util.Constants.Companion.COMPANY_IMAGE_STORAGE_PATH
import com.example.jobspotadmin.util.UiState
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "JobEditViewModelTAG"
class JobEditViewModel : ViewModel() {

    private val mFirestore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mFirebaseStorage : FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val _editOperationStatus : MutableLiveData<UiState> = MutableLiveData(UiState.LOADING)
    val editOperationStatus : LiveData<UiState> = _editOperationStatus

    fun editJob(job : Job){
        viewModelScope.launch {
            try {
                _editOperationStatus.postValue(UiState.LOADING)
                val jobId = job.uid
                Log.d(TAG, "Jobs : ${job}")
                if (!job.imageUrl.startsWith("https://firebasestorage.googleapis.com/")){
                    val editJobStorageRef = mFirebaseStorage.getReference(COMPANY_IMAGE_STORAGE_PATH).child(jobId)
                    editJobStorageRef.putFile(Uri.parse(job.imageUrl)).await()
                    job.imageUrl = editJobStorageRef.downloadUrl.await().toString()
                    Log.d(TAG, "It's a firebase image : ${job.imageUrl}")
                }

                val editJobRef = mFirestore.collection(COLLECTION_PATH_COMPANY).document(jobId)
                editJobRef.set(job).await()

                _editOperationStatus.postValue(UiState.SUCCESS)
            }catch (e : Exception){
                _editOperationStatus.postValue(UiState.FAILURE)
                Log.d(TAG, "Error : ${e.message}")
            }
        }
    }
}