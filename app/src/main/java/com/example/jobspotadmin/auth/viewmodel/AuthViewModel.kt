package com.example.jobspotadmin.auth.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.Tpo
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_TPO
import com.example.jobspotadmin.util.Constants.Companion.TPO_IMAGE_STORAGE_PATH
import com.example.jobspotadmin.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private const val TAG = "AuthViewModel"

class AuthViewModel : ViewModel() {
    private var imageUri: Uri? = null;
    private val mFireStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val mFireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mFirebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _uploadDataStatus: MutableLiveData<UiState> = MutableLiveData(UiState.LOADING)
    val uploadDataStatus: LiveData<UiState> = _uploadDataStatus

    fun setImageUri(imageUri: Uri?) {
        this.imageUri = imageUri
    }

    fun getImageUri(): Uri? {
        return this.imageUri
    }

    fun uploadData(imageUri: Uri, tpo: Tpo) {
        viewModelScope.launch {
            try {
                _uploadDataStatus.postValue(UiState.LOADING)
                val profileUpdates =
                    UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build()
                val currentUser = mFirebaseAuth.currentUser!!
                currentUser.updateProfile(profileUpdates).await()

                val userId = tpo.uid
                val imageName = tpo.uid
                val storageRef = mFireStorage.reference.child(TPO_IMAGE_STORAGE_PATH + imageName)
                storageRef.putFile(imageUri).await()
                val imageUrl = storageRef.downloadUrl.await().toString()
                tpo.imageUri = imageUrl
                Log.d(TAG, "Image Uploaded Success")


                mFireStore.collection(COLLECTION_PATH_TPO).document(userId).set(tpo).await()
                Log.d(TAG, "Data Uploaded Success")

                _uploadDataStatus.postValue(UiState.SUCCESS)
            } catch (error: Exception) {
                Log.d(TAG, "Exception : ${error.message}")
                _uploadDataStatus.postValue(UiState.FAILURE)
            }
        }
    }
}