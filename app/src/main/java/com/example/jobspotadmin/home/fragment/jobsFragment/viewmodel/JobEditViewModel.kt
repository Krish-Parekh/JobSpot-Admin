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
import com.example.jobspotadmin.util.Resource
import com.example.jobspotadmin.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "JobEditViewModelTAG"
class JobEditViewModel : ViewModel() {

    private var imageUri: Uri? = null
    fun setImageUri(imageUri: Uri?) {
        this.imageUri = imageUri
    }

    fun getImageUri(): Uri? {
        return this.imageUri
    }

    private val mFirestore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mStorage : StorageReference by lazy { FirebaseStorage.getInstance().reference }

    private val _editJobStatus : MutableLiveData<Resource<String>> = MutableLiveData()
    val editJobStatus : LiveData<Resource<String>> = _editJobStatus

    fun editJob(job: Job) {
        viewModelScope.launch(IO) {
            try {
                _editJobStatus.postValue(Resource.loading())
                val jobId = job.uid
                val firebaseImagePrefix = "https://firebasestorage.googleapis.com/"
                val jobImagePath = "$COMPANY_IMAGE_STORAGE_PATH/$jobId"
                if (job.imageUrl.startsWith(firebaseImagePrefix).not()){
                    val jobImageRef = mStorage.child(jobImagePath)
                    jobImageRef.putFile(Uri.parse(job.imageUrl)).await()
                    val imageUri = jobImageRef.downloadUrl.await()
                    job.imageUrl = imageUri.toString()
                }
                val jobRef = mFirestore.collection(COLLECTION_PATH_COMPANY).document(jobId)
                jobRef.set(job).await()
                _editJobStatus.postValue(Resource.success("Job edit success."))
            } catch (error : Exception) {
                val errorMessage = error.message!!
                _editJobStatus.postValue(Resource.error(errorMessage))
            }
        }
    }

}