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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "JobsViewModelTAG"

class JobsViewModel : ViewModel() {

    private var imageUri: Uri? = null
    private val mFireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mFireStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val _uploadDataStatus: MutableLiveData<UiState> = MutableLiveData(UiState())
    val uploadDataStatus: LiveData<UiState> = _uploadDataStatus


    fun setImageUri(imageUri: Uri?) {
        this@JobsViewModel.imageUri = imageUri
    }

    fun getImageUri(): Uri? {
        return this@JobsViewModel.imageUri
    }

    fun uploadData(imageUri: Uri, job: Job) {
        viewModelScope.launch {
            try {
                _uploadDataStatus.postValue(
                    UiState(loading = true)
                )
                val uid = job.uid
                val storageRef = mFireStorage.reference.child(COMPANY_IMAGE_STORAGE_PATH + uid)
                storageRef.putFile(imageUri).await()
                val imageUrl = storageRef.downloadUrl.await().toString()
                job.imageUrl = imageUrl
                Log.d(TAG, "Company image upload success")

                mFireStore.collection(COLLECTION_PATH_COMPANY).document(uid).set(job).await()
                Log.d(TAG, "Company data upload success")

                _uploadDataStatus.postValue(
                    UiState(success = true)
                )
            } catch (error: Exception) {
                Log.d(TAG, "Exception : ${error.message}")
                _uploadDataStatus.postValue(
                    UiState(
                        failed = true
                    )
                )
            }
        }
    }
}